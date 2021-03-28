package com.ioc.service;

import com.ioc.anotation.MyAutowired;
import com.ioc.anotation.MyService;
import com.ioc.mapper.LoginMapper;

@MyService(value = "pro")
public class LoginServiceImpl implements LoginService{
    @MyAutowired
    private LoginMapper loginMapper;

    @Override
    public String login() {
        return loginMapper.login();
    }
}
