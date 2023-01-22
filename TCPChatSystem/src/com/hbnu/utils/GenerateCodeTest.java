package com.hbnu.utils;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * 邮件发送测试
 */
public class GenerateCodeTest {

    @Test
    public void sendAuthCodeEmail() {
        String s = GenerateCode.sendAuthCodeEmail("yujing@stu.hbnu.edu.cn");
        System.out.println(s);
    }
}