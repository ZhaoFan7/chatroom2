package com.zhaofan.client.entity;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Author:zhaofan
 * Created:2019/8/24
 */
public class UserTest {

    //测试lombok能否正常使用
    @Test
    public void getUserName() {
        User user = new User();
        user.setUserName("zhang");
        System.out.println(user.getUserName());
    }
}