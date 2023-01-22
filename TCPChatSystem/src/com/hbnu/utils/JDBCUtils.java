package com.hbnu.utils;

import java.sql.*;
import java.util.ResourceBundle;

/**
 * @Auther: admin
 * @Date: 2022/10/25 11:15
 * @Description: JDBC工具类
 */

public class JDBCUtils {
    static ResourceBundle bundle;
    static String driverClass;
    static String url;
    static String username;
    static String password;

    /*
    静态代码块
    创建类时自动执行
     */
    static {
        // 获取配置文件
        bundle = ResourceBundle.getBundle("com\\hbnu\\config\\jdbc");
        driverClass = bundle.getString("driverClass");
        url = bundle.getString("url");
        username = bundle.getString("username");
        password = bundle.getString("password");

        try {
            Class.forName(driverClass);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取连接
     * @return
     * @throws SQLException
     */
    public static Connection getConnection(){
        try {
            return DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            System.out.println("数据库连接失败");
            e.printStackTrace();
            System.exit(-1);
        }
        return null;
    }

    /**
     * 关闭连接
     * @param rs
     * @param st
     * @param co
     */
    public static void close(ResultSet rs, Statement st, Connection co) {
        try {
            if (rs != null) rs.close();
            if (st != null) st.close();
            if (co != null) co.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
