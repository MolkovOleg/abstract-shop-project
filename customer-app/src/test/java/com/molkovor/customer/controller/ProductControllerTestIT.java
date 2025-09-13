package com.molkovor.customer.controller;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockUser;

@SpringBootTest
@AutoConfigureWebTestClient
@WireMockTest(httpPort = 54321)
class ProductControllerTestIT {

    @Autowired
    WebTestClient webTestClient;

    @Test
    void addToFavourites_RequestIsValid_ReturnsRedirectionToProductPage() {
        // given
        WireMock.stubFor(WireMock.get("/catalogue-api/products/1")
                .willReturn(WireMock.okJson(
                                """
                                        {
                                          "id": 1,
                                          "title": "Новый товар №1",
                                          "details": "Оисание товара №1"
                                        }
                                        """)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));

        WireMock.stubFor(WireMock.post("/feedback-api/favourite-products")
                .withRequestBody(WireMock.equalToJson(
                        """
                                {
                                  "productId": 1
                                }
                                """))
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
                .willReturn(WireMock.created()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("""
                                {
                                  "id": "C15332DA-808C-416A-965A-21D25BBBA93B",
                                  "productId": 1
                                }
                                """)));

        // when
        webTestClient
                .mutateWith(mockUser())
                .mutateWith(csrf())
                .post()
                .uri("/customer/products/1/add-to-favourites")
                // then
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().location("/customer/products/1");

        WireMock.verify(getRequestedFor(urlMatching("/catalogue-api/products/1")));
        WireMock.verify(postRequestedFor(urlMatching("/feedback-api/favourite-products"))
                .withRequestBody(WireMock.equalToJson("""
                        {
                          "productId": 1
                        }
                        """)));
    }

    @Test
    void addToFavourites_ProductDoesntExist_ReturnsNonFoundPage() {
        // given

        // when
        webTestClient
                .mutateWith(mockUser())
                .mutateWith(csrf())
                .post()
                .uri("/customer/products/1/add-to-favourites")
                // then
                .exchange()
                .expectStatus().isNotFound();

        WireMock.verify(getRequestedFor(urlMatching("/catalogue-api/products/1")));
    }
}