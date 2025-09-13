package com.molkovor.catalogue.controller;

import com.molkovor.catalogue.controller.dto.NewProductDto;
import com.molkovor.catalogue.entity.Product;
import com.molkovor.catalogue.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.MapBindingResult;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductsRestControllerTest {

    @Mock
    ProductService productService;

    @InjectMocks
    ProductsRestController controller;

    @Test
    void getAllProducts_ProductsExits_ReturnsProducts() {
        // given
        String filter = "Шок";

        doReturn(List.of(
                new Product(1, "Шоколад", "Темный шоколад")
        )).when(productService).getAllProducts("Шок");

        // when
        var result = controller.getAllProducts(filter);

        // then
        assertEquals(List.of(
                new Product(1, "Шоколад", "Темный шоколад")), result);
    }

    @Test
    void createProduct_RequestIsValid_ReturnsProduct() throws BindException {
        // given
        var dto = new NewProductDto("Название", "Описание");
        var bindingResult = new MapBindingResult(Map.of(), "dto");
        var uriBuilder = UriComponentsBuilder.fromUriString("http://localhost");

        doReturn(new Product(1, "Название", "Описание"))
                .when(productService).createProduct("Название", "Описание");

        // when
        var result = controller.createProduct(dto, bindingResult, uriBuilder);

        // then
        assertNotNull(result);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(URI.create("http://localhost/catalogue-api/products/1"), result.getHeaders().getLocation());
        assertEquals(new Product(1, "Название", "Описание"), result.getBody());

        verify(productService).createProduct("Название", "Описание");
        verifyNoMoreInteractions(productService);
    }
}