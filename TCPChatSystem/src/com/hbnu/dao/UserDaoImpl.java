package com.hbnu.dao;

import com.hbnu.pojo.User;
import com.hbnu.utils.JDBCUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author Tiam
 * @Date 2022/11/3 13:11
 * @Description: User数据库操作层实现类
 */
public class UserDaoImpl implements UserDao {

    @Override
    public int insertUser(User user) {
        int row = 0;
        Connection connection = null;
        PreparedStatement ps = null;
        try {
            connection = JDBCUtils.getConnection();
            // 关闭自动提交事务
            connection.setAutoCommit(false);
            String sql = "insert into user values(?,?,?)";
            ps = connection.prepareStatement(sql);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getEmail());
            row = ps.executeUpdate();
            // 手动提交事务
            connection.commit();
        } catch (SQLException e) {
            try {
                // 发生异常回滚
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            JDBCUtils.close(null, ps, connection);
        }
        return row;
    }

    @Override
    public User findByUsername(String name) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            connection = JDBCUtils.getConnection();
            String sql = "select * from user where username = ?";
            ps = connection.prepareStatement(sql);
            ps.setString(1, name);
            rs = ps.executeQuery();
            if (rs.next()) {
                return new User(rs.getString("username"), rs.getString("password"), rs.getString("email"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.close(rs, ps, connection);
        }
        return null;
    }


    @Override
    public int deleteUser(String name) {
        int row = 0;
        Connection connection = null;
        PreparedStatement ps = null;
        try {
            connection = JDBCUtils.getConnection();
            connection.setAutoCommit(false);
            String sql = "delete from user where username = ?";
            ps = connection.prepareStatement(sql);
            ps.setString(1, name);
            row = ps.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            JDBCUtils.close(null, ps, connection);
        }
        return row;
    }

    @Override
    public int updatePwd(String name, String pwd) {
        int row = 0;
        Connection connection = null;
        PreparedStatement ps = null;
        try {
            connection = JDBCUtils.getConnection();
            connection.setAutoCommit(false);
            String sql = "update user set password = ? where username = ?";
            ps = connection.prepareStatement(sql);
            ps.setString(1, pwd);
            ps.setString(2, name);
            row = ps.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            JDBCUtils.close(null, ps, connection);
        }
        return row;
    }

    @Override
    public List<User> findAllUsers() {
        List<User> users = null;
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            connection = JDBCUtils.getConnection();
            // top 10, 防止数据过大
            String sql = "select * from user ";
            ps = connection.prepareStatement(sql);
            rs = ps.executeQuery();
            users = rsToList(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.close(rs, ps, connection);
        }
        return users;
    }

    /**
     * @param rs 查询的结果集
     * @return 转为 List集合
     * @throws SQLException
     */
    private List<User> rsToList(ResultSet rs) throws SQLException {
        List<User> res = new ArrayList<>();
        while (rs.next()) {
            res.add(new User(rs.getString("username"), rs.getString("password"), rs.getString("email")));
        }
        return res;
    }
}
