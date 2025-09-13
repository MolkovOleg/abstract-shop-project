package com.molkovor.customer.controller;

import com.molkovor.customer.client.FavouriteProductsClient;
import com.molkovor.customer.client.ProductCommentClient;
import com.molkovor.customer.client.ProductsClient;
import com.molkovor.customer.client.exception.ClientBadRequestException;
import com.molkovor.customer.controller.dto.NewProductCommentDto;
import com.molkovor.customer.entity.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.web.reactive.result.view.CsrfRequestDataValueProcessor;
import org.springframework.security.web.server.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;

@Controller
@RequiredArgsConstructor
@RequestMapping("/customer/products/{productId:\\d+}")
@Slf4j
public class ProductController {

    private final ProductsClient productsClient;
    private final FavouriteProductsClient favouriteProductsClient;
    private final ProductCommentClient productCommentClient;

    @ModelAttribute(value = "product", binding = false)
    public Mono<Product> getProduct(@PathVariable("productId") int productId) {

        return productsClient.findProduct(productId)
                .switchIfEmpty(Mono.defer(() ->
                        Mono.error(new NoSuchElementException("customer.product.error.not_found"))
                ));
    }

    @GetMapping
    public Mono<String> getProductPage(Product product,
                                       Model model) {
        model.addAttribute("isFavourite", false);

        return productCommentClient.findProductCommentsByProductId(product.id())
                .collectList()
                .doOnNext(comments -> model.addAttribute("comments", comments))
                .then(favouriteProductsClient.findFavouriteProductByProductId(product.id())
                        .doOnNext(fProduct -> model.addAttribute("isFavourite", true)))
                .thenReturn("customer/products/product_page");
    }

    @PostMapping("add-to-favourites")
    public Mono<String> addToFavourites(Product product) {

        return favouriteProductsClient.addProductToFavourites(product.id())
                .thenReturn("redirect:/customer/products/%d".formatted(product.id()))
                .onErrorResume(exception -> {
                    log.error(exception.getMessage(), exception);
                    return Mono.just("redirect:/customer/products/%d".formatted(product.id()));
                });
    }

    @PostMapping("delete-from-favourites")
    public Mono<String> deleteFromFavourites(Product product) {

        return favouriteProductsClient.removeProductFromFavourites(product.id())
                .thenReturn("redirect:/customer/products/%d".formatted(product.id()));
    }

    @PostMapping("create-comment")
    public Mono<String> createComment(Product product,
                                      NewProductCommentDto newProductCommentDto,
                                      Model model) {

        return productCommentClient.createProductComment(
                        product.id(),
                        newProductCommentDto.rating(),
                        newProductCommentDto.comment())
                .thenReturn("redirect:/customer/products/%d".formatted(product.id()))
                .onErrorResume(ClientBadRequestException.class, ex -> {
                    model.addAttribute("isFavourite", false);
                    model.addAttribute("newProductCommentDto", newProductCommentDto);
                    model.addAttribute("errors", ex.getErrors());

                    return favouriteProductsClient.findFavouriteProductByProductId(product.id())
                            .doOnNext(fProduct -> model.addAttribute("isFavourite", true))
                            .thenReturn("customer/products/product_page");
                });
    }

    @ExceptionHandler(NoSuchElementException.class)
    public String handleNoSuchElementException(NoSuchElementException ex, Model model,
                                               ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.NOT_FOUND);
        model.addAttribute("error", ex.getMessage());
        return "errors/404";
    }

    @ModelAttribute
    public Mono<CsrfToken> loadCsrfToken(ServerWebExchange exchange) {

        return exchange.<Mono<CsrfToken>>getAttribute(CsrfToken.class.getName())
                .doOnSuccess(token -> exchange.getAttributes()
                        .put(CsrfRequestDataValueProcessor.DEFAULT_CSRF_ATTR_NAME, token));
    }
}
