package com.colin.bh.jedis;


import com.colin.bh.exception.email.CodeCheckException;
import com.colin.bh.exception.email.CodeGeneralException;
import com.colin.bh.exception.email.EmailException;
import com.colin.bh.util.response.ResponseEntity;
import com.colin.bh.util.response.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * 2024年05月15日19:11
 */
@Component
public class UserJedisOperate {

    @Autowired
    private JedisPool jedisPool;

    @Value("${my.project.param.code.expiration-time}")
    private Integer expirationTime;

    public String generateCode(String email, String code) {
        try (Jedis resource = jedisPool.getResource()) {

            // 在注册表单中 验证验证码是否发送过
            if (resource.exists(email)) {
                return "error";
            }

            resource.setex(email, this.expirationTime, code);
            return "success";

        } catch (Exception e) {
            throw new CodeGeneralException("验证码生成错误");
        }
    }

    public ResponseEntity<String> checkCode(String code, String email) {
        try (Jedis resource = jedisPool.getResource()){
            ResponseEntity<String> responseEntity = new ResponseEntity<>(Status.ERROR, "验证码错误", null);
            if(!resource.get(email).equals(code)){
                return responseEntity;
            }
            responseEntity.setStatus(Status.SUCCESS);
            responseEntity.setMessage("验证码正确");
            return responseEntity;
        }catch (Exception e){
            throw new CodeCheckException("验证码校验错误");
        }
    }

    public void delCache(String email) {
        try (Jedis resource = jedisPool.getResource()){
            resource.del(email);
        }catch (Exception e){
            throw new CodeCheckException("验证码校验错误");
        }
    }

    public void cacheEmail(String email) {
        try (Jedis resource = jedisPool.getResource()){
            resource.sadd("emailCache", email);
        }catch (Exception e){
            throw new EmailException("邮箱缓存错误");
        }
    }

    public boolean checkEmailExists(String email) {
        try (Jedis resource = jedisPool.getResource()){
            return resource.sismember("emailCache", email);
        }catch (Exception e){
            throw new EmailException("邮箱缓存错误");
        }
    }
}
