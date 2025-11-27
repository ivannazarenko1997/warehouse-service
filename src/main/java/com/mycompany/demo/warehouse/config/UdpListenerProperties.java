package com.mycompany.demo.warehouse.config;

import com.mycompany.demo.warehouse.enums.SensorType;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "app.udp")
public class UdpListenerProperties {


    private List<ListenerConfig> listeners;

    public List<ListenerConfig> getListeners() {
        return listeners;
    }

    public void setListeners(List<ListenerConfig> listeners) {
        this.listeners = listeners;
    }

  @ToString
    public static class ListenerConfig {
        private int port;
        private SensorType type;
        private String bindHost;

        public int getPort() { return port; }
        public void setPort(int port) { this.port = port; }

        public SensorType getType() { return type; }
        public void setType(SensorType type) { this.type = type; }

        public String getBindHost() { return bindHost; }
        public void setBindHost(String bindHost) { this.bindHost = bindHost; }
    }
}