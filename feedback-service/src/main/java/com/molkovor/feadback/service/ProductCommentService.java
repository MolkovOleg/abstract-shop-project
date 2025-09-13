package com.molkovor.feadback.service;

import com.molkovor.feadback.entity.ProductComment;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductCommentService {

    Mono<ProductComment> createProductComment(int productId, int rating, String comment, String userId);

    Flux<ProductComment> findAllProductCommentsByProductId(int productId);
}
