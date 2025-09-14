package com.molkovor.feadback.config;

import de.codecentric.boot.admin.client.config.ClientProperties;
import de.codecentric.boot.admin.client.registration.ReactiveRegistrationClient;
import de.codecentric.boot.admin.client.registration.RegistrationClient;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ClientBeans {


    @Bean
    public RegistrationClient registrationClient(
            ReactiveClientRegistrationRepository clientRegistrationRepository,
            ReactiveOAuth2AuthorizedClientService authorizedClientService,
            ClientProperties clientProperties
    ) {
        AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager authorizedClientManager =
                new AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager(
                        clientRegistrationRepository, authorizedClientService);

        ServerOAuth2AuthorizedClientExchangeFilterFunction filterFunction =
                new ServerOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);

        filterFunction.setDefaultClientRegistrationId("keycloak");

        return new ReactiveRegistrationClient(WebClient.builder()
                .filter(filterFunction)
                .build(), clientProperties.getReadTimeout());
    }
}
