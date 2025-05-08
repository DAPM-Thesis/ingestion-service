//// src/main/java/com/dapm2/ingestion_service/kafka/KafkaProducerConfig.java
//package com.dapm2.ingestion_service.kafka;
//
//import communication.message.impl.event.Event;
//import org.apache.kafka.clients.producer.ProducerConfig;
//import org.apache.kafka.common.serialization.StringSerializer;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.kafka.core.*;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@Configuration
//public class KafkaProducerConfig {
//
//    @Value("${spring.kafka.bootstrap-servers}")
//    private String bootstrapServers;
//
//    @Bean
//    public ProducerFactory<String, Event> eventProducerFactory() {
//        Map<String, Object> props = new HashMap<>();
//        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
//        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,   StringSerializer.class);
//        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, EventSerializer.class);
//
//        DefaultKafkaProducerFactory<String, Event> factory =
//                new DefaultKafkaProducerFactory<>(props);
//        // override with our instance
//        factory.setValueSerializer(new EventSerializer());
//        return factory;
//    }
//
//    @Bean
//    public KafkaTemplate<String, Event> eventKafkaTemplate() {
//        return new KafkaTemplate<>(eventProducerFactory());
//    }
//}
