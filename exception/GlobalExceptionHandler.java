package com.azs.exception;

import com.azs.constants.ErrorCodeAndErrorMsgEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiErrorResponse> handleCustomException(CustomException ex) {
        log.error("CustomException occurred: Code: {}, Message: {}", ex.getErrorCode().getCode(), ex.getErrorCode().getMessage());
        ApiErrorResponse response = new ApiErrorResponse(
            ex.getHttpStatusCode(),
            ex.getErrorCode().getCode(),
            ex.getErrorCode().getMessage()
        );
        return ResponseEntity.status(ex.getErrorCode().getHttpStatus()).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, ApiErrorResponse>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, ApiErrorResponse> errorResponses = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            ApiErrorResponse errorResponse = mapFieldToErrorResponse(fieldName, error.getDefaultMessage());
            errorResponses.put(fieldName, errorResponse);
        });
        log.error("Validation errors: {}", errorResponses);
        return ResponseEntity.badRequest().body(errorResponses);
    }

    private ApiErrorResponse mapFieldToErrorResponse(String fieldName, String defaultMessage) {
        switch (fieldName) {
            case "invoiceNo":
                return new ApiErrorResponse(
                    ErrorCodeAndErrorMsgEnum.EMPTY_INVOICE_NO.getHttpStatus().value(),
                    ErrorCodeAndErrorMsgEnum.EMPTY_INVOICE_NO.getCode(),
                    ErrorCodeAndErrorMsgEnum.EMPTY_INVOICE_NO.getMessage()
                );
            case "productName":
                return new ApiErrorResponse(
                    ErrorCodeAndErrorMsgEnum.EMPTY_PRODUCT_NAME.getHttpStatus().value(),
                    ErrorCodeAndErrorMsgEnum.EMPTY_PRODUCT_NAME.getCode(),
                    ErrorCodeAndErrorMsgEnum.EMPTY_PRODUCT_NAME.getMessage()
                );
            case "currentStock":
                return new ApiErrorResponse(
                    ErrorCodeAndErrorMsgEnum.NULL_CURRENT_STOCK.getHttpStatus().value(),
                    ErrorCodeAndErrorMsgEnum.NULL_CURRENT_STOCK.getCode(),
                    ErrorCodeAndErrorMsgEnum.NULL_CURRENT_STOCK.getMessage()
                );
            case "productPurchasedFromLocation":
                return new ApiErrorResponse(
                    ErrorCodeAndErrorMsgEnum.EMPTY_PURCHASE_LOCATION.getHttpStatus().value(),
                    ErrorCodeAndErrorMsgEnum.EMPTY_PURCHASE_LOCATION.getCode(),
                    ErrorCodeAndErrorMsgEnum.EMPTY_PURCHASE_LOCATION.getMessage()
                );
            case "purchaseDate":
                return new ApiErrorResponse(
                    ErrorCodeAndErrorMsgEnum.NULL_PURCHASE_DATE.getHttpStatus().value(),
                    ErrorCodeAndErrorMsgEnum.NULL_PURCHASE_DATE.getCode(),
                    ErrorCodeAndErrorMsgEnum.NULL_PURCHASE_DATE.getMessage()
                );
            case "productQtyOrdered":
                return new ApiErrorResponse(
                    ErrorCodeAndErrorMsgEnum.NULL_ORDERED_QUANTITY.getHttpStatus().value(),
                    ErrorCodeAndErrorMsgEnum.NULL_ORDERED_QUANTITY.getCode(),
                    ErrorCodeAndErrorMsgEnum.NULL_ORDERED_QUANTITY.getMessage()
                );
            case "productPrice":
                return new ApiErrorResponse(
                    ErrorCodeAndErrorMsgEnum.NULL_PRODUCT_PRICE.getHttpStatus().value(),
                    ErrorCodeAndErrorMsgEnum.NULL_PRODUCT_PRICE.getCode(),
                    ErrorCodeAndErrorMsgEnum.NULL_PRODUCT_PRICE.getMessage()
                );
            default:
                return new ApiErrorResponse(
                    ErrorCodeAndErrorMsgEnum.INVALID_INPUT.getHttpStatus().value(),
                    ErrorCodeAndErrorMsgEnum.INVALID_INPUT.getCode(),
                    defaultMessage
                );
        }
    }
    
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneralException(Exception ex) {
        log.error("Unexpected exception occurred: ", ex);
        ApiErrorResponse response = new ApiErrorResponse(
            ErrorCodeAndErrorMsgEnum.GENERAL_ERROR.getHttpStatus().value(),
            ErrorCodeAndErrorMsgEnum.GENERAL_ERROR.getCode(),
            ErrorCodeAndErrorMsgEnum.GENERAL_ERROR.getMessage()
        );
        return ResponseEntity
            .status(ErrorCodeAndErrorMsgEnum.GENERAL_ERROR.getHttpStatus())
            .body(response);
    }
}
