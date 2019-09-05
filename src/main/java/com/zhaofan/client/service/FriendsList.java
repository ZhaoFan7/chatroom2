package com.zhaofan.client.service;

import com.zhaofan.util.CommonUtils;
import com.zhaofan.util.MessageVO;

import javax.swing.*;
import java.awt.event.*;
import java.awt.peer.MouseInfoPeer;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Author:zhaofan
 * Created:2019/8/25
 */
public class FriendsList {
    private JPanel friendsPan;
    private JLabel onLineFriendsLab;
    private JScrollPane friendsListPan;
    private JLabel groupLab;
    private JScrollPane groupPan;
    private JButton createGroupBut;
    private JButton exitBut;
    private JFrame frame;

    private String name;
    private Set<String> userSet;
    private Connect2Server connect2Server;
    //群名以及群成员 String为群名
    private Map<String,Set<String>> groupMap = new ConcurrentHashMap<>();
    //私聊界面缓存
    private Map<String,PrivateChatGUI> privateChatGUIMap = new ConcurrentHashMap<>();
    //群聊界面缓存
    private Map<String,GroupChatGUI> groupChatGUIMap = new ConcurrentHashMap<>();

    public void putGroupMap(String groupName, Set<String> selectUserSet) {
        groupMap.put(groupName,selectUserSet);
    }

