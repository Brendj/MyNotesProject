package ru.axetta.ecafe.processor.core.push.kafka.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;
import ru.axetta.ecafe.processor.core.RuntimeContext;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaProducerConfig {

    private final Logger log = LoggerFactory.getLogger(KafkaProducerConfig.class);
    public static final String MESH_KAFKA_ADDRESS_PROPERTY = "ecafe.processing.mesh.kafka.address";
    private final String bootstrapServer;

    public KafkaProducerConfig() {
        this.bootstrapServer = getServiceAddress();
    }

    @Bean
    public <T> KafkaTemplate<String, T> kafkaStringJsonTemplate() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(props));
    }

    private String getServiceAddress() {
        String address = RuntimeContext.getInstance().getConfigProperties()
                .getProperty(MESH_KAFKA_ADDRESS_PROPERTY, "");
        if (address.equals(""))
            log.error(String.format("Kafka address not specified, addressLink: %s",
                    MESH_KAFKA_ADDRESS_PROPERTY));
        return address;
    }
}