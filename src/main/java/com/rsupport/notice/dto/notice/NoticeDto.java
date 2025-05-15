package com.rsupport.notice.dto.notice;

import com.rsupport.notice.entity.Attachment;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class NoticeDto {

    private Long noticeId;
    private String title;
    private String content;
    private LocalDateTime regDate;
    private int viewCount;
    private String writer;
    private List<Attachment> attachmentList;
}
