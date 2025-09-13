package com.molkovor.manager.controller;

import com.molkovor.manager.client.BadRequestException;
import com.molkovor.manager.client.ProductsRestClient;
import com.molkovor.manager.dto.NewProductDto;
import com.molkovor.manager.entity.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ConcurrentModel;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Модульные тесты ProductsController")
class ProductsControllerTest {

    @Mock
    ProductsRestClient productsRestClient;

    @InjectMocks
    ProductsController controller;

    @Test
    @DisplayName("getProductCreatePage - создает новый товар и перенаправляет на страницу товара")
    void getProductCreatePage_RequestIsValid_ReturnsRedirectionToProductPage() {
        // given
        var newProductDto = new NewProductDto("Новый товар", "Описание");
        var model = new ConcurrentModel();

        doReturn(new Product(1, "Новый товар", "Описание"))
                .when(productsRestClient).createProduct("Новый товар", "Описание");

        // when
        var result = controller.getProductCreatePage(newProductDto, model);

        // then
        assertEquals("redirect:/catalogue/products/1", result);

        verify(productsRestClient).createProduct("Новый товар", "Описание");
        verifyNoMoreInteractions(productsRestClient);
    }

    @Test
    @DisplayName("getProductCreatePage - вернет страницу создания товара, но описанием ошибок")
    void getProductCreatePage_RequestIsInvalid_ReturnsCreateProductPage() {
        // given
        var newProductDto = new NewProductDto("  ", null);
        var model = new ConcurrentModel();

        doThrow(new BadRequestException(List.of("Ошибка 1", "Ошибка 2")))
                .when(productsRestClient).createProduct("  ", null);

        // when
        var result = controller.getProductCreatePage(newProductDto, model);

        // then
        assertEquals("catalogue/products/new_product", result);
        assertEquals(newProductDto, model.getAttribute("newProductDto"));
        assertEquals(List.of("Ошибка 1", "Ошибка 2"), model.getAttribute("errors"));

        verify(productsRestClient).createProduct("  ", null);
        verifyNoMoreInteractions(productsRestClient);
    }

}