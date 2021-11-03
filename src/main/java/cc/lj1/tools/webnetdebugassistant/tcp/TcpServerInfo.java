package cc.lj1.tools.webnetdebugassistant.tcp;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class TcpServerInfo {
    private String sid;
    private String address;
    private Integer port;
    private List<TcpClientInfo> clients;

    public TcpServerInfo(TcpServer server) {
        this.sid = server.sid;
        this.address = server.getAddress();
        this.port = server.port;
        this.clients = server.getClientsInfo();
    }
}
