package com.molkovor.feadback.controller;

import com.molkovor.feadback.controller.dto.NewProductCommentDto;
import com.molkovor.feadback.entity.ProductComment;
import com.molkovor.feadback.service.ProductCommentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductReviewsRestControllerTest {

    @Mock
    ProductCommentService productCommentService;

    @InjectMocks
    ProductCommentRestController controller;

    @Test
    void findProductReviewsByProductId_ReturnsProductReviews() {
        // given
        doReturn(Flux.fromIterable(List.of(
                new ProductComment(UUID.fromString("bd7779c2-cb05-11ee-b5f3-df46a1249898"), 1, 1,
                        "Отзыв №1", "user-1"),
                new ProductComment(UUID.fromString("be424abc-cb05-11ee-ab16-2b747e61f570"), 1, 3,
                        "Отзыв №2", "user-2"),
                new ProductComment(UUID.fromString("be77f95a-cb05-11ee-91a3-1bdc94fa9de4"), 1, 5,
                        "Отзыв №3", "user-3")
        ))).when(this.productCommentService).findAllProductCommentsByProductId(1);

        // when
        StepVerifier.create(this.controller.findAllProductCommentsByProductId(1))
                // then
                .expectNext(
                        new ProductComment(UUID.fromString("bd7779c2-cb05-11ee-b5f3-df46a1249898"), 1, 1,
                                "Отзыв №1", "user-1"),
                        new ProductComment(UUID.fromString("be424abc-cb05-11ee-ab16-2b747e61f570"), 1, 3,
                                "Отзыв №2", "user-2"),
                        new ProductComment(UUID.fromString("be77f95a-cb05-11ee-91a3-1bdc94fa9de4"), 1, 5,
                                "Отзыв №3", "user-3")
                )
                .verifyComplete();

        verify(this.productCommentService).findAllProductCommentsByProductId(1);
        verifyNoMoreInteractions(this.productCommentService);
    }

    @Test
    void createProductComment_ReturnsCreatedProductComment() {
        // given
        doReturn(Mono.just(new ProductComment(UUID.fromString("bd7779c2-cb05-11ee-b5f3-df46a1249898"),
                1, 4, "Норм", "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c")))
                .when(productCommentService).createProductComment(1, 4, "Норм",
                        "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c");
        // when
        StepVerifier.create(controller.createProductComment(
                        Mono.just(new JwtAuthenticationToken(Jwt.withTokenValue("e30.e30")
                                .headers(headers -> headers.put("foo", "bar"))
                                .claim("sub", "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c").build())),
                        Mono.just(new NewProductCommentDto(1, 4, "Норм")),
                        UriComponentsBuilder.fromUriString("http://localhost")))
                // then
                .expectNext(ResponseEntity
                        .created(URI.create("http://localhost/feedback-api/product-comments/bd7779c2-cb05-11ee-b5f3-df46a1249898"))
                        .body(new ProductComment(UUID.fromString("bd7779c2-cb05-11ee-b5f3-df46a1249898"),
                                1, 4, "Норм", "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c")))
                .verifyComplete();

        verify(productCommentService)
                .createProductComment(1, 4, "Норм",
                        "5f1d5cf8-cbd6-11ee-9579-cf24d050b47c");
        verifyNoMoreInteractions(productCommentService);
    }

}