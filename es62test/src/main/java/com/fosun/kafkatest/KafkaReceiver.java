package com.fosun.kafkatest;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
public class KafkaReceiver {

    @KafkaListener(topics = {"topic1"})
    public void listen(ConsumerRecord<String, String> record) {
        Optional<String> kafkaMessage = Optional.ofNullable(record.value());

        kafkaMessage.ifPresent((val) -> {
            log.info("consumer-groupDefault,{} - {} : {}", record.topic(), record.key(), record.value());
        });
    }

    @KafkaListener(id="key2", topics = "topic2", groupId = "test-consumer-group2")
    public void listenT2(ConsumerRecord<String,String> cr) throws Exception {
        log.info("consumer-group2,{} - {} : {}", cr.topic(), cr.key(), cr.value());
    }

}
