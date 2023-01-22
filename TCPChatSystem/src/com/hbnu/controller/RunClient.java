package com.hbnu.controller;

import com.hbnu.service.Client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * @Author Tiam
 * @Date 2022/11/9 15:31
 * @Description: 客户端启动类
 */
public class RunClient {
    public static void main(String[] args) {
        Socket socket = null;
        String hostAddress = null;
        try {
            // host 改为服务器IP地址(服务端的ip地址)
            hostAddress = InetAddress.getLocalHost().getHostAddress();
            socket = new Socket(hostAddress, 8888);
            System.out.println("成功连接到服务器" + hostAddress + " 本机端口:" + socket.getLocalPort());
        } catch (IOException e) {
            System.out.println("服务端未开启, 请先开启服务端! 服务端IP:" + hostAddress);
            e.printStackTrace();
        }
        //启动客户端
        new Client(socket).clientView();
    }
}
