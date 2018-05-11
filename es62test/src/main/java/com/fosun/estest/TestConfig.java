package com.fosun.estest;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.sniff.Sniffer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestConfig {

    @Bean(destroyMethod = "close")
    public RestHighLevelClient getRestHighLevelClient(){

        RestHighLevelClient restClient = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("192.168.2.4", 9200, "http")
                        //new HttpHost("192.168.2.251", 9200, "http")
                ));

        return restClient;
    }

    @Bean(destroyMethod = "close")
    public Sniffer getSniffer() {
        Sniffer sniffer = Sniffer.builder(getRestHighLevelClient().getLowLevelClient())
                .setSniffIntervalMillis(60000).build();

        return sniffer;
    }

//    @Bean
//    public RestClient getRestClient() {
//        RestClient restClient = RestClient.builder(
//                new HttpHost("localhost", 9200, "http"))
//                .build();
//
//        return restClient;
//    }
}
