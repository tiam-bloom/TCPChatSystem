package com.hbnu.dao;

import com.hbnu.pojo.User;

import java.util.List;

/**
 * @Author Tiam
 * @Date 2022/11/3 13:10
 * @Description: 数据库操作层接口
 */

public interface UserDao {
    /**
     * 新增用户
     *
     * @param user
     * @return 返回受影响的行数
     */
    int insertUser(User user);

    /**
     * 根据用户名查询用户, Username主键, 只能查到唯一值
     *
     * @param name
     * @return 查询为空返回null, 否则返回User对象
     */
    User findByUsername(String name);

    /**
     * 根据用户名 删除用户
     *
     * @param name
     * @return 返回受影响的行数
     */
    int deleteUser(String name);

    /**
     * 根据用户名修改密码
     *
     * @param name   要修改的用户
     * @param newPwd 新密码
     * @return 返回受影响的行数
     */
    int updatePwd(String name, String newPwd);

    /**
     * 查询所有已注册用户, 可能数据过大
     * @return
     */
    List<User> findAllUsers();
}
