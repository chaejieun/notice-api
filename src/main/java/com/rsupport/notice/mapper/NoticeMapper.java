package com.rsupport.notice.mapper;

import com.rsupport.notice.dto.notice.NoticeDto;
import com.rsupport.notice.dto.notice.NoticeRegDto;
import com.rsupport.notice.entity.Notice;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NoticeMapper {

    Notice toEntity(NoticeRegDto noticeRegDto);

    NoticeDto toNoticeDto(Notice notice);
}
