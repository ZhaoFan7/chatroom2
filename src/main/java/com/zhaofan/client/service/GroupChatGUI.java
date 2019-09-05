package com.zhaofan.client.service;

import com.zhaofan.util.CommonUtils;
import com.zhaofan.util.MessageVO;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 * Author:zhaofan
 * Created:2019/8/29
 */
public class GroupChatGUI {
    private JPanel groupChatPan;
    private JTextField sendToServer;
    private JScrollPane recordScrollPane;
    private JPanel newRecordPan;
    private JTextArea recordTextArea;
    private JScrollPane groupSetScrollPan;
    private JPanel groupSetPan;
    private JFrame frame;

    private String myName;
    private Set<String> groupSet;
    private Connect2Server connect2Server;
    private PrintStream printStream;

    public GroupChatGUI(FriendsList friendsList,String groupName,
                        String name,Set<String> userSet,Connect2Server connect2Server) {
        this.myName = name;
        this.groupSet = userSet;
        this.connect2Server = connect2Server;
        this.printStream = connect2Server.getPrintStream();

        frame = new JFrame(myName +"    群聊中,群名为"+groupName);
        frame.setContentPane(groupChatPan);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setSize(400,300);
        frame.setVisible(true);

        groupSetPan.setLayout(new BoxLayout(groupSetPan,BoxLayout.Y_AXIS));
        JLabel[] newGroupLab = new JLabel[groupSet.size()];
        Iterator<String> iterator = groupSet.iterator();
        int i = 0;
        while(iterator.hasNext()){
            String groupUserName = iterator.next();
            newGroupLab[i] = new JLabel(groupUserName);
            newGroupLab[i].addMouseListener(friendsList.new PrivateChat(groupUserName,myName));
            groupSetPan.add(newGroupLab[i]);
            i++;
        }
        groupSetScrollPan.setViewportView(groupSetPan);
        groupSetScrollPan.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        groupSetPan.revalidate();
        groupSetScrollPan.revalidate();

        sendToServer.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {

                if (e.getKeyCode()==KeyEvent.VK_ENTER){
                    String message = sendToServer.getText();
                    //将消息发送给服务器，有服务器转发给各个用户
                    MessageVO voGroupChat = new MessageVO();
                    voGroupChat.setType("3");
                    voGroupChat.setContent(message);
                    voGroupChat.setTo(groupName);
                    voGroupChat.setFrom(myName);
                    System.out.println(voGroupChat);
                    printStream.println(CommonUtils.Object2Json(voGroupChat));
                    //将发送栏置为空
                    sendToServer.setText("");
                }
            }
        });
    }

    public JFrame getFrame() {
        return frame;
    }

    public void record(String str){
        recordTextArea.append(str+"\n");
    }
}
