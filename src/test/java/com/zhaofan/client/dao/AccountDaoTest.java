package com.zhaofan.client.dao;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Author:zhaofan
 * Created:2019/8/24
 */
public class AccountDaoTest {

    private AccountDao accountDao = new AccountDao();

    @Test
    public void encrypt() {
        String password = "zabced942632dgsfjkfbshd6";
        //System.out.println(accountDao.encrypt(password));  //将encryptgai成private方法测试
        System.out.println((char) ('a'+5));
    }
}