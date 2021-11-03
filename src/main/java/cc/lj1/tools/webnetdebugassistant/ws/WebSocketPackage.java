package cc.lj1.tools.webnetdebugassistant.ws;

import cc.lj1.tools.webnetdebugassistant.tools.StringConvertUtils;
import lombok.Builder;
import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Date;

@Data
@Builder
public class WebSocketPackage {
    final static String EVT_NEW_SERVER = "ns";
    final static String EVT_SERVER_GONE = "sg";
    final static String EVT_NEW_CLIENT = "nc";
    final static String EVT_CLIENT_GONE = "cg";
    final static String EVT_NEW_DATA = "nd";

    private String event;
    private Integer port;
    private String id;
    private String ts;
    private String data;

    public static WebSocketPackage newServer(Integer port) {
        return WebSocketPackage.builder()
                .event(EVT_NEW_SERVER)
                .port(port)
                .ts(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").format(new Date()))
                .build();
    }

    public static WebSocketPackage serverGone(Integer port) {
        return WebSocketPackage.builder()
                .event(EVT_SERVER_GONE)
                .port(port)
                .ts(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").format(new Date()))
                .build();
    }

    public static WebSocketPackage newClient(Integer port, Long id) {
        return WebSocketPackage.builder()
                .event(EVT_NEW_CLIENT)
                .port(port)
                .id(String.valueOf(id))
                .ts(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").format(new Date()))
                .build();
    }

    public static WebSocketPackage clientGone(Integer port, Long id) {
        return WebSocketPackage.builder()
                .event(EVT_CLIENT_GONE)
                .port(port)
                .id(String.valueOf(id))
                .ts(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").format(new Date()))
                .build();
    }

    public static WebSocketPackage newData(Integer port, Long id, byte[] data) {
        return WebSocketPackage.builder()
                .event(EVT_NEW_DATA)
                .port(port)
                .id(String.valueOf(id))
                .ts(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").format(new Date()))
                .data(StringConvertUtils.toHexString(data))
                .build();
    }

    public static WebSocketPackage newData(Integer port, Long id, byte[] data, int offset, int size) {
        return WebSocketPackage.builder()
                .event(EVT_NEW_DATA)
                .port(port)
                .id(String.valueOf(id))
                .ts(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S").format(new Date()))
                .data(StringConvertUtils.toHexString(data, offset, size))
                .build();
    }

}
