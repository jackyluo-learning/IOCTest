package com.ioc.mapper;

import com.ioc.anotation.MyMapping;

@MyMapping
public class LoginMapperImpl implements LoginMapper{
    @Override
    public String login() {
        return "Login success.";
    }
}
