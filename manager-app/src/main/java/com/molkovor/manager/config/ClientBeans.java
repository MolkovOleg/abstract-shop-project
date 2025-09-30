package com.molkovor.manager.config;

import com.molkovor.manager.client.RestClientProductsRestClientImpl;
import com.molkovor.manager.config.security.OAuth2ClientHttpRequestInterceptor;
import de.codecentric.boot.admin.client.registration.BlockingRegistrationClient;
import de.codecentric.boot.admin.client.registration.RegistrationClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

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

    @Bean
    @ConditionalOnProperty(name = "spring.boot.admin.client.enabled", havingValue = "true")
    public RegistrationClient registrationClient(
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientService authorizedClientService) {

        AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientManager =
                new AuthorizedClientServiceOAuth2AuthorizedClientManager(
                        clientRegistrationRepository, authorizedClientService);

        RestTemplate restTemplate = new RestTemplateBuilder()
                .interceptors((request, body, execution) -> {
                    if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                        OAuth2AuthorizedClient authorizeClient = authorizedClientManager.authorize(
                                OAuth2AuthorizeRequest.withClientRegistrationId("metrics")
                                        .principal("manager-app-metrics-service")
                                        .build());
                        request.getHeaders().setBearerAuth(authorizeClient.getAccessToken().getTokenValue());
                    }

                    return execution.execute(request, body);
                })
                .build();

        return new BlockingRegistrationClient(restTemplate);
    }
}
