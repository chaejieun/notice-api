package com.rsupport.notice.constant.annotation.enums;

import lombok.Getter;

@Getter
public enum ErrorCode {

    NOTICE_NOT_FOUND(1001, "NOTICE_NOT_FOUND"),
    ATTACHMENT_DIR_FAILED(2001, "ATTACHMENT_DIR_FAILED"),
    ATTACHMENT_UPLOAD_FAILED(2002, "ATTACHMENT_UPLOAD_FAILED"),
    AUTHENTICATION_FAILED(4001, "AUTHENTICATION_FAILED"),
    EXCEPTION(9999, "UNDEFINED_EXCEPTION");

    final int code;
    final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
