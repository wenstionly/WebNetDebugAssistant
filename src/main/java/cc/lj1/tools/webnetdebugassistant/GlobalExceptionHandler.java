package cc.lj1.tools.webnetdebugassistant;

import cc.lj1.tools.webnetdebugassistant.tools.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public void commonHandler(HttpServletResponse response, Exception e) {
        response.setStatus(500);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        try {
            response.getWriter().write(new ObjectMapper().writeValueAsString(new Response(500, e.getMessage())));
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }
}
