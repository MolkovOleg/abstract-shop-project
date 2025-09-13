package com.molkovor.customer.client;

import com.molkovor.customer.client.dto.NewProductCommentDto;
import com.molkovor.customer.client.exception.ClientBadRequestException;
import com.molkovor.customer.entity.ProductComment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ProblemDetail;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class WebClientProductCommentClient implements ProductCommentClient {

    private final WebClient webClient;

    @Override
    public Flux<ProductComment> findProductCommentsByProductId(Integer productId) {
        return webClient
                .get()
                .uri("/feedback-api/product-comments/by-productId/{productId}", productId)
                .retrieve()
                .bodyToFlux(ProductComment.class)
                .onErrorComplete(WebClientResponseException.NotFound.class);
    }

    @Override
    public Mono<ProductComment> createProductComment(Integer productId, Integer rating, String comment) {
        return webClient.post()
                .uri("/feedback-api/product-comments")
                .bodyValue(new NewProductCommentDto(productId, rating, comment))
                .retrieve()
                .bodyToMono(ProductComment.class)
                .onErrorMap(WebClientResponseException.BadRequest.class,
                        ex -> new ClientBadRequestException("Возникла ошибка при добавлении отзыва о товаре",
                                ex, ((List<String>) ex.getResponseBodyAs(ProblemDetail.class)
                                .getProperties().get("errors"))));

    }
}