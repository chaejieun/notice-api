package com.rsupport.notice.dto.notice;

import com.rsupport.notice.constant.annotation.enums.SearchType;
import com.rsupport.notice.dto.common.PagingRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoticeListSearchDto extends PagingRequest {

    private String keyword;
    private SearchType searchType;
    private String regStartDate;
    private String regEndDate;
}
