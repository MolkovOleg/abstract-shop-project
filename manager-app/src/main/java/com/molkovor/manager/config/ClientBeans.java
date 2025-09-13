package com.molkovor.manager.config;

import com.molkovor.manager.client.RestClientProductsRestClientImpl;
import com.molkovor.manager.config.security.OAuth2ClientHttpRequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.web.client.RestClient;

@Configuration
public class ClientBeans {

    @Bean
    RestClientProductsRestClientImpl productsRestClient(
            @Value("${shop.services.catalogue.url:http://localhost:8081}") String catalogueBaseUrl,
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientRepository authorizedClientRepository,
            @Value("${shop.services.catalogue.registrationId:keycloak}") String registrationId) {

        return new RestClientProductsRestClientImpl(RestClient.builder()
                .baseUrl(catalogueBaseUrl)
                .requestInterceptor(
                        new OAuth2ClientHttpRequestInterceptor(
                                new DefaultOAuth2AuthorizedClientManager(clientRegistrationRepository,
                                        authorizedClientRepository), registrationId))
                .build());
    }
}
