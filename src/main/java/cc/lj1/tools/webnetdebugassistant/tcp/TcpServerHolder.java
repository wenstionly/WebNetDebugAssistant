package cc.lj1.tools.webnetdebugassistant.tcp;

import cc.lj1.tools.webnetdebugassistant.config.ServiceConfiguration;
import org.apache.tomcat.util.collections.SynchronizedQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TcpServerHolder {
    @Autowired
    ServiceConfiguration configuration;

    Map<Integer, TcpServer> serverPools = new ConcurrentHashMap<>();
//    List<TcpServer> serverPools = Collections.synchronizedList(new ArrayList<>());
    Map<String, List<TcpServer>> serverPoolsForSession = new ConcurrentHashMap<>();

    public TcpServerInfo create(String sid, int port) throws Exception {
        final int minPort = configuration.getTcp().getMinPort();
        final int maxPort = configuration.getTcp().getMaxPort();
        final int maxServer = configuration.getTcp().getMaxServer();
        final int maxServerPerSession = configuration.getTcp().getMaxServerPerSession();

        synchronized (serverPools) {
            if(serverPools.size() >= maxServer)
                throw new Exception("已达最大服务数量[" + maxServer + "]，请等待其他用户关闭服务后再试");

            if(port == 0) {
                for(port = minPort; port <= maxPort; port++) {
                    if(!serverPools.containsKey(port))
                        break;
                }
                if(port > maxPort)
                    throw new Exception("无可用端口，请关闭一些服务或等待其他用户关闭服务，稍后再试");
            }
            if(port < minPort || port > maxPort)
                throw new Exception("端口号超出允许的范围("+minPort+"~"+maxPort+")!");

            if(serverPools.containsKey(port))
                throw new Exception("端口已被占用");

            synchronized (serverPoolsForSession) {
                List<TcpServer> servers = serverPoolsForSession.get(sid);
                if (servers == null) {
                    servers = new ArrayList<>();
                    serverPoolsForSession.put(sid, servers);
                }
                if (servers.size() >= maxServerPerSession)
                    throw new Exception("已达单个用户最大服务数量[" + maxServerPerSession + "]，请关闭一些服务后再试");

                TcpServer server = new TcpServer(sid, port);
                server.start();
                servers.add(server);
                serverPools.put(port, server);
                return new TcpServerInfo(server);
            }
        }
    }

    public void remove(String sid, int port) throws Exception {
        synchronized (serverPools) {
            if(!serverPools.containsKey(port))
                throw new Exception("服务不存在，请检查");

            synchronized (serverPoolsForSession) {
                List<TcpServer> servers = serverPoolsForSession.get(sid);
                if (servers != null) {
                    Iterator<TcpServer> iterator = servers.iterator();
                    while(iterator.hasNext()) {
                        TcpServer server = iterator.next();
                        if(server.getPort() == port) {
                            iterator.remove();
                            serverPools.remove(port);
                            server.close();
                        }
                    }
                }
            }
        }
    }

    public void remove(TcpServer server) {
        synchronized (serverPools) {
            serverPools.remove(server.getPort());
            synchronized (serverPoolsForSession) {
                List<TcpServer> servers = serverPoolsForSession.get(server.sid);
                if (servers != null) {
                    Iterator<TcpServer> iterator = servers.iterator();
                    while(iterator.hasNext()) {
                        TcpServer s = iterator.next();
                        if(s.equals(server)) {
                            iterator.remove();
                            try {
                                server.close();
                            }
                            catch (IOException e) {}
                        }
                    }
                }
            }
        }
    }

    public void offline(String sid, Long id) {
        synchronized (serverPoolsForSession) {
            List<TcpServer> servers = serverPoolsForSession.get(sid);
            if (servers != null) {
                Iterator<TcpServer> iterator = servers.iterator();
                while(iterator.hasNext()) {
                    TcpServer server = iterator.next();
                    server.closeClient(id);
                }
            }
        }
    }
    public void sendData(String sid, List<Long> id, byte[] data) {
        synchronized (serverPoolsForSession) {
            List<TcpServer> servers = serverPoolsForSession.get(sid);
            if(servers != null) {
                Iterator<TcpServer> iterator = servers.iterator();
                while(iterator.hasNext()) {
                    TcpServer server = iterator.next();
                    server.sendTo(id, data);
                }
            }
        }
    }

    public List<TcpServerInfo> getList(String sid) {
        synchronized (serverPoolsForSession) {
            List<TcpServerInfo> infos = new ArrayList<>();
            List<TcpServer> servers = serverPoolsForSession.get(sid);
            if(servers != null) {
                for(TcpServer server : servers) {
                    infos.add(new TcpServerInfo(server));
                }
            }
            return infos;
        }
    }

    public boolean isActive() {
        synchronized (serverPoolsForSession) {
            return !serverPoolsForSession.isEmpty();
        }
    }
}
