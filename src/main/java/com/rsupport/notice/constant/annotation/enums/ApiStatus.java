package com.rsupport.notice.constant.annotation.enums;

import lombok.Getter;

@Getter
public enum ApiStatus {

    OK(200),
    BAD_REQUEST(400),
    FORBIDDEN(403),
    NOT_FOUND(404),
    METHOD_NOT_ALLOWED(405),
    INTERNAL_SERVER_ERROR(500);

    final int code;

    ApiStatus(int code) {
        this.code = code;
    }

}
