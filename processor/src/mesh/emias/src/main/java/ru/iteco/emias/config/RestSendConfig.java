package ru.iteco.emias.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.iteco.emias.service.rest.SendMessage;

@Configuration
public class RestSendConfig {
    @Value(value = "${ip.processing}")
    private String targetUrl;

    @Bean
    public SendMessage setAdressProcessing(){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setUrl(targetUrl + "processor/emias_internal/kafkaEmias");
        return sendMessage;
    }
}
