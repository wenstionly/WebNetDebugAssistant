package cc.lj1.tools.webnetdebugassistant.config;

import cc.lj1.tools.webnetdebugassistant.tools.IdWorker;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Beans {
    @Bean
    IdWorker idWorker() {
        return new IdWorker(0, 0, 0);
    }
}
