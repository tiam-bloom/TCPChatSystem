package com.hbnu.dao;

import com.hbnu.pojo.User;
import org.junit.Test;

/**
 * DAO层测试类
 */

public class UserDaoTest {
    private UserDao userDao = new UserDaoImpl();

    @Test
    public void insertUser() {
        System.out.println(userDao.insertUser(new User("1", "1", "1")));
    }

    @Test
    public void findByUsername() {
        User user = userDao.findByUsername("1");
        System.out.println(user);
    }

    @Test
    public void deleteUser() {
        System.out.println(userDao.deleteUser("1"));
    }

    @Test
    public void updatePwd() {
        System.out.println(userDao.updatePwd("1", "2"));
    }
}