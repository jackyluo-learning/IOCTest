package com.ioc.service;

import com.ioc.anotation.MyService;

@MyService(value = "test")
public class TestLoginServiceImpl implements LoginService{
    @Override
    public String login() {
        return "Testing";
    }
}
