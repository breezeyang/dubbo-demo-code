package com.breeze.impl;


import com.breeze.api.EchoService;

public class EchoServiceImpl implements EchoService {

    public String sayHello(String name) {

        System.out.print("name: " + name);
        return "hello: " + name ;
    }
}
