package com.molkovor.customer.config;

import com.molkovor.customer.client.WebClientFavouriteProductsClient;
import com.molkovor.customer.client.WebClientProductCommentClient;
import com.molkovor.customer.client.WebClientProductsClient;
import de.codecentric.boot.admin.client.config.ClientProperties;
import de.codecentric.boot.admin.client.registration.ReactiveRegistrationClient;
import de.codecentric.boot.admin.client.registration.RegistrationClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.security.oauth2.client.AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ClientBeans {

    @Bean
    @Scope("prototype")
    public WebClient.Builder shopServicesWebClientBuilder(
            ReactiveClientRegistrationRepository clientRegistrationRepository,
            ServerOAuth2AuthorizedClientRepository authorizedClientRepository
    ) {

        ServerOAuth2AuthorizedClientExchangeFilterFunction filter =
                new ServerOAuth2AuthorizedClientExchangeFilterFunction(clientRegistrationRepository,
                        authorizedClientRepository);
        filter.setDefaultClientRegistrationId("keycloak");

        return WebClient.builder()
                .filter(filter);
    }

    @Bean
    public WebClientProductsClient webClientProductsClient(
            @Value("${shop.services.catalogue.url:http://localhost:8081}") String catalogueBaseUrl,
            WebClient.Builder shopServicesWebClientBuilder
    ) {
        return new WebClientProductsClient(shopServicesWebClientBuilder
                .baseUrl(catalogueBaseUrl)
                .build());
    }

    @Bean
    public WebClientFavouriteProductsClient webClientFavouriteProductsClient(
            @Value("${shop.services.feedback.url:http://localhost:8083}") String feedbackBaseUrl,
            WebClient.Builder shopServicesWebClientBuilder
    ) {
        return new WebClientFavouriteProductsClient(shopServicesWebClientBuilder
                .baseUrl(feedbackBaseUrl)
                .build());
    }

    @Bean
    public WebClientProductCommentClient webClientProductCommentClient(
            @Value("${shop.services.feedback.url:http://localhost:8083}") String feedbackBaseUrl,
            WebClient.Builder shopServicesWebClientBuilder
    ) {
        return new WebClientProductCommentClient(shopServicesWebClientBuilder
                .baseUrl(feedbackBaseUrl)
                .build());
    }

    @Bean
    @ConditionalOnProperty(name = "spring.boot.admin.client.enabled", havingValue = "true")
    public RegistrationClient registrationClient(
            ClientProperties clientProperties,
            ReactiveClientRegistrationRepository clientRegistrationRepository,
            ReactiveOAuth2AuthorizedClientService authorizedClientService
    ) {
        AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager authorizedClientManager =
                new AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager(clientRegistrationRepository,
                        authorizedClientService);

        ServerOAuth2AuthorizedClientExchangeFilterFunction filterFunction =
                new ServerOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);
        filterFunction.setDefaultClientRegistrationId("metrics");

        return new ReactiveRegistrationClient(WebClient.builder()
                .filter(filterFunction)
                .build(), clientProperties.getReadTimeout());

    }
}
