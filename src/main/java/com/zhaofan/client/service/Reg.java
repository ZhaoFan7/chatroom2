package com.zhaofan.client.service;

import com.zhaofan.client.dao.AccountDao;
import com.zhaofan.client.entity.User;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

/**
 * Author:zhaofan
 * Created:2019/8/24
 */
public class Reg {
    private JPanel regPan;
    private JButton regBut;
    private JTextField userNameText;
    private JPasswordField passwordText;
    private JTextPane briefText;
    private JLabel userNameLab;
    private JLabel passwordLab;
    private JLabel briefLab;

    private AccountDao accountDao = new AccountDao();


    public Reg() {
        JFrame frame = new JFrame("用户注册");
        frame.setContentPane(regPan);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setVisible(true);

        regBut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //得到注册的用户信息
                String name = userNameText.getText();
                String password = String.valueOf(passwordText.getPassword());
                String brief = briefText.getText();
                //将信息存入User
                User user = new User();
                user.setUserName(name);
                user.setPassword(password);
                user.setBrief(brief);
                //将user存入数据库
                if(accountDao.reg(user)){
                    JOptionPane.showMessageDialog(frame,"注册成功","提示信息",JOptionPane.INFORMATION_MESSAGE);
                    frame.dispose();
                }else{
                    try {
                        throw new Exception("注册失败");
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    // 失败，停留在当前注册页面，提示用户信息错误，再来一次，或者退出
                    // showMessageDialog（jPanel，"提示消息"，"标题"，图标（警告⚠，错误❌，问题，信息等））
                    JOptionPane.showMessageDialog(frame, "注册失败!（可能是用户名重复）","错误信息", JOptionPane.ERROR_MESSAGE);
                }

            }
        });


    }
}
