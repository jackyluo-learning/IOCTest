package com.ioc.bean;

import com.ioc.anotation.MyComponent;

@MyComponent
public class UserDao {
    public void findUser(String userName) {
        System.out.println("Find a user named: " + userName);
    }
}
