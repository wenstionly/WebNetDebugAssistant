package cc.lj1.tools.webnetdebugassistant.ws;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.websocket.EncodeException;
import javax.websocket.Session;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class WebSocketSessionHolder {
    private Map<String, List<Session>> sessionPools = new ConcurrentHashMap<>();

    public void addSession(String sid, Session session) {
        synchronized (sessionPools) {
            List<Session> list = sessionPools.get(sid);
            if(list == null) {
                list = new ArrayList<>();
                sessionPools.put(sid, list);
            }
            list.add(session);
        }
    }

    public void removeSession(String sid, Session session) {
        synchronized (sessionPools) {
            List<Session> list = sessionPools.get(sid);
            if(list != null) {
                if(list.contains(session))
                    list.remove(session);
            }
        }
    }

    public void sendTo(String sid, WebSocketPackage webSocketPackage) {
        log.info("ws.sendTo {}", sid);
        synchronized (sessionPools) {
            List<Session> sessions = sessionPools.get(sid);
            if(sessions != null) {
                for(Session session : sessions) {
                    try {
                        session.getBasicRemote().sendText(new ObjectMapper().writeValueAsString(webSocketPackage));
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
//                    catch (EncodeException e) {
//                        e.printStackTrace();
//                    }
                }
            }
        }
    }

    public void sendTo(String sid, String message) {
        synchronized (sessionPools) {
            List<Session> sessions = sessionPools.get(sid);
            if(sessions != null) {
                for(Session session : sessions) {
                    try {
                        session.getBasicRemote().sendText(message);
                    }
                    catch (IOException e) {

                    }
                }
            }
        }
    }

    public void sendTo(String sid, Object data) {
        synchronized (sessionPools) {
            List<Session> sessions = sessionPools.get(sid);
            if(sessions != null) {
                for(Session session : sessions) {
                    try {
                        session.getBasicRemote().sendObject(data);
                    }
                    catch (EncodeException e) {

                    }
                    catch (IOException e) {

                    }
                }
            }
        }
    }
}
