# Java-即时聊天程序
基于socket，使用二进制文本读写模拟数据库的即时聊天程序

***

## 功能
- 新用户的注册
- 实时发送接收消息
- 公共群聊
- 文件的发送和接收
- 获取全部和在线成员列表（不支持实时获取）

## 管理员权限
- 访问日志
- 强制用户退出
- 注销用户
- 关闭服务器

***

## 运行前修改

- ClientFX：com/chensan/client/service/UserService.java
  - *修改Server IP：SERVER_IP*
  - *修改端口号 ：SERVER_PORT*
- ServerFX: com/chensan/server/utils/FileUtils.java
- ServerFX: com/chensan/server/service/WechatServer.java
  - *修改文件地址（源码采用绝对地址）*

## 运行入口

- *Cilent： com/chensan/client/viewFX/ViewStartFX.java*
- *Server： com/chensan/server/wechatframe/WeChatServerStart.java*
- Server（无界面版）： com/chensan/server/wechatframe/WechatFrame.java

***





