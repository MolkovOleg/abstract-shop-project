package com.molkovor.feadback.controller.dto;

import jakarta.validation.constraints.NotNull;

public record NewFavouriteProductDto(

        @NotNull(message = "{feedback.products.create.favourite.product.errors.product_id_is_null}")
        Integer productId) {
}
