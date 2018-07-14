package com.breeze.handler.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.breeze.api.EchoService;
import com.breeze.handler.HelloHandler;

@Component("helloHandler")
public class HelloHandlerImpl implements HelloHandler {

    @Autowired
    EchoService echoService;

    @Override
    public String sayHello(String name) {
        System.out.println("handler param: " + name);
        String result = echoService.sayHello(name);
        return result;
    }
}
