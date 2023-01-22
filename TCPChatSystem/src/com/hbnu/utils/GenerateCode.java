package com.hbnu.utils;

import org.apache.commons.mail.HtmlEmail;

import java.util.Random;
import java.util.ResourceBundle;

/**
 * @Author Tiam
 * @Date 2022/11/11 21:38
 * @Description: 邮箱验证码
 */
public class GenerateCode {
    static ResourceBundle bundle;
    static String userName;
    static String password;
    static String aHostName;
    static String fromEmail;
    static String name;
    static String codeNumber;
    static String newCharset;


    static {
        bundle = ResourceBundle.getBundle("com\\hbnu\\config\\emailcode"); //不加文件后缀名!
        userName = bundle.getString("userName");
        password = bundle.getString("password");
        aHostName = bundle.getString("aHostName");
        fromEmail = bundle.getString("fromEmail");
        name = bundle.getString("name");
        codeNumber = bundle.getString("codeNumber");
        newCharset = bundle.getString("newCharset");
    }


    /**
     * 发送验证码
     *
     * @param toEmail
     * @return 发送成功返回发送的验证码, 否则返回null
     */
    public static String sendAuthCodeEmail(String toEmail) {
        String code;
        try {
            HtmlEmail mail = new HtmlEmail();
            /*发送邮件的服务器 126邮箱为smtp.126.com,163邮箱为163.smtp.com，QQ为smtp.qq.com*/
            mail.setHostName(aHostName);
            /*不设置发送的消息有可能是乱码*/
            mail.setCharset(newCharset);
            /*IMAP/SMTP服务的密码*/
            mail.setAuthentication(userName, password);
            /*发送邮件的邮箱和发件人*/
            mail.setFrom(fromEmail, name);
            /*使用安全链接*/
            mail.setSSLOnConnect(true);
            /*接收的邮箱*/
            mail.addTo(toEmail);
            /*验证码*/
            code = generateVerifyCode(Integer.parseInt(codeNumber));
            /*设置邮件的主题*/
            mail.setSubject("邮箱验证码");
            /*设置邮件的内容*/
            String message = "尊敬的用户，您好：\n" +
                    "您正在TCPChatSystem进行找回密码操作，本次请求的邮件验证码为：" + code + "\n" +
                    "本验证码1分钟内有效，请及时输入。如非本人操作，请忽略该邮件。\n" +
                    "祝在TCPChatSystem收获愉快！\n" +
                    "( ゜- ゜)つロ Tiam~ - HBNU-计信2014班\n" +
                    "（这是一封自动发送的邮件，请不要直接回复）";
            mail.setMsg(message);
            mail.send();//发送
            System.out.println("邮箱验证码" + code + "发送成功");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return code;
    }

    /**
     * 生成验证码
     *
     * @param number 位数
     * @return
     */
    public static String generateVerifyCode(int number) {
        Random random = new Random();
        StringBuilder builder = new StringBuilder();
        for (int i = 1; i <= number; i++) {
            builder.append(random.nextInt(10));
        }
        return builder.toString();
    }


}
