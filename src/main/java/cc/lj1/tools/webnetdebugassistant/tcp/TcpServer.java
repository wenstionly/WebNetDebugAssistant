package cc.lj1.tools.webnetdebugassistant.tcp;

import cc.lj1.tools.webnetdebugassistant.ApplicationContextProvider;
import cc.lj1.tools.webnetdebugassistant.config.ServiceConfiguration;
import cc.lj1.tools.webnetdebugassistant.controller.WebSocketController;
import cc.lj1.tools.webnetdebugassistant.tools.IdWorker;
import cc.lj1.tools.webnetdebugassistant.tools.StringConvertUtils;
import cc.lj1.tools.webnetdebugassistant.ws.WebSocketPackage;
import cc.lj1.tools.webnetdebugassistant.ws.WebSocketSessionHolder;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class TcpServer extends Thread {
    private final Integer ACCEPT_TIMEOUT = 3 * 60;
    String sid;

    IdWorker idWorker;
    WebSocketSessionHolder holder;
    ServiceConfiguration configuration;
    TcpServerHolder tcpServerHolder;

    private ServerSocket serverSocket;
    int port;

    private AtomicInteger timeoutCounter = new AtomicInteger();

    private Map<Long, ClientThread> clients = new ConcurrentHashMap<>();

    public TcpServer(String sid, int port) throws IOException {
        this.sid = sid;
        this.idWorker = ApplicationContextProvider.getBean(IdWorker.class);
        this.holder = ApplicationContextProvider.getBean(WebSocketSessionHolder.class);
        this.configuration = ApplicationContextProvider.getBean(ServiceConfiguration.class);
        this.tcpServerHolder = ApplicationContextProvider.getBean(TcpServerHolder.class);
        this.port = port;
        serverSocket = new ServerSocket(port);
        serverSocket.setSoTimeout(1000);
        holder.sendTo(sid, WebSocketPackage.newServer(port));
        timeoutCounter.set(0);
    }

    public String getAddress() {
        String address = configuration.getServerIp();
        if(address == null || address.equals("")) {
            try {
                address = InetAddress.getLocalHost().getHostAddress();
            }
            catch (Exception e) {
                address = "localhost";
            }
        }
        return address;
    }
    public int getPort() {
        return port;
    }

    public void close() throws IOException {
        serverSocket.close();
        for(ClientThread thread : clients.values()) {
            thread.interrupt();
            thread.close();
        }
        clients.clear();
        holder.sendTo(sid, WebSocketPackage.serverGone(this.port));
    }

    public List<TcpClientInfo> getClientsInfo() {
        List<TcpClientInfo> infos = new ArrayList<>();
        for(Map.Entry<Long, TcpServer.ClientThread> entry : clients.entrySet()) {
            infos.add(new TcpClientInfo(entry.getValue()));
        }
        return infos;
    }

    public ClientThread getClient(long id) {
        synchronized (clients) {
            return clients.getOrDefault(id, null);
        }
    }

    public void closeClient(long id) {
        synchronized (clients) {
            if(clients.containsKey(id)) {
                clients.get(id).close();
            }
        }
    }

    public void sendTo(List<Long> idList, byte[] data) {
        synchronized (clients) {
            for(Long id : idList) {
                if(clients.containsKey(id)) {
                    clients.get(id).write(data);
                }
            }
        }
    }

    public boolean canAddClient() {
        synchronized (clients) {
            return clients.size() < configuration.getTcp().getMaxClientPerServer();
        }
    }
    public void addClient(long id, ClientThread thread) {
        holder.sendTo(sid, WebSocketPackage.newClient(port, id));
        synchronized (clients) {
            clients.put(id, thread);
        }
        timeoutCounter.set(0);
    }

    public void removeClient(long id) {
        holder.sendTo(sid, WebSocketPackage.clientGone(port, id));
        synchronized (clients) {
            clients.remove(id);
        }
        timeoutCounter.set(0);
    }

    public Map<Long, ClientThread> getClients() {
        return clients;
    }

    @Override
    public void run() {
        while(!serverSocket.isClosed()) {
            try {
                Socket client = serverSocket.accept();
                if(client != null) {
                    if(canAddClient()) {
                        Long id = idWorker.nextId();
                        ClientThread thread = new ClientThread(id, client);
                        thread.start();
                        addClient(id, thread);
                    }
                    else
                        client.close();
                }
            }
            catch (SocketTimeoutException e) {
                log.warn("accept timeout!!!");
                if(clients.size() <= 0) {
                    if(timeoutCounter.incrementAndGet() >= ACCEPT_TIMEOUT) {
                        log.warn("NO MORE CLIENTS!! SHUT DOWN SERVER");
                        tcpServerHolder.remove(this);
                        break;
                    }
                }
            }
            catch (Exception e) {
//                e.printStackTrace();
                log.warn("exception when accept " + e.getMessage());
                log.warn("status => {}", serverSocket.isClosed());
            }
        }
    }

    public class ClientThread extends Thread {
        private Long id;
        private Socket socket;
        private InputStream in;
        private OutputStream out;

        public ClientThread(Long id, Socket socket) {
            this.id = id;
            this.socket = socket;
            try {
                in = socket.getInputStream();
                out = socket.getOutputStream();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

        public long getId() {
            return id;
        }

        public Socket getSocket() {
            return socket;
        }

        public void write(byte[] message) {
            if(out != null) {
                try {
                    out.write(message);
                    out.flush();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void write(String message) {
            write(message.getBytes(StandardCharsets.UTF_8));
        }

        public void close() {
            try {
                if(in != null)
                    in.close();
                if(out != null)
                    out.close();
                if(socket != null)
                    socket.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            TcpServer.this.removeClient(id);
        }

        @SneakyThrows
        @Override
        public void run() {
            log.info("CLIENT STARTED>>>>>");
            log.info("TIMEOUT>>>" + socket.getSoTimeout());
            log.info("BufferSize>>>" + socket.getReceiveBufferSize());
            int bufferSize = socket.getReceiveBufferSize();
            if(bufferSize <= 0)
                bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            while(!isInterrupted() && !socket.isClosed()) {
                if(in == null)
                    break;
                try {
                    int size = in.read(buffer);
                    if(size > 0) {
                        String strData = StringConvertUtils.toHexString(buffer, 0, size);
                        log.info("TCP SERVER RECEIVED DATA:::" + strData);
                        holder.sendTo(sid, WebSocketPackage.newData(port, id, buffer, 0, size));
                    }
                    else {
                        log.info("in.read size = " + size);
                        break;
                    }
//                    int available = in.available();
//                    if(available > 0) {
//                        byte[] buffer = new byte[available];
//                        int size = in.read(buffer);
//                        log.info("TCP SERVER RECEIVED DATA:::" + StringConvertUtils.toHexString(buffer, 0, size));
//                        if(size > 0) {
//                            WebSocketController.sendTo(sid, StringConvertUtils.toHexString(buffer, 0, size));
//                        }
//                    }
//                    else if(available < 0) {
//                        log.info("TEST NEG");
//                    }
                }
                catch (Exception e) {
                    break;
                }
            }
            log.info("CLIENT GONE!!!");
            close();
        }

        private boolean isSocketAlive() {
            try {
                socket.sendUrgentData(0xFF);
                return true;
            }
            catch (IOException e) {
                return false;
            }
        }
    }
}
