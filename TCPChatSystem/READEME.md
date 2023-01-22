# Hbnu_DingLi Chat

## 说明

- **运行注意：**基于TCP的，必须先运行服务端`RunServer`，再启动客户端`RunServer`

- 项目是基于TCP连接的
- Socket网络编程
- 多线程编程
- 连接数据库用的JDBC
- 用正则校验用户输入
- 发送字符串用的打印流， Printwriter
- 接收用的字符缓冲流  BufferedReader
- 可在view层先扩展用户可输入的指令，逐层扩展功能，方便扩展。
- Finished in 2022/11/15  
- 湖北师范大学 大三上学期Java课程设计

- 隐藏功能 ， 系统首页输入 `0`
- 进入管理员界面, 输入管理员密码 `yujing`
- 只能在局域网下使用，或者一台电脑自己跟自己玩。
- 私聊若在线用户不存在，会返回相应提示。
- 但是群聊 最少都会给自己一个人发送消息。若当前只有自己在线
- 群聊默认会给自己发送
- 修改密码，注销账号若成功执行 都会导致自动退出登录
- 用户名限制2-6位，且具有唯一性，为方便区分，限制中文和大写英文字母
- **使用注意：**客户端 非正常退出 可能 导致程序异常，
- **正常退出流程**：主页输入6退出到首页，再输入4，结束程序。
- 服务端只能手动 强制停止运行
- 测试单元可删除不影响功能。
- 登录只能单点登录，不可多端登录。
- 找回密码会给你注册时绑定的邮箱发送验证码，若填写的邮箱不存在或不可接受验证码，会导致验证码发送失败。
- 发送邮件用了两个jar包，`commons-email-1.5.jar`, `mail.jar`
- 配置文件中可更改发送邮箱的地址等等，自行配置
- 由于流对象ObjectInputStream一直出报错 无法得以解决，所以导入了fastjson代替，将对象转为json字符串发送，接收到后再转为相应类型对象。  错误类型：`java.io.StreamCorruptedException: invalid stream header`和` invalid type code`，以后有时间再看看吧。
- 但是少许功能还是使用了流对象，但是都是单向发送，比如都是客户端向服务端发送，到目前测试未发现问题。
- 数据库用的Mysql，只有一个用户表User，三个属性分别是 ，username主键，password和email，可自行建表
- 可用作Java课程设计

**整体功能结构如图**

![image-20221114121448259](http://qiniu.yujing.fit/typora_img/image-20221114121448259.png)

# 大致功能演示

## 增添管理员功能

方便测试，不用每次都去验证数据库。

![systemAdmin](http://qiniu.yujing.fit/typora_img/systemAdmin.gif)

## 系统首页

### **1. 用户注册** 

基本流程:

![register](http://qiniu.yujing.fit/typora_img/register.png)

客户端代码

```java
	private void register() {
        // 1. 获取用户输入的注册信息, 封装到User对象
        User user = HomePage.registerPage();
        // 2. 发送给服务端
        rs.send(user);
        // 3. 读取服务端返回的结果
        String message = rs.readString();
        // 4. 处理结果
        System.out.println(message);
    }
```

服务端代码

```java
	private void register() {
        // 1. 读取客户端发送过来的用户注册信息
        User user = (User) rs.read();
        // 2. 在数据库中查询是否已注册
        User userByFind = userDao.findByUsername(user.getUsername());
        System.out.print(socket.getPort() + ":" + user);
        String message;
        if (userByFind != null) {
            message = "用户已注册, 请直接登录";
        } else {
            // 3. 如未注册, 将用户的信息插入到数据库
            int i = userDao.insertUser(user);
            message = i == 1 ? "注册成功" : "注册失败";
        }
        System.out.println(message);
        // 4. 向客户端发送注册结果
        rs.sendString(message);
    }
```

效果演示1: 已注册

![register1](http://qiniu.yujing.fit/typora_img/register1.gif)

![register2](http://qiniu.yujing.fit/typora_img/register2.gif)

### 2. 登录

![login (1)](http://qiniu.yujing.fit/typora_img/login (1).gif)

![login (2)](http://qiniu.yujing.fit/typora_img/login (2).gif)

### 3. 找回密码

![findPwd (1)](http://qiniu.yujing.fit/typora_img/findPwd (1).gif)

### 4. 退出系统

![quitSystem](http://qiniu.yujing.fit/typora_img/quitSystem.gif)

## 系统主页

### 1. 查看在线用户

![lookOnlineUsers](http://qiniu.yujing.fit/typora_img/lookOnlineUsers.gif)

### 2. 私聊

![privateChat](http://qiniu.yujing.fit/typora_img/privateChat.gif)

### 3. 群聊

![groupChat](http://qiniu.yujing.fit/typora_img/groupChat.gif)

### 4. 账号注销

![delAccount](http://qiniu.yujing.fit/typora_img/delAccount.gif)

### 5. 修改密码

![modifyPwd](http://qiniu.yujing.fit/typora_img/modifyPwd.gif)

### 6. 退出登录

![quitLogin](http://qiniu.yujing.fit/typora_img/quitLogin.gif)