package cc.lj1.tools.webnetdebugassistant.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("service")
public class ServiceConfiguration {
    private String serverIp = "";
    private TcpConfiguration tcp = new TcpConfiguration();

    @Data
    public class TcpConfiguration {
        private Integer minPort = 10000;
        private Integer maxPort = 20000;

        private Integer maxServer = 500;
        private Integer maxServerPerSession = 1;
        private Integer maxClientPerServer = 10;
    }
}
