package com.zhaofan.client.dao;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.zhaofan.client.entity.User;
import com.zhaofan.util.CommonUtils;

import javax.sql.DataSource;
import java.sql.*;

/**
 * Author:zhaofan
 * Created:2019/8/23
 * 数据库操作，将注册的新用户存入数据库，登陆时从数据库中查询用户名，密码是否正确
 */
public class AccountDao {
    private static DataSource dataSource;

    static {
        try {
            dataSource = DruidDataSourceFactory.createDataSource
                    (CommonUtils.loadProperties("datasource.properties"));
        } catch (Exception e) {
            System.out.println("数据源加载失败");
            e.printStackTrace();
        }
    }


    private Connection getConnection(){
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            System.out.println("数据库连接获取失败");
            e.printStackTrace();
        }
        return null;
    }

    //加密
    private String encrypt(String passward){
        char[] chars = passward.toCharArray();
        StringBuffer str = new StringBuffer("");//StringBuffer线程安全
        for(int i = 0;i<chars.length;i++){
            if(chars[i]>='0'&&chars[i]<='9'){
                str.append(chars[i]*5);
            }else if(chars[i]>='a'&&chars[i]<='z'||chars[i]>='A'&&chars[i]<='Z'){
                str.append((char) (chars[i]+2));
            }else {
                str.append(chars[i]);
            }
            if(i%4==0){
                str.append("%");
            }
            if(i%3==0){
                str.append("&");
            }
        }
        return str.toString();
    }

    //注册时插入用户
    public boolean reg(User user)  {
        System.out.println(user.toString());
        Connection connection = this.getConnection();
        String sql = "INSERT INTO USER (userName,password,brief) values(?,?,?)";
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sql);
            statement.setString(1,user.getUserName());
            statement.setString(2,encrypt(user.getPassword()));
            statement.setString(3,user.getBrief());
            int res = statement.executeUpdate();
            if(res==1){
                return true;
            }
        } catch (SQLException e) {
            System.out.println("注册失败");
            e.printStackTrace();

        }finally {
            closeResources(connection,statement);
        }
        return false;
    }


    //登陆
    public User login(String name,String password){
        String sql = "SELECT * FROM user WHERE username = ? AND password = ?";
        Connection connection = this.getConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(sql);
            statement.setString(1,name);
            statement.setString(2,encrypt(password));
            resultSet = statement.executeQuery();
            if(resultSet.next()){
                User user = getUser(resultSet);
                return user;
            }
        } catch (SQLException e) {
            System.out.println("登陆失败");
            e.printStackTrace();
        }finally {
            closeResources(connection,statement,resultSet);
        }
        return null;
    }

    private User getUser(ResultSet resultSet) {
        User user = new User();
        try {
            user.setId(resultSet.getInt("id"));
            user.setUserName(resultSet.getString("userName"));
            user.setPassword(resultSet.getString("password"));
            user.setBrief(resultSet.getString("brief"));
            return user;
        } catch (SQLException e) {
            System.out.println("用户获失败");
            e.printStackTrace();
        }
        return null;
    }

    //注册用户——新用户插入数据库
    private void closeResources(Connection connection,Statement statement) {
        try {
            statement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //登陆-获取用户信息结果集
    private void closeResources(Connection connection, Statement statement, ResultSet resultSet) {
        try {
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        closeResources(connection,statement);
    }


}
