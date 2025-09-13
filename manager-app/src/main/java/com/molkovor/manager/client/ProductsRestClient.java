package com.molkovor.manager.client;

import com.molkovor.manager.entity.Product;

import java.util.List;
import java.util.Optional;

public interface ProductsRestClient {

    List<Product> getAllProducts(String filter);

    Product createProduct(String title, String details);

    Optional<Product> getProductById(int productId);

    void updateProduct(int productId, String title, String details);

    void deleteProduct(int productId);

}
