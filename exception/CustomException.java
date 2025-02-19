package com.azs.exception;

import com.azs.constants.ErrorCodeAndErrorMsgEnum;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private final ErrorCodeAndErrorMsgEnum errorCode;

    public CustomException(ErrorCodeAndErrorMsgEnum errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public CustomException(ErrorCodeAndErrorMsgEnum errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }

    public int getHttpStatusCode() {
        return this.errorCode.getHttpStatus().value();
    }
}
