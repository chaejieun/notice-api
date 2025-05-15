package com.rsupport.notice.service;

import com.rsupport.notice.config.exception.NotFoundException;
import com.rsupport.notice.constant.annotation.enums.ErrorCode;
import com.rsupport.notice.dto.notice.*;
import com.rsupport.notice.entity.Notice;
import com.rsupport.notice.mapper.NoticeMapper;
import com.rsupport.notice.repository.AttachmentRepository;
import com.rsupport.notice.repository.NoticeRepository;
import com.rsupport.notice.repository.qdsl.QNoticeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final QNoticeRepository qNoticeRepository;
    private final NoticeMapper noticeMapper;
    private final AttachmentService attachmentService;
    private final AttachmentRepository attachmentRepository;

    @CacheEvict(value = "notice", allEntries = true)
    public Notice registerNotice(NoticeRegDto noticeRegDto, List<MultipartFile> attachmentList) {

        Notice notice = noticeMapper.toEntity(noticeRegDto);

        noticeRepository.save(notice);

        if (attachmentList != null && !attachmentList.isEmpty()) {
            attachmentService.registerFileList(attachmentList, notice);
        }

        return notice;
    }

    public Notice getNoticeById(Long noticeId) {
        return noticeRepository.findById(noticeId).orElseThrow(() -> new NotFoundException(ErrorCode.NOTICE_NOT_FOUND));
    }

    @Cacheable(cacheNames = "noticeDto", key = "#noticeId")
    public NoticeDto search(Long noticeId) {
        Notice notice = getNoticeById(noticeId);
        return noticeMapper.toNoticeDto(notice);
    }

    public Page<NoticeListDto> searchList(NoticeListSearchDto noticeListSearchDto, Pageable pageable) {
        List<NoticeListDto> noticeList = qNoticeRepository.findByListDto(noticeListSearchDto, pageable);

        Long noticeListCount = qNoticeRepository.findListCountByDto(noticeListSearchDto);

        return PageableExecutionUtils.getPage(noticeList, pageable, () -> noticeListCount);
    }

    @CacheEvict(value = "notice", allEntries = true)
    public void modifyNotice(Long noticeId, NoticeModDto noticeModDto, List<MultipartFile> attachmentList) {
        Notice notice = getNoticeById(noticeId);
        notice.updateNotice(noticeModDto);
        noticeRepository.save(notice);

        attachmentRepository.deleteByNoticeNoticeId(noticeId);

        if (attachmentList != null && !attachmentList.isEmpty()) {
            attachmentService.registerFileList(attachmentList, notice);
        }
    }

    @CacheEvict(value = "notice", key = "#noticeId")
    public void deleteNotice(Long noticeId) {
        Notice notice = getNoticeById(noticeId);

        attachmentRepository.deleteByNoticeNoticeId(noticeId);

        noticeRepository.delete(notice);
    }

    public NoticeDto getNoticeWithViewCountIncrease(Long noticeId) {
        Notice notice = noticeRepository.findWithLockByNoticeId(noticeId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOTICE_NOT_FOUND));

        notice.increaseViewCount();

        return noticeMapper.toNoticeDto(notice);
    }
}
