package com.molkovor.feadback.service;

import com.molkovor.feadback.entity.ProductComment;
import com.molkovor.feadback.repository.ProductCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DefaultProductCommentService implements ProductCommentService {

    private final ProductCommentRepository productCommentRepository;

    @Override
    public Mono<ProductComment> createProductComment(int productId, int rating, String comment, String userId) {
        return productCommentRepository.save(new ProductComment(UUID.randomUUID(), productId, rating, comment, userId));
    }

    @Override
    public Flux<ProductComment> findAllProductCommentsByProductId(int productId) {
        return productCommentRepository.findAllByProductId(productId);
    }
}
