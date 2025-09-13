package com.molkovor.customer.client;

import com.molkovor.customer.client.dto.NewFavouriteProductDto;
import com.molkovor.customer.client.exception.ClientBadRequestException;
import com.molkovor.customer.entity.FavouriteProduct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ProblemDetail;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class WebClientFavouriteProductsClient implements FavouriteProductsClient {

    private final WebClient webClient;

    @Override
    public Mono<FavouriteProduct> findFavouriteProductByProductId(int productId) {
        return webClient
                .get()
                .uri("/feedback-api/favourite-products/by-productId/{productId}", productId)
                .retrieve()
                .bodyToMono(FavouriteProduct.class)
                .onErrorComplete(WebClientResponseException.NotFound.class);
    }

    @Override
    public Mono<FavouriteProduct> addProductToFavourites(int productId) {
        return webClient
                .post()
                .uri("/feedback-api/favourite-products")
                .bodyValue(new NewFavouriteProductDto(productId))
                .retrieve()
                .bodyToMono(FavouriteProduct.class)
                .onErrorMap(WebClientResponseException.BadRequest.class,
                        ex -> new ClientBadRequestException("Возникла ошибка при добавлении товара в избранное",
                                ex, ((List<String>) ex.getResponseBodyAs(ProblemDetail.class)
                                .getProperties().get("errors"))));

    }

    @Override
    public Flux<FavouriteProduct> findFavouriteProducts() {
        return webClient
                .get()
                .uri("/feedback-api/favourite-products")
                .retrieve()
                .bodyToFlux(FavouriteProduct.class);
    }

    @Override
    public Mono<Void> removeProductFromFavourites(int productId) {
        return webClient
                .delete()
                .uri("/feedback-api/favourite-products/by-productId/{productId}", productId)
                .retrieve()
                .toBodilessEntity()
                .then();
    }
}
