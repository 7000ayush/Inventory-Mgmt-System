package com.azs.constants;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCodeAndErrorMsgEnum {

    PRODUCT_NOT_FOUND("ERR001", "Product not found", HttpStatus.NOT_FOUND),
    INVALID_INPUT("ERR002", "Invalid input provided", HttpStatus.BAD_REQUEST),
    DATABASE_ERROR("ERR003", "Database operation failed", HttpStatus.INTERNAL_SERVER_ERROR),
    DATE_PARSE_ERROR("ERR004", "Failed to parse the provided date", HttpStatus.BAD_REQUEST),
    GENERAL_ERROR("ERR005", "An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR),
    INSUFFICIENT_STOCK("ERR006", "Insufficient stock", HttpStatus.BAD_REQUEST),
    MAPPING_ERROR("ERR007", "Error mapping entity to DTO", HttpStatus.INTERNAL_SERVER_ERROR),

    // Field-specific validation errors
    EMPTY_INVOICE_NO("ERR008", "Invoice number cannot be null or empty", HttpStatus.BAD_REQUEST),
    EMPTY_PRODUCT_NAME("ERR009", "Product name cannot be null or empty", HttpStatus.BAD_REQUEST),
    NULL_CURRENT_STOCK("ERR010", "Current stock cannot be null", HttpStatus.BAD_REQUEST),
    EMPTY_PURCHASE_LOCATION("ERR011", "Product purchase location cannot be null or empty", HttpStatus.BAD_REQUEST),
    NULL_PURCHASE_DATE("ERR012", "Purchase date cannot be null", HttpStatus.BAD_REQUEST),
    NULL_ORDERED_QUANTITY("ERR013", "Ordered quantity cannot be null", HttpStatus.BAD_REQUEST),
    NULL_PRODUCT_PRICE("ERR014", "Product price cannot be null", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCodeAndErrorMsgEnum(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
