package com.colin.bh.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 2024年03月23日16:59
 */
@Controller
public class RouterController {

    @RequestMapping("/toRegister")
    public String toRegister() {
        return "register";
    }

    @RequestMapping("/toLogin")
    public String toLogin() {
        return "login";
    }
    @RequestMapping("/toForgetPassword")
    public String toForgetPassword() {
        return "forgetPassword";
    }

    @RequestMapping("/toChangePassword/{email}")
    public String toChangePassword(
            @PathVariable("email") String email,
            Model model
    ) {
        model.addAttribute("email", email);
        return "changePassword";
    }
    @RequestMapping("/toPersonal")
    public String toPersonal() {
        return "personalCenter";
    }
}
