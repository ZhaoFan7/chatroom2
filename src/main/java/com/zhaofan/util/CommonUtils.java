package com.zhaofan.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.Properties;

/**
 * Author:zhaofan
 * Created:2019/8/22
 */
public class CommonUtils {
    private final static int PORT = Integer.parseInt
            (loadProperties("socket.properties").getProperty("port"));
    private final static String IP =
            loadProperties("socket.properties").getProperty("ip");

    public static int getPORT() {
        return PORT;
    }

    public static String getIP() {
        return IP;
    }

    //Json深拷贝.序列化都是深拷贝
    //序列化与反序列化可以使让我们不用再解析字符串，而是直接获得对象
    private static final Gson GSON = new GsonBuilder().create();

    public static Properties loadProperties(String fileName){
        Properties properties = new Properties();
        InputStream in = null;
        try {
            //获取classes路径下“文件”的路径
            in = CommonUtils.class.getClassLoader().getResourceAsStream(fileName);
            //in = new FileInputStream(new File("路径"));  不能，当程序发给别人时，不能成功找到
            properties.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    /**
     * 将对象序列化为json字符串
     * @param obj
     * @return
     */
    public static String Object2Json(Object obj){
        return GSON.toJson(obj);
    }

    /**
     * 将json字符串反序列化为对象
     * @param jsonStr  json字符串
     * @param objClass  反序列化的类反射对象
     * @return
     */
    public static Object Json2Object(String jsonStr,Class objClass){
        return GSON.fromJson(jsonStr,objClass);
    }
}
