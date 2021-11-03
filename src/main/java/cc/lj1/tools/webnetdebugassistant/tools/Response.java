package cc.lj1.tools.webnetdebugassistant.tools;

import lombok.Data;

@Data
public class Response {
    private Integer code;
    private String message;

    public Response(String message) {
        this.code = 200;
        this.message = message;
    }

    public Response(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
