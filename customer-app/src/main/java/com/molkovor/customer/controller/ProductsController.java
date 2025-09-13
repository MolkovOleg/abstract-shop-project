package com.molkovor.customer.controller;

import com.molkovor.customer.client.FavouriteProductsClient;
import com.molkovor.customer.client.ProductsClient;
import com.molkovor.customer.entity.FavouriteProduct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
@RequestMapping("/customer/products")
public class ProductsController {

    private final ProductsClient productsClient;
    private final FavouriteProductsClient favouriteProductsClient;

    @GetMapping("list")
    public Mono<String> getProductsListPage(
            @RequestParam(name = "filter", required = false) String filter, Model model) {
        model.addAttribute("filter", filter);

        return productsClient.findAllProducts(filter)
                .collectList()
                .doOnNext(products -> model.addAttribute("products", products))
                .thenReturn("customer/products/list");
    }

    @GetMapping("favourites")
    public Mono<String> getFavoriteProductsListPage(
            @RequestParam(name = "filter", required = false) String filter, Model model) {
        model.addAttribute("filter", filter);

        return favouriteProductsClient.findFavouriteProducts()
                .map(FavouriteProduct::productId)
                .collectList()
                .flatMap(fProductsIdList -> productsClient.findAllProducts(filter)
                        .filter(product -> fProductsIdList.contains(product.id()))
                        .collectList())
                .doOnNext(products -> model.addAttribute("products", products))
                .thenReturn("customer/products/favourites");
    }
}
