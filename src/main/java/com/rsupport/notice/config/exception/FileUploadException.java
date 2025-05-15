package com.rsupport.notice.config.exception;

import com.rsupport.notice.constant.annotation.enums.ErrorCode;
import lombok.Getter;

@Getter
public class FileUploadException extends RuntimeException{

    final int errorCode;
    final String errorMessage;

    public FileUploadException(ErrorCode errorCode, String message) {
        this.errorCode = errorCode.getCode();
        this.errorMessage = message;
    }
}
