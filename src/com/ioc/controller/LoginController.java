package com.ioc.controller;

import com.ioc.anotation.MyAutowired;
import com.ioc.anotation.MyController;
import com.ioc.service.LoginService;
import com.ioc.anotation.Value;

@MyController
public class LoginController {
    @MyAutowired(value = "pro")
    private LoginService loginService;

    @Value(value = "ioc.bean.pathTest")
    private String test;

    public String login() {
        return loginService.login();
    }
}
