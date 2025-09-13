package com.molkovor.customer.client;

import com.molkovor.customer.entity.ProductComment;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductCommentClient {

    Flux<ProductComment> findProductCommentsByProductId(Integer productId);

    Mono<ProductComment> createProductComment(Integer productId, Integer rating, String comment);
}
