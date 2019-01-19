package com.qkh.learn.socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @Date 2019/1/18 17:58
 */
@ServerEndpoint("/webSocket")
@Component
public class MyWebSocket {

    private static final Logger LOGGER = LoggerFactory.getLogger(MyWebSocket.class);

    private static int onlineCount = 0;

    private static CopyOnWriteArraySet<MyWebSocket> webSockets = new CopyOnWriteArraySet<>();

    private Session session;

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        webSockets.add(this);
        addOnlineCount();
        LOGGER.info("有新连接加入，当前在线人数：{}", getOnlineCount());
        sendMessage("当前在线人数：" + getOnlineCount());
    }

    @OnClose
    public void onClose(){
        webSockets.remove(this);
        subOnlineCount();
        LOGGER.info("有一个连接关闭，目前在线人数：" + getOnlineCount());
    }

    @OnMessage
    public void onMessage(String message, Session session){
        LOGGER.info("message from client:" + message);
        for (MyWebSocket webSocket : webSockets) {
            webSocket.sendMessage(message);
        }
    }

    @OnError
    public void onError(Session session, Throwable error){
        LOGGER.info("error happen!!!");
        error.printStackTrace();
    }


    private void subOnlineCount() {
        MyWebSocket.onlineCount--;
    }

    private void sendMessage(String message) {
        try {
            this.session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            LOGGER.error("send message failed", e);
        }
    }

    private int getOnlineCount() {
        return onlineCount;
    }

    private void addOnlineCount() {

        MyWebSocket.onlineCount++;
    }

}
