package com.molkovor.feadback.controller;

import com.molkovor.feadback.entity.ProductComment;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;
import static org.springframework.web.reactive.function.client.ExchangeFilterFunction.ofRequestProcessor;

@SpringBootTest
@AutoConfigureWebTestClient
@Slf4j
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
class ProductCommentRestControllerIT {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ReactiveMongoTemplate reactiveMongoTemplate;

    @BeforeEach
    void setUp() {
        reactiveMongoTemplate.insertAll(List.of(
                new ProductComment(UUID.fromString("DBD198A2-8876-48C4-B2B1-351A9D9C2CA4"), 1,
                        1, "comment", "user-1"),
                new ProductComment(UUID.fromString("71657810-8211-4283-9CCB-4E02F53A260D"), 1,
                        5, "comment", "user-2"),
                new ProductComment(UUID.fromString("C0ACAC27-E009-45EE-83A5-7C9993F4DD85"), 1,
                        5, "comment", "user-3")
        )).blockLast();
    }

    @AfterEach
    void tearDown() {
        reactiveMongoTemplate.remove(ProductComment.class).all().block();
    }

    @Test
    void findAllProductCommentsByProductId_ReturnsComments() {
        // then
        webTestClient.mutateWith(mockJwt())
                .mutate().filter(ofRequestProcessor(clientRequest -> {
                    log.info("======== REQUEST ========");
                    log.info("{} {}", clientRequest.method(), clientRequest.url());
                    clientRequest.headers().forEach((header, values) ->
                            log.info("{}: {}", header, values));
                    log.info("====== END REQUEST =======");
                    return Mono.just(clientRequest);
                }))
                .build()
                .get()
                .uri("/feedback-api/product-comments/by-productId/1")
                .exchange()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody()
                .json("""
                        [
                            {
                                "id": "dbd198a2-8876-48c4-b2b1-351a9d9c2ca4", 
                                "productId": 1,
                                "rating": 1, 
                                "comment": "comment", 
                                "userId": "user-1"
                            },
                            {
                                "id": "71657810-8211-4283-9ccb-4e02f53a260d", 
                                "productId": 1,
                                "rating": 5, 
                                "comment": "comment", 
                                "userId": "user-2"
                            },
                            {
                                "id": "c0acac27-e009-45ee-83a5-7c9993f4dd85", 
                                "productId": 1,
                                "rating": 5, 
                                "comment": "comment", 
                                "userId": "user-3"
                            }
                        ]
                        """)
                .returnResult();
    }

