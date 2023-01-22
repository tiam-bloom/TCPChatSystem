package com.hbnu.utils;

import java.io.*;
import java.net.Socket;

/**
 * @Author Tiam
 * @Date 2022/11/8 17:01
 * @Description: 向 Socket读取和发送消息
 */
public class ReadSendUtils {
    private Socket socket;

    public ReadSendUtils(Socket socket) {
        this.socket = socket;
    }

    /**
     * 发送字符串消息
     * @param string
     */
    public void sendString(String string) {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(socket.getOutputStream(), true);
            pw.println(string);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送对象
     * @param object
     */
    public void send(Object object) {
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(object);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取一行字符串
     * @return
     */
    public String readString() {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            return br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("读取异常");
            e.printStackTrace();
            System.exit(-1);
        }
        return null;
    }

    /**
     * 读取对象
     * @return
     */
    public Object read() {
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(socket.getInputStream());
            return ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("读取异常");
            e.printStackTrace();
            System.exit(-1);
        }
        return null;
    }
}
