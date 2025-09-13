package com.molkovor.feadback.service;

import com.molkovor.feadback.entity.FavouriteProduct;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FavouriteProductService {

    Mono<FavouriteProduct> addProductToFavourites(int productId, String userId);

    Mono<Void> removeProductFromFavourites(int productId, String userId);

    Mono<FavouriteProduct> findFavouriteProductByProductIdAndUserId(int productId, String userId);

    Flux<FavouriteProduct> findFavouriteProducts(String userId);
}