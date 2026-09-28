package com.ecommerce.productservice.api;

import java.time.Instant;
import java.util.Map;

public record ErrorResponse(Instant timestamp, int status, String error, String message, String path,
                            Map<String, String> details) {
}
