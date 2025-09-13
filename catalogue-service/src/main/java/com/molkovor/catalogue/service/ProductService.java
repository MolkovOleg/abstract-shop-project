package com.molkovor.catalogue.service;

import com.molkovor.catalogue.entity.Product;

import java.util.Optional;

public interface ProductService {

    Iterable<Product> getAllProducts(String filter);

    Product createProduct(String title, String details);

    Optional<Product> getProductById(Integer productId);

    void updateProduct(Integer id, String title, String details);

    void deleteProduct(Integer id);
}
