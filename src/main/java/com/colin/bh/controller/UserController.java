package com.colin.bh.controller;

import com.colin.bh.bean.User;
import com.colin.bh.exception.NullFileException;
import com.colin.bh.service.UserService;
import com.colin.bh.util.response.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.Multipart;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;

/**
 * 2024年03月23日16:44
 */
@RequestMapping("/user")
@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("/checkUsername/{username}")
    @ResponseBody
    public ResponseEntity<String> checkUsername(
            @PathVariable("username") String username
    ) {
        return userService.checkUsername(username);
    }

    @RequestMapping("/sendEmail/{email}/{flag}")
    @ResponseBody
    public ResponseEntity<String> sendEmail(
            @PathVariable("email") String email,
            @PathVariable("flag") Integer flag
    ) {
        return userService.sendEmail(email, flag);
    }

    @RequestMapping("/checkCode/{code}/{email}")
    @ResponseBody
    public ResponseEntity<String> checkCode(
            @PathVariable("code") String code,
            @PathVariable("email") String email
    ) {
        return userService.checkCode(code, email);
    }

    @RequestMapping("/doRegister")
    public String doRegister(@Valid User user, Model model) {
        if (!userService.doRegister(user)) {
            model.addAttribute("fail", null);
            return "register";
        } else {
            model.addAttribute("success", true);
            return "register";
        }
    }
    @RequestMapping("/changePassword/{email}")
    public String changePassword(
            @PathVariable("email") String email,
            @RequestParam("password") String password,
            Model model
    ) {
        if (userService.changePassword(email, password)) {
            model.addAttribute("changePasswordSuccess", "true");
        } else {
            model.addAttribute("changePasswordError", "true");
        }
        return "changePassword";
    }

    /**
     * 分布式系统用户登录三个框架
     * spring security
     * shiro
     * sa-token
     * @param username
     * @param password
     * @param session
     * @param model
     * @return
     */
    @RequestMapping("/doLogin")
    public String doLogin(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            HttpSession session,
            Model model
    ) {
        User user = null;
        if ((user = userService.doLogin(username, password)) == null){
            model.addAttribute("fail", true);
            return "login";
        }
        session.setAttribute("loginUser", user);
        //如果一次接口或者视图的跳转没有用到Request域对象或者Model，能用重定向就用重定向
        //因为用户可能刷新浏览器
        //防止因为地址栏不变导致的表单重复提交
        return "redirect:/video/getHomeVideos";
    }
    @RequestMapping("/headImgload")
    @ResponseBody
    public  ResponseEntity<String> headImgLoad(@RequestParam("headImg") MultipartFile headImg,
                                                HttpSession session) throws IOException, NullFileException {
        return userService.headImgUpload(headImg, session);
    }
}
