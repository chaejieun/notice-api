package com.rsupport.notice.config.exception;

import com.rsupport.notice.constant.annotation.enums.ApiStatus;
import com.rsupport.notice.constant.annotation.enums.ErrorCode;
import com.rsupport.notice.dto.common.ApiResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({NotFoundException.class, FileUploadException.class, })
    public ResponseEntity<Object> notFoundException(NotFoundException e) {
        ApiResult apiResult = new ApiResult();

        apiResult.setCode(ApiStatus.BAD_REQUEST.getCode());
        apiResult.setStatus(ApiStatus.BAD_REQUEST);
        apiResult.setErrorCode(e.getErrorCode());
        apiResult.setErrorMessage(e.getErrorMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResult);
    }

    @ExceptionHandler({ ForbiddenException.class })
    public ResponseEntity<Object> forbiddenException() {
        ApiResult result = new ApiResult();

        result.setCode(ApiStatus.FORBIDDEN.getCode());
        result.setStatus(ApiStatus.FORBIDDEN);
        result.setErrorCode(ErrorCode.AUTHENTICATION_FAILED.getCode());
        result.setErrorMessage(ErrorCode.AUTHENTICATION_FAILED.getMessage());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(result);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> internalServerErrorException(Exception e) {
        ApiResult result = new ApiResult();

        result.setCode(ApiStatus.INTERNAL_SERVER_ERROR.getCode());
        result.setStatus(ApiStatus.INTERNAL_SERVER_ERROR);
        result.setErrorCode(ErrorCode.EXCEPTION.getCode());
        result.setErrorMessage(e.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
    }
}
