package com.rsupport.notice.repository.qdsl;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.rsupport.notice.constant.annotation.enums.SearchType;
import com.rsupport.notice.dto.notice.NoticeListDto;
import com.rsupport.notice.dto.notice.NoticeListSearchDto;
import com.rsupport.notice.dto.notice.QNoticeListDto;
import com.rsupport.notice.entity.QAttachment;
import com.rsupport.notice.entity.QNotice;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class QNoticeRepository {

    private final JPAQueryFactory queryFactory;
    QNotice notice = QNotice.notice;
    QAttachment attachment = QAttachment.attachment;

    public List<NoticeListDto> findByListDto(NoticeListSearchDto noticeListSearchDto, Pageable pageable) {
        return queryFactory
                .select(
                    new QNoticeListDto(
                        notice.noticeId,
                        notice.title,
                        attachment.filePath.isNotNull(),
                        notice.regDate,
                        notice.viewCount,
                        notice.writer
                    )
                )
                .from(notice)
                .leftJoin(notice.attachmentList, attachment)
                .distinct()
                .where(
                        keywordEq(noticeListSearchDto.getKeyword(), noticeListSearchDto.getSearchType()),
                        regDateBetween(noticeListSearchDto.getRegStartDate(), noticeListSearchDto.getRegEndDate())
                )
                .orderBy(
                    notice.regDate.desc()
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    public Long findListCountByDto(NoticeListSearchDto noticeListSearchDto) {
        return queryFactory
                .select(notice.count())
                .from(notice)
                .leftJoin(notice.attachmentList, attachment)
                .distinct()
                .where(
                        keywordEq(noticeListSearchDto.getKeyword(), noticeListSearchDto.getSearchType()),
                        regDateBetween(noticeListSearchDto.getRegStartDate(), noticeListSearchDto.getRegEndDate())
                )
                .fetchOne();
    }

    private BooleanExpression keywordEq(String keyword, SearchType searchType) {
        if (keyword == null || keyword.isEmpty()) {
            return null;
        }

        if (SearchType.TITLE.equals(searchType)) {
            return notice.title.contains(keyword);
        } else if (SearchType.TITLE_OR_CONTENT.equals(searchType)) {
            return notice.title.contains(keyword).or(notice.content.contains(keyword));
        }
        return null;
    }

    private BooleanExpression regDateBetween(String regStartDate, String regEndDate) {
        LocalDateTime startDateTime = null;
        LocalDateTime endDateTime = null;

        if (StringUtils.isNotBlank(regStartDate)) {
            startDateTime = LocalDate.parse(regStartDate, DateTimeFormatter.ofPattern("yyyyMMdd")).atStartOfDay();
        }

        if (StringUtils.isNotBlank(regEndDate)) {
            endDateTime =  LocalDate.parse(regEndDate, DateTimeFormatter.ofPattern("yyyyMMdd")).atTime(23, 59, 59, 999999999);
        }

        if (startDateTime != null && endDateTime != null) {
            return notice.regDate.between(startDateTime, endDateTime);
        } else if (startDateTime != null) {
            return notice.regDate.goe(startDateTime);
        } else if (endDateTime != null) {
            return notice.regDate.loe(endDateTime);
        } else {
            return null;
        }
    }
}
