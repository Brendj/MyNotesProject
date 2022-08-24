package ru.axetta.ecafe.processor.core.pull.kafka.config;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;
import ru.axetta.ecafe.processor.core.RuntimeContext;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {

    private final Logger log = LoggerFactory.getLogger(KafkaConsumerConfig.class);
    public static final String MESH_KAFKA_ADDRESS_PROPERTY = "ecafe.processing.mesh.kafka.address";
    public static final String MESH_KAFKA_LOGIN_PROPERTY = "ecafe.processing.mesh.kafka.login";
    public static final String MESH_KAFKA_PASSWORD_PROPERTY = "ecafe.processing.mesh.kafka.password";

    private final String bootstrapServer;

    public KafkaConsumerConfig() {
        this.bootstrapServer = getServiceAddress();
    }

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "pp_group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT");
        props.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
        props.put(SaslConfigs.SASL_JAAS_CONFIG,
                String.format("org.apache.kafka.common.security.plain.PlainLoginModule required " +
                        "username=\"%s\" password=\"%s\";", getServiceLogin(), getServicePassword()));
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory
                = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setBatchListener(false);
        return factory;
    }

    @Bean
    public StringJsonMessageConverter converter() {
        return new StringJsonMessageConverter();
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