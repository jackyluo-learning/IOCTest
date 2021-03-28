package com.ioc;

import com.ioc.controller.LoginController;

public class LoginTest {
    public static void main (String[] args) {
        MyApplicationContext applicationContext = new MyApplicationContext();
        LoginController loginController = (LoginController) applicationContext.getBean("loginController");
        System.out.println(loginController.login());
    }
}
