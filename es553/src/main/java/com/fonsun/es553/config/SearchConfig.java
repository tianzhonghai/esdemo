package com.fonsun.es553.config;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;


@Configuration
public class SearchConfig {

    @Bean
    public TransportClient getTransportClient() throws Exception {
        byte[] ip = new byte[] { (byte)192,(byte)168,(byte)2,(byte)81};

        Settings settings = Settings.builder()
                .put("cluster.name", "foliday-local-dev")
                .put("client.transport.sniff", true)
                .build();

        TransportClient client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByAddress(ip), 9300));

        return client;
    }

}