    @Test
    void createProductComment_RequestIsValid_ReturnsCreatedProductComment() {
        // when
        webTestClient
                .mutateWith(mockJwt().jwt(builder -> builder.subject("user-tester")))
                .post()
                .uri("/feedback-api/product-comments")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                            "productId": 1,
                            "rating": 5,
                            "comment": "on five!"                                                         
                        }
                        """)
                // then
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().exists(HttpHeaders.LOCATION)
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody()
                .json("""
                        {
                            "productId": 1,
                            "rating": 5,
                            "comment": "on five!",
                            "userId": "user-tester"                                                         
                        }
                        """).jsonPath("$.id").exists()
                .consumeWith(document("feedback/product_comments/createProductComment",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("productId").type("int").description("ID продукта"),
                                fieldWithPath("rating").type("int").description("Оценка продукта"),
                                fieldWithPath("comment").type("String").description("Комментарий")
                        ),
                        responseFields(
                                fieldWithPath("id").type("UUID").description("ID комментария"),
                                fieldWithPath("productId").type("int").description("ID продукта"),
                                fieldWithPath("rating").type("int").description("Оценка п   родукта"),
                                fieldWithPath("comment").type("String").description("Комментарий"),
                                fieldWithPath("userId").type("String").description("ID пользователя")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("Ссылка на созданный комментарий товара")
                        )
                ));
    }

    @Test
    void createProductComment_RequestIsInValid_ReturnsBadRequest() {
        // when
        webTestClient
                .mutateWith(mockJwt().jwt(builder -> builder.subject("user-tester")))
                .post()
                .uri("/feedback-api/product-comments")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                            "productId": null,
                            "rating": -1,
                            "comment": "Lorem ipsum dolor sit amet consectetur adipiscing elit. Semper vel class aptent taciti sociosqu ad litora. Blandit quis suspendisse aliquet nisi sodales consequat magna. Cras eleifend turpis fames primis vulputate ornare sagittis. Sem placerat in id cursus mi pretium tellus. Orci varius natoque penatibus et magnis dis parturient. Finibus facilisis dapibus etiam interdum tortor ligula congue. Proin libero feugiat tristique accumsan maecenas potenti ultricies. Sed diam urna tempor pulvinar vivamus fringilla lacus. Eros lobortis nulla molestie mattis scelerisque maximus eget. Porta elementum a enim euismod quam justo lectus. Curabitur facilisi cubilia curae hac habitasse platea dictumst. Nisl malesuada lacinia integer nunc posuere ut hendrerit. Efficitur laoreet mauris pharetra vestibulum fusce dictum risus. Imperdiet mollis nullam volutpat porttitor ullamcorper rutrum gravida. Adipiscing elit quisque faucibus ex sapien vitae pellentesque. Ad litora torquent per conubia nostra inceptos himenaeos. Consequat magna ante condimentum neque at luctus nibh. Ornare sagittis vehicula praesent dui felis venenatis ultrices. Pretium tellus duis convallis tempus leo eu aenean. Dis parturient montes nascetur ridiculus mus donec rhoncus. Ligula congue sollicitudin erat viverra ac tincidunt nam. Potenti ultricies habitant morbi senectus netus suscipit auctor. Fringilla lacus nec metus bibendum egestas iaculis massa. Maximus eget fermentum odio phasellus non purus est. Justo lectus commodo augue arcu dignissim velit aliquam. Platea dictumst lorem ipsum dolor sit amet consectetur. Ut hendrerit semper vel class aptent taciti sociosqu. Dictum risus blandit quis suspendisse aliquet nisi sodales. Rutrum gravida cras eleifend turpis fames primis vulputate. Vitae pellentesque sem placerat in id cursus mi. Inceptos himenaeos orci varius natoque penatibus et magnis. Luctus nibh finibus facilisis dapibus etiam interdum tortor. Venenatis ultrices proin libero feugiat tristique accumsan maecenas. Eu aenean sed diam urna tempor pulvinar vivamus. Donec rhoncus eros lobortis nulla molestie mattis scelerisque. Tincidunt nam porta elementum a enim euismod quam. Suscipit auctor curabitur facilisi cubilia curae hac habitasse. Iaculis massa nisl malesuada lacinia integer nunc posuere. Purus est efficitur laoreet mauris pharetra vestibulum fusce. Velit aliquam imperdiet mollis nullam volutpat porttitor ullamcorper. Amet consectetur adipiscing elit quisque faucibus ex sapien. Taciti sociosqu ad litora torquent per conubia nostra. Nisi sodales consequat magna ante condimentum neque at. Primis vulputate ornare sagittis vehicula praesent dui felis. Cursus mi pretium tellus duis convallis tempus leo. Et magnis dis parturient montes nascetur ridiculus mus. Interdum tortor ligula congue sollicitudin erat viverra ac. Accumsan maecenas potenti ultricies habitant morbi senectus netus. Pulvinar vivamus fringilla lacus nec metus bibendum egestas."                                                         
                        }
                        """)
                // then
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().doesNotExist(HttpHeaders.LOCATION)
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody()
                .json("""
                        {
                            "errors" : [
                            "Товар не указан",
                            "Оценка меньше 1",
                            "Отзыв не должен превышать 1000 символов"
                            ]                                                       
                        }
                        """);
    }

    @Test
    void createProductComment_UserIsNotAuthenticated_ReturnsNotAuthorized() {
        // when
        webTestClient
                .post()
                .uri("/feedback-api/product-comments")
                .exchange()
                .expectStatus().isUnauthorized();

    }
}