package com.vz.eduwisdom.controller;

import javax.servlet.http.HttpServletRequest;

import com.vz.eduwisdom.domain.User;
import com.vz.eduwisdom.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("index")
public class IndexController {
    @Autowired
    @Qualifier("userService")
    private UserService userService;

    @Autowired
    private HttpServletRequest request;

    @RequestMapping("/login.index")
    @ResponseBody
    public User login(String username, String password) throws Exception {
        return userService.doLoginByUsernameAndPassword(username, password, request);
    }

    @RequestMapping("/forwardMainUI.do")
    public String forwardMainUI() throws Exception {
        return "main";
    }

    @RequestMapping(value = "/sendEmail.index", produces = {"text/javascript;charset=UTF-8"})
    @ResponseBody
    public String sendEmail(String email) throws Exception {
        return userService.doGetUserByEmail(email);
    }

    @RequestMapping("/forwardResetPasswordUI.index")
    public String forwardResetPasswordUI() throws Exception {
        return "resetPassword";
    }

    @RequestMapping(value = "/doResetPassword.index", produces = {"text/javascript;charset=UTF-8"})
    @ResponseBody
    public String doResetPassword(String username, String checkCode, String password, String rePassword) throws Exception{
        return userService.doResetPassword(username,checkCode,password,rePassword);
    }

    @RequestMapping(value = "/doResetPassword.do", produces = {"text/javascript;charset=UTF-8"})
    @ResponseBody
    public String doResetPassword(String oldPassword, String password, String rePassword) throws Exception {
        return userService.doResetPassword(oldPassword, password, rePassword, request);
    }

    @RequestMapping("redirectExit.do")
    public String redirectExit() throws Exception{
        userService.doExit(request);
        return "../../index";
    }
}
