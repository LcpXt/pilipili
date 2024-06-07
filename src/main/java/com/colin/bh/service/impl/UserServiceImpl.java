package com.colin.bh.service.impl;

import cn.hutool.core.io.IoUtil;
import cn.hutool.crypto.digest.MD5;
import com.colin.bh.bean.HeadImg;
import com.colin.bh.bean.User;
import com.colin.bh.exception.NullFileException;
import com.colin.bh.jedis.UserJedisOperate;
import com.colin.bh.mapper.UserMapper;
import com.colin.bh.service.UserService;
import com.colin.bh.util.EncipherUtil;
import com.colin.bh.util.FileUtils;
import com.colin.bh.util.MailUtils;
import com.colin.bh.util.response.ResponseEntity;
import com.colin.bh.util.response.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 2024年03月23日17:01
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserJedisOperate userJedisOperate;
    @Autowired
    private EncipherUtil encipherUtil;
    @Value("${my.project.param.head-img}")
    private String headImg;
    @Value("${my.project.user.head-img.resource-location}")
    private String headImgResourceLocationPrefix;
    /**
     * 程序访问头像的虚拟路径前缀
     */
    @Value("${my.project.user.head-img.resource-handler}")
    private String headImgResourceHandlerPrefix;

    @Override
    public ResponseEntity<String> checkUsername(String username) {

        Pattern compile = Pattern.compile("^[a-zA-Z0-9]{3,12}$");
        Matcher matcher = compile.matcher(username);
        ResponseEntity<String> responseEntity = new ResponseEntity<>(Status.PATTERN_ERROR, "用户名不合法", null);
        if (!matcher.matches()){
            return responseEntity;
        }
        if ( userMapper.selectIdByUsername(username) == null){
            responseEntity.setStatus(Status.SUCCESS);
            responseEntity.setMessage("用户名可以使用");
            return responseEntity;
        }
        responseEntity.setStatus(Status.USERNAME_EXISTS);
        responseEntity.setMessage("用户名已被使用");
        return responseEntity;
    }

    @Override
    public ResponseEntity<String> sendEmail(String email, Integer flag) {
        // 1. 校验邮箱是否符合邮箱规则
        ResponseEntity<String> responseEntity = new ResponseEntity<>(Status.PATTERN_ERROR, "邮箱不合法", null);
        Pattern compile = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
        Matcher matcher = compile.matcher(email);
        if (matcher.matches()){
            String random = Math.random() + "";
            String randomString = random.substring(2, 8);
            final boolean idExists = userJedisOperate.checkEmailExists(email);
            // 如果邮箱注册过 并且是注册请求 返回邮箱已存在
            if (idExists && flag == 0){
                responseEntity.setStatus(Status.EMAIL_EXISTS);
                responseEntity.setMessage("邮箱已存在");
                return responseEntity;
            }
            //邮箱没注册过，并且是修改密码请求，返回邮箱未注册
            if (!idExists && flag == 1){
                responseEntity.setStatus(Status.ERROR);
                responseEntity.setMessage("邮箱尚未注册");
                return responseEntity;
            }
            MailUtils.sendMail(email, "验证码是 : " + randomString, "验证码");
           if(userJedisOperate.generateCode(email, randomString).equals("success")){
               responseEntity.setStatus(Status.SUCCESS);
               responseEntity.setMessage("验证码生成成功");
           }else {
               responseEntity.setStatus(Status.ERROR);
               responseEntity.setMessage("验证码生成失败");
           }
            return responseEntity;
        }
        return responseEntity;
    }

    @Override
    public ResponseEntity<String> checkCode(String code, String email) {
        return userJedisOperate.checkCode(code, email);
    }

    @Override
    public Boolean doRegister(User user) {
        //加盐加密
        final String originalPassword = user.getPassword();
        final String finalPassword = encipherUtil.doEncipher(originalPassword);
        user.setPassword(finalPassword);
        //补齐缺少属性
        final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        user.setRegisterTime(timestamp);
        user.setUpdateTime(timestamp);
        user.setHeadImg(headImg);
        user.setUserType(0);
        //插入数据表
        if(userMapper.insertUser(user)){
            userJedisOperate.cacheEmail(user.getEmail());
            userJedisOperate.delCache(user.getEmail());
            return true;
        }
        return false;
    }

    @Override
    public boolean changePassword(String email, String password) {
        Boolean updateResult = false;
        Pattern compile = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
        Matcher matcher = compile.matcher(email);


        if (matcher.matches()) {
            String finalPassword = encipherUtil.doEncipher(password);
            updateResult = userMapper.updatePasswordByEmail(email, finalPassword);
        }

        return updateResult;
    }

    @Override
    public User doLogin(String username, String password) {
        User user = null;
        final String finalPassword = encipherUtil.doEncipher(password);
        if ((user = userMapper.selectUserByUsernameAndPassword(username, finalPassword)) != null){
            final Timestamp currentTime = new Timestamp(System.currentTimeMillis());
            userMapper.updateLastLoginTime(currentTime, username);
            return user;
        }
        return user;
    }

    @Override
    public ResponseEntity<String> headImgUpload(MultipartFile multipartFile, HttpSession session) throws IOException, NullFileException {
        User loginUser = (User) session.getAttribute("loginUser");
        String username = loginUser.getUsername();
        //1.接受文件并存储
        //如果把文件存储在服务所在的主机上，一切和文件相关的路径最好都用变量声明
        String originalFilename = multipartFile.getOriginalFilename();
        InputStream inputStream = multipartFile.getInputStream();
        //解决重名问题，每个用户都单独建立一个文件夹，并且在文件名后拼接上时间戳
        File file = new File(headImgResourceLocationPrefix + username);
        if (!file.exists() && !file.isDirectory()){
            file.mkdir();
        }
        //解析文件名test.txt
        //获取文件中最后一个"."的索引
        long currentTime = System.currentTimeMillis();
        String finalFileName = FileUtils.getTimestampFileName(originalFilename, currentTime);
        String suffixName = FileUtils.getSuffixName(originalFilename);
        FileOutputStream fileOutputStream = new FileOutputStream(headImgResourceLocationPrefix + username + "/" +finalFileName);
        //hutool工具包
        IoUtil.copy(inputStream, fileOutputStream);
        inputStream.close();
        fileOutputStream.close();
        //2.用数据表保存文件的相关信息
        HeadImg headImg = new HeadImg();
        headImg.setUser(loginUser);
        headImg.setUploadTime(new Timestamp(currentTime));
        headImg.setOriginalPath(headImgResourceLocationPrefix + username + "/" +finalFileName);
        headImg.setMappingPath(headImgResourceHandlerPrefix + username + "/" +finalFileName);
        //获取文件本身大小
        File file1 = new File(headImgResourceLocationPrefix + username + "/" + finalFileName);
        long length = file1.length();
        headImg.setImgSize(length + "");
        headImg.setImgType(suffixName.substring(1));//0位置是.，1到最后是后缀
        headImg.setOriginalName(finalFileName);
        userMapper.insertHeadImg(headImg);
        //3.返回结果
        return null;
    }
}
