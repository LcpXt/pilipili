package com.colin.bh.service;

import com.colin.bh.bean.User;
import com.colin.bh.util.response.ResponseEntity;

/**
 * 2024年03月23日17:01
 */
public interface UserService {

    ResponseEntity<String> checkUsername(String username);

    ResponseEntity<String> sendEmail(String email, Integer flag);

    ResponseEntity<String> checkCode(String code, String email);

    Boolean doRegister(User user);

    boolean changePassword(String email, String password);

    User doLogin(String username, String password);
}
