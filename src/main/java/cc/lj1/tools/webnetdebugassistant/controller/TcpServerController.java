package cc.lj1.tools.webnetdebugassistant.controller;

import cc.lj1.tools.webnetdebugassistant.tcp.TcpServerHolder;
import cc.lj1.tools.webnetdebugassistant.tools.Response;
import cc.lj1.tools.webnetdebugassistant.tools.StringConvertUtils;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/{sid}/tcp/server/")
public class TcpServerController {
    @Autowired
    TcpServerHolder tcpServerHolder;

    @GetMapping("/list")
    public Object getTcpServerList(@PathVariable("sid") String sid) {
        return tcpServerHolder.getList(sid);
    }

    @RequestMapping("/open")
    public Object createTcpServerWithRandomPort(@PathVariable("sid") String sid) throws Exception {
        return tcpServerHolder.create(sid, 0);
    }

    @RequestMapping("/open/{port}")
    public Object createTcpServer(@PathVariable("sid") String sid, @PathVariable("port") Integer port) throws Exception {
        return tcpServerHolder.create(sid, port);
    }

    @RequestMapping("/close/{port}")
    public Object closeTcpServer(@PathVariable("sid") String sid, @PathVariable("port") Integer port) throws Exception {
        tcpServerHolder.remove(sid, port);
        return new Response("成功");
    }

    @RequestMapping("/offline/{id}")
    public Object setClientOffline(@PathVariable("sid") String sid, @PathVariable("id") Long id) throws Exception {
        tcpServerHolder.offline(sid, id);
        return new Response("成功");
    }

    @Data
    public static class SendData {
        List<Long> clients;
        String data;
    }
    @PostMapping("/send")
    public Object sendDataToClient(@PathVariable("sid") String sid, @RequestBody SendData sendData) throws Exception {
        byte[] rawData = StringConvertUtils.fromHexString(sendData.data);
        if(rawData == null)
            throw new Exception("数据不正确");
        tcpServerHolder.sendData(sid, sendData.clients, rawData);
        return new Response("成功");
    }
    @Data
    public static class SendLinesData {
        List<Long> clients;
        List<String> lines;
    }
    @PostMapping("/send/lines")
    public Object sendLinesToClient(@PathVariable("sid") String sid, @RequestBody SendLinesData linesData) throws Exception {
        for(String line : linesData.lines) {
            byte[] rawData = StringConvertUtils.fromHexString(line);
            if(rawData == null)
                throw new Exception("数据不正确");
            tcpServerHolder.sendData(sid, linesData.clients, rawData);
        }
        return new Response("成功");
    }
}
