package cc.lj1.tools.webnetdebugassistant.controller;

import cc.lj1.tools.webnetdebugassistant.config.ServiceConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class BaseController {

    @Autowired
    ServiceConfiguration configuration;

    @GetMapping("/info")
    public Object getServerInfo() {
        return configuration;
    }

}
