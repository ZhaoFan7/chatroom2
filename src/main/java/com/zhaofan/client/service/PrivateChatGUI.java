package com.zhaofan.client.service;

import com.zhaofan.util.CommonUtils;
import com.zhaofan.util.MessageVO;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.PrintStream;
import java.util.Scanner;

/**
 * Author:zhaofan
 * Created:2019/8/24
 */
public class PrivateChatGUI {
    private JPanel privateChatPan;
    private JLabel title;
    private JTextArea readFromServer;
    private JTextField sendToServer;
    private JButton stopSoundBut;
    private JButton startSoundBut;
    private JFrame frame;

    private String friendName;
    private String myName;
    private Connect2Server connect2Server;
    private PrintStream printStream ;
    private Scanner scanner;

    public PrivateChatGUI(String friendName,String myName,Connect2Server connect2Server) {

        this.friendName = friendName;
        this.myName = myName;
        this.connect2Server = connect2Server;
        this.printStream = connect2Server.getPrintStream();
        this.scanner = connect2Server.getScanner();

        frame = new JFrame(myName);
        frame.setContentPane(privateChatPan);
        //setDefaultCloseOperation：窗口关闭的操作，JFrame.EXIT_ON_CLOSE:程序退出，这里仅需要隐藏
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setSize(400,300);
        //唤醒页面
        frame.setVisible(true);
        title.setText("与"+friendName+"私聊中。。。");

        sendToServer.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {

                if(e.getKeyCode()==KeyEvent.VK_ENTER){
                    String message = sendToServer.getText();
                    //当按下enter键时,将消息发送给服务器，再由服务器转交给好友
                    MessageVO voPrivate = new MessageVO();
                    voPrivate.setContent(message);
                    voPrivate.setType("2");
                    voPrivate.setTo(friendName);
                    voPrivate.setFrom(myName);
                    String info = CommonUtils.Object2Json(voPrivate);
                    printStream.println(info);
                    //将发送框置为空
                    sendToServer.setText("");
                    // 将自己发送的信息展示到当前私聊界面
                    readFromServer.append(" "+myName+"说(wo):  "+message+"\n");
                }
            }
        });
    }

    public JFrame getFrame() {
        return frame;
    }

    public void record(String str){
        readFromServer.append(" "+friendName+"说："+str+"\n");
    }
}
