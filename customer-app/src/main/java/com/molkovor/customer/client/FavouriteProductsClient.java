package com.molkovor.customer.client;

import com.molkovor.customer.entity.FavouriteProduct;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FavouriteProductsClient {

    Mono<FavouriteProduct> findFavouriteProductByProductId(int productId);

    Mono<FavouriteProduct> addProductToFavourites(int productId);

    Flux<FavouriteProduct> findFavouriteProducts();

    Mono<Void> removeProductFromFavourites(int productId);
}
