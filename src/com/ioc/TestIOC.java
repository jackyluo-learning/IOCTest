package com.ioc;

import com.ioc.bean.UserService;

import java.util.HashMap;

public class TestIOC {
    public static void main(String[] args) throws Exception {
        MyApplicationContext myApplicationContext=new MyApplicationContext();
        UserService userService =(UserService)myApplicationContext.getBean("userService");
        UserService userService1 =(UserService)myApplicationContext.getBean("userService");
        System.out.println("userService HashCode:" + System.identityHashCode(userService));
        System.out.println("userService1 HashCode:" + System.identityHashCode(userService1));
        System.out.println(userService == userService1);
        userService.findUser("张三");
        myApplicationContext.getBeanMap();
        myApplicationContext.getClassName();
    }
}
