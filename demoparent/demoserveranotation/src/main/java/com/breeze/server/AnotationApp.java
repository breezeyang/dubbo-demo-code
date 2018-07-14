package com.breeze.server;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class AnotationApp {

    public static void main(String[] args) {
        try {
            // 初始化Spring
            ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationProvider.xml");
            System.out.println("dubbo provider is running...");
            System.in.read();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}

