package com.molkovor.manager.controller;

import com.molkovor.manager.client.BadRequestException;
import com.molkovor.manager.client.ProductsRestClient;
import com.molkovor.manager.dto.NewProductDto;
import com.molkovor.manager.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("catalogue/products")
public class ProductsController {

    private final ProductsRestClient productsRestClient;

    @GetMapping("list")
    public String getProductListPage(Model model, @RequestParam(name = "filter", required = false) String filter) {

        List<Product> products = productsRestClient.getAllProducts(filter);
        model.addAttribute("products", products);
        model.addAttribute("filter", filter);
        return "catalogue/products/list";
    }

    @GetMapping("create_page")
    public String getCreateProductPage() {
        return "catalogue/products/new_product";
    }

    @PostMapping("create")
    public String getProductCreatePage(NewProductDto newProductDto,
                                       Model model) {
        try {
            Product product = productsRestClient.createProduct(newProductDto.title(), newProductDto.details());
            return "redirect:/catalogue/products/%d".formatted(product.id());
        } catch (BadRequestException ex) {
            model.addAttribute("newProductDto", newProductDto);
            model.addAttribute("errors", ex.getErrors());
            return "catalogue/products/new_product";
        }
    }
}
