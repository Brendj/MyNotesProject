package ru.iteco.meshsync.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ru.iteco.client.ApiClient;
import ru.iteco.client.api.*;

@Configuration
public class RestMeshClientConfig {
    private static ApiClient apiClient;

    @Value(value = "${client.targeturl}")
    private String targetUrl;

    @Value(value = "${client.X-Api-Key}")
    private String apiKey;

    @Bean
    public ApiClient apiClient(){
        if(apiClient == null) {
            apiClient = new ApiClient();
            apiClient.setApiKey(apiKey);
            apiClient.setBasePath(targetUrl);
        }
        return apiClient;
    }

    @Bean
    public CategoryApi categoryApi(){
        return new CategoryApi(apiClient());
    }

    @Bean
    public ClassApi classApi(){
        return new ClassApi(apiClient());
    }

    @Bean
    public CreateApi createApi(){
        return new CreateApi(apiClient());
    }

    @Bean
    public DeleteApi deleteApi(){
        return new DeleteApi(apiClient());
    }

    @Bean
    public GetApi getApi(){
        return new GetApi(apiClient());
    }

    @Bean
    public PersonApi personApi(){
        return new PersonApi(apiClient());
    }

    @Bean
    public SearchApi searchApi(){
        return new SearchApi(apiClient());
    }

    @Bean
    public TransactionApi transactionApi(){
        return new TransactionApi(apiClient());
    }

    @Bean
    public UpdateApi updateApi(){
        return new UpdateApi(apiClient());
    }
}
