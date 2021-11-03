package cc.lj1.tools.webnetdebugassistant.controller;

import cc.lj1.tools.webnetdebugassistant.ApplicationContextProvider;
import cc.lj1.tools.webnetdebugassistant.ws.WebSocketSessionHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@ServerEndpoint("/ws/{sid}")
@Component
public class WebSocketController {
//    private static AtomicInteger onlineNum = new AtomicInteger();

    // websocket是线程安全的，即，其工作在多线程环境下
//    @Autowired
    WebSocketSessionHolder holder = ApplicationContextProvider.getBean(WebSocketSessionHolder.class);

    @OnOpen
    public void onOpen(Session session, @PathParam("sid") String sid) {
        log.info("有新连接加入[{}]：{}", sid, session.getId());
//        sendMessage("Welcome!", session);
//        addSession(sid, session);
        holder.addSession(sid, session);
    }

    @OnClose
    public void onClose(Session session, @PathParam("sid") String sid) {
        log.info("有连接关闭：{}", session.getId());
        holder.removeSession(sid, session);
    }

    @OnMessage
    public void onMessage(String message, Session session, @PathParam("sid") String sid) {
        log.info("服务端收到客户端[{}][{}]的消息：{}", sid, session.getId(), message);
//        sendMessage("Echo: " + message, session);
    }

    @OnError
    public void onError(Session session, Throwable error, @PathParam("sid") String sid) {
        log.error("发生错误");
        error.printStackTrace();
        holder.removeSession(sid, session);
        try {
            session.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
