package com.zhaofan.client.service;

import com.zhaofan.util.CommonUtils;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * Author:zhaofan
 * Created:2019/8/24
 * 与服务器建立连接,并得到输入流与输出流
 */
public class Connect2Server {
    //输入流，读数据
    private Scanner scanner;
    //输出流，写数据
    private PrintStream printStream;
    private final static int PORT = CommonUtils.getPORT();
    private final static String IP = CommonUtils.getIP();
    private Socket client;

    public Connect2Server() {
        try {
            this.client = new Socket(IP,PORT);
            scanner = new Scanner(client.getInputStream());
            printStream = new PrintStream(client.getOutputStream(),true,"UTF-8");
        } catch (IOException e) {
            System.out.println("与服务器建立连接失败");
            e.printStackTrace();
        }
    }

    public Scanner getScanner() {
        return scanner;
    }

    public PrintStream getPrintStream() {
        return printStream;
    }
}
