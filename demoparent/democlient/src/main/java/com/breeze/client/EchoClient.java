package com.breeze.client;

import com.breeze.api.EchoService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

public class EchoClient {

    public static void main(String[] args) {
        try {
            // 初始化Spring
            ApplicationContext ctx = new ClassPathXmlApplicationContext(
                    "applicationConsumer.xml");
            EchoService demoService = (EchoService) ctx
                    .getBean("echoService"); // 获取远程服务代理
            String result = demoService.sayHello("zhangsan"); // 运行远程方法

            System.out.println(result);
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

