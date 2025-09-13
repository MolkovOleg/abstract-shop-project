package com.molkovor.feadback.repository;

import com.molkovor.feadback.entity.FavouriteProduct;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface FavouriteProductRepository
        extends ReactiveMongoRepository<FavouriteProduct, UUID> {

    Flux<FavouriteProduct> findAllByUserId(String userId);

    Mono<FavouriteProduct> findByProductIdAndUserId(int productId, String userId);

    Mono<Void> deleteByProductIdAndUserId(int productId, String userId);
}