    public FriendsList(String name, Set<String> userSet, Connect2Server connect2Server,JFrame friendListframe) {
        this.name = name;
        this.userSet = userSet;
        this.connect2Server =connect2Server;

        friendListframe.dispose();
        frame = new JFrame(name+"的联系人列表");
        frame.setContentPane(friendsPan);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400,300);
        //frame.pack();  setSize与pack()同时用时，setSize()不起作用
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);

        //将联系人动态加入好友列表
        userLoad();

        //后台线程不断监听服务器发来的消息
        Thread listenThread = new Thread(new ListenTask());
        listenThread.setDaemon(true);
        listenThread.start();

        //按下创建群组键时会发生的事情
        createGroupBut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //弹出创建群组页面
                new CreateGroup(name,userSet,connect2Server,FriendsList.this);
            }
        });
        exitBut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MessageVO voExit = new MessageVO();
                voExit.setFrom(name);
                voExit.setType("4");
                PrintStream printStream = connect2Server.getPrintStream();
                printStream.println(CommonUtils.Object2Json(voExit));
                frame.dispose();
            }
        });
    }

    //加载好友列表
    private void userLoad(){
        //新建标签数组，将其放入滚动好友列表中
        JLabel[] newFirendsLab = new JLabel[userSet.size()];
        JPanel newFriendsPan = new JPanel();

        //setLayout()设置布局
        //new BoxLayout(nfriendsPan,BoxLayout.Y_AXIS) 盒子布局，
        // 第一个参数：那个地方，第二个参数：BoxLayout.Y_AXIS代表垂直方向
        newFriendsPan.setLayout(new BoxLayout(newFriendsPan,BoxLayout.Y_AXIS));
        Iterator<String> iterator = userSet.iterator();
        int i = 0;
        while(iterator.hasNext()){
            String friendName = iterator.next();
            newFirendsLab[i] = new JLabel(friendName);
            //将每个标签数组添加到好友panel中
            newFriendsPan.add(newFirendsLab[i]);
            //添加鼠标点击事件  name:传入自己的名字
            newFirendsLab[i].addMouseListener(new PrivateChat(friendName,name));
            i++;
        }
        //设置朋友滚动盘子的布局,将朋友panel加到滚动朋友panel中
        friendsListPan.setViewportView(newFriendsPan);
        //设置滚动条向垂直动
        friendsListPan.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        //revalidate()：刷新
        newFriendsPan.revalidate();
        friendsListPan.revalidate();
    }

    //加载群组列表
    public void groupLoad(){
        JPanel newGroupPan = new JPanel();
        newGroupPan.setLayout(new BoxLayout(newGroupPan,BoxLayout.Y_AXIS));
        JLabel[] newGroupLab = new JLabel[groupMap.size()];
        Set<Map.Entry<String,Set<String>>> groupUserSet= groupMap.entrySet();
        Iterator<Map.Entry<String,Set<String>>> iterator = groupUserSet.iterator();
        int i = 0;
        while(iterator.hasNext()){
            String groupName = iterator.next().getKey();
            newGroupLab[i] = new JLabel(groupName);
            newGroupPan.add(newGroupLab[i]);
            newGroupLab[i].addMouseListener(new GroupChat(groupName));
            i++;
        }
        groupPan.setViewportView(newGroupPan);
        groupPan.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        newGroupPan.revalidate();
        groupPan.revalidate();
    }

    //好友列表的后台任务，不断监听服务器发来的信息:好友的上线信息、用户的私聊、群聊
    class ListenTask implements Runnable{
        @Override
        public void run() {
            Scanner scanner = connect2Server.getScanner();
            while (scanner.hasNextLine()){
                //获得来自服务器的消息
                String info = scanner.nextLine();
                MessageVO voFromServer = (MessageVO) CommonUtils.Json2Object(info,MessageVO.class);
                String type = voFromServer.getType();
                String content = voFromServer.getContent();
                String to = voFromServer.getTo();
                String from = voFromServer.getFrom();
                if(type.equals("5")){
                    //加载用户列表,在log中已经接收过，这里接不到type为5的消息
                }else if(type.equals("6")){
                    //新用户上线信息
                    // showMessageDialog（jPanel，"提示消息"，"标题"，图标（警告⚠，错误❌，问题，信息等））
                    JOptionPane.showMessageDialog(frame, "好友"+content+"上线啦！",
                            "好友上线提示", JOptionPane.INFORMATION_MESSAGE);
                    userSet.add(content);
                    //刷新好友列表
                    userLoad();
                }else if(type.equals("7")){
                    //接收私聊
                    if(privateChatGUIMap.containsKey(from)){
                        //之前私聊过，隐藏状态只需要调用重新显示，显示状态只需发送消息
                        PrivateChatGUI privateChatGUI = privateChatGUIMap.get(from);
                        privateChatGUI.getFrame().setVisible(true);
                        privateChatGUI.record(content);
                    }else{
                        //第一次私聊
                        PrivateChatGUI privateChatGUI = new PrivateChatGUI(from,to,connect2Server);
                        privateChatGUIMap.put(from,privateChatGUI);
                        privateChatGUI.record(content);
                    }
                }else if(type.equals("8")){
                    //接收群聊
                    if(groupChatGUIMap.containsKey(to)){//to为groupName
                        GroupChatGUI groupChatGUI = groupChatGUIMap.get(to);
                        groupChatGUI.getFrame().setVisible(true);
                        String s = " "+from+"说："+content;
                        groupChatGUI.record(s);
                     }else{
                        Set<String> groupSet = groupMap.get(to); //该群中的成员
                        GroupChatGUI groupChatGUI = new GroupChatGUI(FriendsList.this,to,name, groupSet,connect2Server);
                        groupChatGUIMap.put(to,groupChatGUI);
                        String s = " "+from+"说："+content;
                        groupChatGUI.record(s);
                    }
                }else if(type.equals("11")) {
                    //用户退出信息
                    // showMessageDialog（jPanel，"提示消息"，"标题"，图标（警告⚠，错误❌，问题，信息等））
                    JOptionPane.showMessageDialog(frame, "好友"+from+"已离开",
                            "好友离线提示", JOptionPane.INFORMATION_MESSAGE);
                    userSet.remove(from);
                    //刷新好友列表
                    userLoad();
                }else if(type.equals("9")){
                    //将别人创建的群（有自己）加入群聊列表
                    if(from.equals("no")){
                        JOptionPane.showMessageDialog(frame, "群名已被注册过，请重新建群",
                                "建群失败提示", JOptionPane.WARNING_MESSAGE);
                    }else{
                        Set<String> groupSet = (Set<String>)CommonUtils.Json2Object(to,Set.class);
                        groupMap.put(content,groupSet);
                        groupLoad();
                    }

                }
            }
        }
    }

    class PrivateChat implements MouseListener{

        private String friendName;
        private String myName;

        public PrivateChat(String friendName,String myName){
            this.friendName = friendName;
            this.myName = myName;
        }

        //鼠标点击事件，只需写这个
        @Override
        public void mouseClicked(MouseEvent e) {
            // 判断好友列表私聊界面缓存是否已经有指定标签
            Set<String> userSet = privateChatGUIMap.keySet();
            if(userSet.contains(friendName)){
                PrivateChatGUI privateChatGUI = privateChatGUIMap.get(friendName);
                privateChatGUI.getFrame().setVisible(true);
            }else{
                PrivateChatGUI privateChatGUI =new PrivateChatGUI(friendName,myName,connect2Server);
                privateChatGUI.getFrame().setVisible(true);
                privateChatGUIMap.put(friendName,privateChatGUI);
            }
        }

        //一直按
        @Override
        public void mousePressed(MouseEvent e) {
        }

        //按下松开
        @Override
        public void mouseReleased(MouseEvent e) {
        }

        //移入
        @Override
        public void mouseEntered(MouseEvent e) {
        }

        //移出
        @Override
        public void mouseExited(MouseEvent e) {
        }
    }


    //群聊点击事件
    class GroupChat implements MouseListener{

        private String groupName;

        public GroupChat(String groupName){
            this.groupName = groupName;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if(groupChatGUIMap.containsKey(groupName)){
                GroupChatGUI groupChatGUI = groupChatGUIMap.get(groupName);
                groupChatGUI.getFrame().setVisible(true);
            }else {
                GroupChatGUI groupChatGUI = new GroupChatGUI(FriendsList.this,groupName,name,groupMap.get(groupName),connect2Server);
                groupChatGUI.getFrame().setVisible(true);
                groupChatGUIMap.put(groupName,groupChatGUI);
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }

}



