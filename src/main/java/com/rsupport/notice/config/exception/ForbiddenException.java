package com.rsupport.notice.config.exception;

import com.rsupport.notice.constant.annotation.enums.ErrorCode;
import lombok.Getter;

@Getter
public class ForbiddenException extends RuntimeException {

    final int errorCode;
    final String errorMessage;

    public ForbiddenException(ErrorCode errorCode) {
        this.errorCode = errorCode.getCode();
        this.errorMessage = errorCode.getMessage();
    }
}
