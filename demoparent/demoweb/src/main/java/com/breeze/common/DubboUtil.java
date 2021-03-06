package com.breeze.common;

import static java.lang.Class.forName;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.rpc.service.GenericService;

public class DubboUtil {

    private static Logger logger = LoggerFactory.getLogger(DubboUtil.class);

    // 当前应用的信息
    private static ApplicationConfig application = new ApplicationConfig();
    // 注册中心信息缓存
    private static Map<String, RegistryConfig> registryConfigCache = new ConcurrentHashMap<>();

    // 各个业务方的ReferenceConfig缓存
    private static Map<String, ReferenceConfig> referenceCache = new ConcurrentHashMap<>();

    static {
        application.setName("consumer-dubbo-util");
    }
    
    /**
     * 缓存key
     * @param address
     * @param group
     * @param version
     * @return
     */
    private static String calRegistryKey(String address, String group, String version) {
        String key = address + "-" + group + "-" + version;
        return key;
    }
    
    /**
     * 注册配置
     * 
     * @param address
     * @param group
     * @param version
     * @return
     */

    private static RegistryConfig getRegistryConfig(String address, String group, String version) {
        String key = calRegistryKey(address, group, version);
        RegistryConfig registryConfig = registryConfigCache.get(key);
        if (null == registryConfig) {
            registryConfig = new RegistryConfig();
            if (StringUtils.isNotEmpty(address)) {
                registryConfig.setAddress(address);
            }
            if (StringUtils.isNotEmpty(group)) {
                registryConfig.setGroup(group);
            }
            
            if (StringUtils.isNotEmpty(version)) {
                registryConfig.setVersion(version);
            }
           
            registryConfigCache.put(key, registryConfig);
        }
        return registryConfig;
    }

    private static ReferenceConfig getReferenceConfig(String interfaceName, String address, String group,
            String version) {
        String referenceKey = interfaceName;
        ReferenceConfig referenceConfig = referenceCache.get(referenceKey);
        if (null == referenceConfig) {
            try {
                referenceConfig = new ReferenceConfig<>();
                referenceConfig.setApplication(application);
                referenceConfig.setRegistry(getRegistryConfig(address, group, version));
                Class interfaceClass = forName(interfaceName);
                referenceConfig.setInterface(interfaceClass);
                if (StringUtils.isNotEmpty(version)) {
                    referenceConfig.setVersion(version);
                }
                referenceConfig.setGeneric(true);
                // 直接连接
//                referenceConfig.setUrl("dubbo://127.0.0.1:20880/com.breeze.api.EchoService");
                referenceCache.put(referenceKey, referenceConfig);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return referenceConfig;
    }

    public static Object invoke(String interfaceName, String methodName, List<Object> paramList, String address,
            String version) {
        ReferenceConfig reference = getReferenceConfig(interfaceName, address, null, version);
        if (null != reference) {
            GenericService genericService = (GenericService) reference.get();
            if (genericService == null) {
                logger.debug("GenericService 不存在:{}", interfaceName);
                return null;
            }

            Object[] paramObject = null;
            if (!CollectionUtils.isEmpty(paramList)) {
                paramObject = new Object[paramList.size()];
                for (int i = 0; i < paramList.size(); i++) {
                    paramObject[i] = paramList.get(i);
                }
            }

            Object resultParam =
                    genericService.$invoke(methodName, getMethodParamType(interfaceName, methodName), paramObject);
            return resultParam;
        }
        return null;
    }

    public static String[] getMethodParamType(String interfaceName, String methodName) {
        try {
            // 创建类
            Class<?> class1 = Class.forName(interfaceName);
            // 获取所有的公共的方法
            Method[] methods = class1.getMethods();
            for (Method method : methods) {
                if (method.getName().equals(methodName)) {
                    Class[] paramClassList = method.getParameterTypes();
                    String[] paramTypeList = new String[paramClassList.length];
                    int i = 0;
                    for (Class className : paramClassList) {
                        paramTypeList[i] = className.getTypeName();
                        i++;
                    }
                    return paramTypeList;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    public static void main(String[] args) {
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setAddress("multicast://224.5.6.7:1234");
        rpcRequest.setInterfaceName("com.breeze.api.EchoService");
        rpcRequest.setMethod("sayHello");
        List<Object> param = new ArrayList<Object>();
        param.add("zhangsan");
        rpcRequest.setParam(param);

        Object invoke = DubboUtil.invoke(rpcRequest.getInterfaceName(), rpcRequest.getMethod(), rpcRequest.getParam(),
                rpcRequest.getAddress(), rpcRequest.getVersion());
        System.out.println(invoke);
    }

}
