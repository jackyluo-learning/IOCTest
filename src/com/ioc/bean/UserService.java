package com.ioc.bean;

import com.ioc.anotation.MyAutowired;
import com.ioc.anotation.MyComponent;

@MyComponent
public class UserService {
    @MyAutowired
    private UserDao userDao;

    public void findUser(String userName) {
        userDao.findUser(userName);
    }
}
