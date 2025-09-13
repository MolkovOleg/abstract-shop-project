package com.molkovor.feadback.controller.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record NewProductCommentDto(

        @NotNull(message = "{feedback.products.create.comment.errors.product_id_is_null}")
        Integer productId,

        @NotNull(message = "{feedback.products.create.comment.errors.rating_is_null}")
        @Min(value = 1, message = "{feedback.products.create.comment.errors.rating_is_below_min}")
        @Max(value = 5, message = "{feedback.products.create.comment.errors.rating_is_above_max}")
        Integer rating,

        @Size(max = 1000, message = "{feedback.products.create.comment.errors.comment_is_too_long}")
        String comment) {
}
