package com.molkovor.customer.controller;

import com.molkovor.customer.client.FavouriteProductsClient;
import com.molkovor.customer.client.ProductCommentClient;
import com.molkovor.customer.client.ProductsClient;
import com.molkovor.customer.entity.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.mock.http.server.reactive.MockServerHttpResponse;
import org.springframework.ui.ConcurrentModel;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Модульные тесты ProductController")
class ProductControllerTest {

    @Mock
    ProductsClient productsClient;
    @Mock
    FavouriteProductsClient favouriteProductsClient;
    @Mock
    ProductCommentClient productCommentClient;

    @InjectMocks
    ProductController controller;

    @Test
    @DisplayName("Исключение NoSuchElementException должно быть транслировано на странице errors/404")
    void handleNoSuchElementException_ReturnsErrors404() {
        // given
        NoSuchElementException exception = new NoSuchElementException("Товар не найден");
        ConcurrentModel model = new ConcurrentModel();
        ServerHttpResponse response = new MockServerHttpResponse();
        // when
        String result = controller.handleNoSuchElementException(exception, model, response);
        // then
        assertEquals("errors/404", result);
        assertEquals("Товар не найден", model.getAttribute("error"));
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getProduct_ProductExists_ReturnNotEmptyMono() {
        // given
        Product product = new Product(1, "Новый товар", "Описание");
        doReturn(Mono.just(product))
                .when(productsClient).findProduct(1);
        // when
        StepVerifier.create(controller.getProduct(1))
                // then
                .expectNext(product)
                .expectComplete()
                .verify();

        verify(productsClient).findProduct(1);
        verifyNoMoreInteractions(productsClient);
        verifyNoInteractions(favouriteProductsClient, productCommentClient);
    }

    @Test
    void getProduct_ProductDoesNotExist_ReturnMonoWithNoSuchElementException() {
        // given
        doReturn(Mono.empty()).when(productsClient).findProduct(1);
        // when
        StepVerifier.create(controller.getProduct(1))
                .expectErrorMatches(exception -> exception instanceof NoSuchElementException ex
                        && ex.getMessage().equals("customer.product.error.not_found"))
                .verify();

        verify(productsClient).findProduct(1);
        verifyNoMoreInteractions(productsClient);
        verifyNoInteractions(favouriteProductsClient, productCommentClient);
    }
}