package com.rsupport.notice.dto.notice;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class NoticeListDto {

    private Long noticeId;
    private String title;
    private boolean fileStatus;
    private LocalDateTime regDate;
    private Integer viewCount;
    private String writer;

    @QueryProjection
    public NoticeListDto(Long noticeId, String title, boolean fileStatus, LocalDateTime regDate, Integer viewCount, String writer) {
        this.noticeId = noticeId;
        this.title = title;
        this.fileStatus = fileStatus;
        this.regDate = regDate;
        this.viewCount = viewCount;
        this.writer = writer;
    }
}
