package com.molkovor.customer.entity;

import java.util.UUID;

public record ProductComment(UUID id, int productId, int rating, String comment) {
}
