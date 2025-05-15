package com.rsupport.notice;

import com.rsupport.notice.config.exception.NotFoundException;
import com.rsupport.notice.constant.annotation.enums.ErrorCode;
import com.rsupport.notice.constant.annotation.enums.SearchType;
import com.rsupport.notice.dto.notice.*;
import com.rsupport.notice.entity.Notice;
import com.rsupport.notice.mapper.NoticeMapper;
import com.rsupport.notice.repository.AttachmentRepository;
import com.rsupport.notice.repository.NoticeRepository;
import com.rsupport.notice.repository.qdsl.QNoticeRepository;
import com.rsupport.notice.service.AttachmentService;
import com.rsupport.notice.service.NoticeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NoticeServiceTest {

    @Mock
    private NoticeRepository noticeRepository;

    @Mock
    private QNoticeRepository qNoticeRepository;

    @Mock
    private AttachmentRepository attachmentRepository;

    @InjectMocks
    private NoticeService noticeService;

    @Mock
    private AttachmentService attachmentService;

    @Mock
    private NoticeMapper noticeMapper;

    private Notice noticeEntity;
    private NoticeRegDto noticeRegDto;
    private List<MultipartFile> attachmentList;

    @BeforeEach
    void setUp() {
        // given
        noticeRegDto = NoticeTestFactory.createNoticeRegDto();
        noticeEntity = NoticeTestFactory.createNotice();
        attachmentList = NoticeTestFactory.createFileList();
    }

    @Test
    @DisplayName("공지사항 등록 - 첨부파일 미포함")
    void registerNoticeWithoutAttachmentList() {
        // when
        when(noticeMapper.toEntity(noticeRegDto)).thenReturn(noticeEntity);
        when(noticeRepository.save(noticeEntity)).thenReturn(noticeEntity);

        Notice result = noticeService.registerNotice(noticeRegDto, null);

        // then
        assertNotNull(result);
        verify(noticeMapper).toEntity(noticeRegDto);
        verify(noticeRepository).save(noticeEntity);
        verify(attachmentService, never()).registerFileList(any(), any());
    }

    @Test
    @DisplayName("공지사항 등록 - 첨부파일 포함")
    void registerNoticeWithAttachmentList() {
        // when
        when(noticeMapper.toEntity(noticeRegDto)).thenReturn(noticeEntity);
        when(noticeRepository.save(noticeEntity)).thenReturn(noticeEntity);
        doNothing().when(attachmentService).registerFileList(eq(attachmentList), eq(noticeEntity));

        Notice result = noticeService.registerNotice(noticeRegDto, attachmentList);

        // then
        assertNotNull(result);
        assertEquals("첫 번째 공지사항 제목", result.getTitle());

        verify(noticeMapper, times(1)).toEntity(noticeRegDto);
        verify(noticeRepository, times(1)).save(noticeEntity);
        verify(attachmentService, times(1)).registerFileList(eq(attachmentList), eq(noticeEntity));
    }

    @Test
    @DisplayName("공지사항 목록 조회 - 검색 조건")
    void findNoticeListBySearchCondition() {
        // given
        String keyword = "첫 번째 제목";

        List<NoticeListDto> noticeListDtoList = NoticeTestFactory.createNoticeListDto();
        NoticeListSearchDto noticeListSearchDto = new NoticeListSearchDto();
        noticeListSearchDto.setSearchType(SearchType.TITLE);
        noticeListSearchDto.setKeyword(keyword);
        Pageable pageable = PageRequest.of(0, 10);

        // when
        when(qNoticeRepository.findByListDto(eq(noticeListSearchDto), eq(pageable)))
                .thenReturn(noticeListDtoList);
        when(qNoticeRepository.findListCountByDto(eq(noticeListSearchDto)))
                .thenReturn((long) noticeListDtoList.size());

        Page<NoticeListDto> result = noticeService.searchList(noticeListSearchDto, pageable);

        // then
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals("첫 번째 공지사항", result.getContent().get(0).getTitle());

        // then
        verify(qNoticeRepository, times(1)).findByListDto(eq(noticeListSearchDto), eq(pageable));
        verify(qNoticeRepository, times(1)).findListCountByDto(eq(noticeListSearchDto));
    }

    @Test
    @DisplayName("공지사항 상세 조회 - 존재하지 않는 공지사항 조회 시 예외 발생")
    void findNoticeByInvalidNoticeId() {
        // given
        Long nonExistingId = 999L;
        when(noticeRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        // when
        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            noticeService.search(nonExistingId);
        });

        // then
        assertNotNull(exception);
        assertEquals(1001, exception.getErrorCode());
        assertEquals(ErrorCode.NOTICE_NOT_FOUND.getMessage(), exception.getErrorMessage());
    }

    @Test
    @DisplayName("공지사항 상세 조회 - 조회수 증가 (비관적락)")
    void findNoticeByNoticeIdWithLock() {
        // given
        noticeEntity.setViewCount(10);
        when(noticeRepository.findWithLockByNoticeId(noticeEntity.getNoticeId()))
                .thenReturn(Optional.of(noticeEntity));

        when(noticeMapper.toNoticeDto(any(Notice.class))).thenAnswer(invocation -> {
            Notice notice = invocation.getArgument(0);
            NoticeDto dto = new NoticeDto();
            dto.setNoticeId(notice.getNoticeId());
            dto.setTitle(notice.getTitle());
            dto.setContent(notice.getContent());
            dto.setWriter(notice.getWriter());
            dto.setViewCount(notice.getViewCount());
            return dto;
        });

        // when
        NoticeDto result = noticeService.getNoticeWithViewCountIncrease(noticeEntity.getNoticeId());

        // then
        assertThat(noticeEntity.getViewCount()).isEqualTo(11);
        assertThat(result.getViewCount()).isEqualTo(11);
        verify(noticeRepository).findWithLockByNoticeId(noticeEntity.getNoticeId());
        verify(noticeMapper).toNoticeDto(any(Notice.class));
    }

    @Test
    @DisplayName("공지사항 수정")
    void modifyNoticeWithAttachmentList() {
        // given
        NoticeModDto noticeModDto = NoticeTestFactory.createNoticeModDto();
        List<MultipartFile> attachmentModList = Arrays.asList(new MockMultipartFile("file1", "test.txt", "text/plain", "content".getBytes()));

        // when
        when(noticeRepository.findById(noticeEntity.getNoticeId())).thenReturn(Optional.of(noticeEntity));
        doNothing().when(attachmentRepository).deleteByNoticeNoticeId(noticeEntity.getNoticeId());
        doNothing().when(attachmentService).registerFileList(attachmentModList, noticeEntity);
        when(noticeRepository.save(any(Notice.class))).thenReturn(noticeEntity);

        noticeService.modifyNotice(noticeEntity.getNoticeId(), noticeModDto, attachmentModList);

        // then
        verify(noticeRepository, times(1)).save(any(Notice.class));
        verify(attachmentRepository, times(1)).deleteByNoticeNoticeId(noticeEntity.getNoticeId());
        verify(attachmentService, times(1)).registerFileList(attachmentModList, noticeEntity);
    }

    @Test
    @DisplayName("공지사항 삭제")
    void deleteNoticeWithAttachmentList() {
        // when
        when(noticeRepository.findById(noticeEntity.getNoticeId())).thenReturn(Optional.of(noticeEntity));
        doNothing().when(attachmentRepository).deleteByNoticeNoticeId(noticeEntity.getNoticeId());
        doNothing().when(noticeRepository).delete(noticeEntity);

        noticeService.deleteNotice(noticeEntity.getNoticeId());

        // then
        verify(noticeRepository, times(1)).delete(noticeEntity);
        verify(attachmentRepository, times(1)).deleteByNoticeNoticeId(noticeEntity.getNoticeId());
    }
}