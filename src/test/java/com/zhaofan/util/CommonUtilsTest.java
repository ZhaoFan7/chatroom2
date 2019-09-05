package com.zhaofan.util;

import com.zhaofan.client.entity.User;
import org.junit.Test;

import java.util.Properties;

import static com.zhaofan.util.CommonUtils.Object2Json;
import static org.junit.Assert.*;

/**
 * Author:zhaofan
 * Created:2019/8/22
 */
public class CommonUtilsTest {
    @Test
    public void loadProperties() {
        Properties properties = CommonUtils.loadProperties("datasource.properties");
        System.out.println(properties);
    }


    @Test
    public void object2Json() {
        User user = new User();
        user.setBrief("jhjk");
        user.setPassword("4567o");
        user.setUserName("zhao");
        user.setId(809);
        String  str = CommonUtils.Object2Json(user);
        System.out.println(str);
    }

    @Test
    public void json2Object() {
        String jsonStr = "{\"id\":809,\"userName\":\"zhao\",\"password\":\"4567o\",\"brief\":\"jhjk\"}";
        User user = (User) CommonUtils.Json2Object(jsonStr,User.class);
        System.out.println(user);
    }
}