package ru.axetta.ecafe.processor.core.push.kafka.config;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
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
    public static final String MESH_KAFKA_LOGIN_PROPERTY = "ecafe.processing.mesh.kafka.login";
    public static final String MESH_KAFKA_PASSWORD_PROPERTY = "ecafe.processing.mesh.kafka.password";
    private final String bootstrapServer;

    public KafkaProducerConfig() {
        this.bootstrapServer = getServiceAddress();
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaStringJsonTemplate() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT");
        props.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
        props.put(SaslConfigs.SASL_JAAS_CONFIG,
                String.format("org.apache.kafka.common.security.plain.PlainLoginModule required " +
                        "username=\"%s\" password=\"%s\";", getServiceLogin(), getServicePassword()));
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

    private String getServiceLogin() {
        String login = RuntimeContext.getInstance().getConfigProperties()
                .getProperty(MESH_KAFKA_LOGIN_PROPERTY, "");
        if (login.equals(""))
            log.error(String.format("Kafka login not specified, loginLink: %s",
                    MESH_KAFKA_LOGIN_PROPERTY));
        return login;
    }

    private String getServicePassword() {
        String password = RuntimeContext.getInstance().getConfigProperties()
                .getProperty(MESH_KAFKA_PASSWORD_PROPERTY, "");
        if (password.equals(""))
            log.error(String.format("Kafka password not specified, passwordLink: %s",
                    MESH_KAFKA_PASSWORD_PROPERTY));
        return password;
    }
}