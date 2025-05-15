package com.rsupport.notice.dto.common;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PagingRequest {

    private int pageNumber = 1;
    private int pageSize = 10;
}
