package cc.lj1.tools.webnetdebugassistant.tcp;

import lombok.Data;


@Data
public class TcpClientInfo {
    private String id;
    private String address;
    private Integer port;

    public TcpClientInfo(TcpServer.ClientThread client) {
        this.id = String.valueOf(client.getId());
        String strAddress = client.getSocket().getInetAddress().toString();
        Integer port = client.getSocket().getPort();
//        String test = client.getSocket().getRemoteSocketAddress().toString();
//        System.out.println("getRemoteSocketAddress >> " + test);
//        System.out.println("getInetAddress >> " + strAddress);
//        System.out.println("getPort >> " + port);
        this.address = strAddress.replace("/", "");
        this.port = port;
    }
}
