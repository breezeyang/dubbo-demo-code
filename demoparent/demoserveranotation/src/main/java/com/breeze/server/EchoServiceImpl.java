package com.breeze.server;


import com.alibaba.dubbo.config.annotation.Service;
import com.breeze.api.EchoService;

@Service
public class EchoServiceImpl implements EchoService {

    public String sayHello(String name) {

        System.out.print("name: " + name);
        return "hello: " + name ;
    }
}
