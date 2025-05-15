package com.rsupport.notice;

import com.rsupport.notice.dto.notice.NoticeDto;
import com.rsupport.notice.dto.notice.NoticeListDto;
import com.rsupport.notice.dto.notice.NoticeModDto;
import com.rsupport.notice.dto.notice.NoticeRegDto;
import com.rsupport.notice.entity.Attachment;
import com.rsupport.notice.entity.Notice;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class NoticeTestFactory {

    public static NoticeRegDto createNoticeRegDto() {
        NoticeRegDto dto = new NoticeRegDto();
        dto.setTitle("첫 번째 공지사항 제목");
        dto.setContent("첫 번째 공지사항 내용");
        dto.setStartDate("20250501");
        dto.setEndDate("20250531");
        dto.setWriter("홍길동");
        return dto;
    }

    public static Notice createNotice() {
        NoticeRegDto dto = createNoticeRegDto();
        Notice notice = new Notice();
        notice.setNoticeId(1L);
        notice.setTitle(dto.getTitle());
        notice.setContent(dto.getContent());
        notice.setStartDate(dto.getStartDate());
        notice.setEndDate(dto.getEndDate());
        notice.setWriter(dto.getWriter());
        return notice;
    }

    public static NoticeModDto createNoticeModDto() {
        NoticeModDto noticeModDto = new NoticeModDto();
        noticeModDto.setTitle("수정된 제목");
        noticeModDto.setContent("수정된 내용");
        noticeModDto.setWriter("수정자명");
        return noticeModDto;
    }

    public static List<MultipartFile> createFileList() {
        return List.of(
            new MockMultipartFile("attachmentList", "file1.txt", "text/plain", "파일1 내용".getBytes()),
            new MockMultipartFile("attachmentList", "file2.pdf", "application/pdf", "파일2 내용".getBytes())
        );
    }

    public static List<MockMultipartFile> createMockFileList() {
        return List.of(
            new MockMultipartFile("attachmentList", "file1.txt", "text/plain", "파일1 내용".getBytes()),
            new MockMultipartFile("attachmentList", "file2.pdf", "application/pdf", "파일2 내용".getBytes())
        );
    }

    public static List<Attachment> createMockAttachmentList(Notice notice) {
        return IntStream.range(1, 3)
                .mapToObj(i -> Attachment.builder()
                        .originalName("original" + i + ".txt")
                        .fileName("file" + i + ".txt")
                        .filePath("/files/file" + i + ".txt")
                        .fileSize("512")
                        .notice(notice)
                        .build()
                ).collect(Collectors.toList());
    }

    public static List<Notice> createNoticeList() {
        List<Notice> noticeList = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Notice notice = new Notice();
            notice.setTitle("공지사항 제목 " + i);
            notice.setContent("공지사항 내용 " + i);
            notice.setStartDate("2025050" + i);
            notice.setEndDate("2025053" + i);
            notice.setWriter("작성자 " + i);
            noticeList.add(notice);
        }

        return noticeList;
    }

    public static List<NoticeListDto> createNoticeListDto() {
        return List.of(
                new NoticeListDto(1L, "첫 번째 공지사항", true, LocalDateTime.of(2025, 5, 10, 10, 0), 10, "홍길동"),
                new NoticeListDto(2L, "두 번째 공지사항", false, LocalDateTime.of(2025, 5, 9, 9, 30), 5, "이순신")
        );
    }
}
