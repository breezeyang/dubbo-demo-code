package com.breeze.controller;

import com.breeze.common.ApplicationContextHelper;
import com.breeze.common.RpcRequest;
import com.breeze.common.DubboUtil;
import com.breeze.handler.HelloHandler;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping("/hello")
@Controller
public class HelloController {

    @Autowired
    HelloHandler helloHandler;

    @RequestMapping("test")
    @ResponseBody
    public String test(@RequestParam(required = false, defaultValue = "zhangsan") String name) {

        Object bean = ApplicationContextHelper.getBean("helloHandler");
        System.out.println(bean);
        return name;
    }

    @RequestMapping("sayHello")
    @ResponseBody
    public String sayHello(@RequestParam String name) {
        System.out.println("param name: " + name);
        String result = helloHandler.sayHello(name);
        System.out.println("result: " + result);
        return result;
    }

    @RequestMapping("dubbo")
    @ResponseBody
    public Object dubbo(@RequestBody String request) {
        System.out.println(request);
        ObjectMapper mapper = new ObjectMapper();
        RpcRequest rpcRequest = null;
        try {
            rpcRequest = mapper.readValue(request, RpcRequest.class);
        } catch (JsonParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JsonMappingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Object invoke = null;
        try {
            invoke = DubboUtil.invoke(rpcRequest.getInterfaceName(), rpcRequest.getMethod(), rpcRequest.getParam(),
                    rpcRequest.getAddress(), rpcRequest.getVersion());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return invoke;

    }
}
