package com.molkovor.feadback.controller;

import com.molkovor.feadback.controller.dto.NewProductCommentDto;
import com.molkovor.feadback.entity.ProductComment;
import com.molkovor.feadback.service.ProductCommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@RestController
@RequiredArgsConstructor
@RequestMapping("feedback-api/product-comments")
@Slf4j
public class ProductCommentRestController {

    private final ProductCommentService productCommentService;

    @GetMapping("by-productId/{productId:\\d+}")
    @Operation(
            security = @SecurityRequirement(name = "keycloak")
    )
    public Flux<ProductComment> findAllProductCommentsByProductId(@PathVariable("productId") int productId) {

        return productCommentService.findAllProductCommentsByProductId(productId);
    }

    @PostMapping
    public Mono<ResponseEntity<ProductComment>> createProductComment(
            Mono<JwtAuthenticationToken> authenticationToken,
            @Valid @RequestBody Mono<NewProductCommentDto> dtoMono,
            UriComponentsBuilder uriBuilder) {

        return authenticationToken.flatMap(token -> dtoMono
                        .flatMap(dto -> productCommentService.createProductComment(dto.productId(),
                                dto.rating(), dto.comment(), token.getToken().getSubject())))
                .map(pComment -> ResponseEntity
                        .created(uriBuilder
                                .replacePath("/feedback-api/product-comments/{commentId}")
                                .build(pComment.getId()))
                        .body(pComment));
    }
}