package com.molkovor.feadback.repository;

import com.molkovor.feadback.entity.ProductComment;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ProductCommentRepository
        extends ReactiveCrudRepository<ProductComment, UUID> {

    Flux<ProductComment> findAllByProductId(int productId);

}
