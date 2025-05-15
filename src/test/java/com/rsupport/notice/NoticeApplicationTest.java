package com.rsupport.notice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rsupport.notice.constant.annotation.enums.SearchType;
import com.rsupport.notice.dto.notice.*;

import com.rsupport.notice.entity.Notice;
import com.rsupport.notice.repository.NoticeRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class NoticeApplicationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private NoticeRepository noticeRepository;

    private List<Notice> noticeList;

    @BeforeEach
    void setUp() {
        noticeRepository.deleteAll();
        // given
        noticeList = NoticeTestFactory.createNoticeList();
        noticeRepository.saveAll(noticeList);
        noticeRepository.flush();
    }

    @AfterEach
    void cleanUp() {
        noticeRepository.deleteAll();
    }

    @Test
    @DisplayName("공지사항 등록 - 유효한 입력")
    void registerNoticeWithValidInput() throws Exception {
        // given
        NoticeRegDto noticeRegDto = NoticeTestFactory.createNoticeRegDto();
        String noticeRegDtoJson = objectMapper.writeValueAsString(noticeRegDto);

        MockMultipartFile noticeRegDtoPart = new MockMultipartFile(
                "noticeRegDto",
                "",
                "application/json",
                noticeRegDtoJson.getBytes());

        // when & then
        mockMvc.perform(multipart("/api/notices")
                        .file(noticeRegDtoPart)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value(noticeRegDto.getTitle()))
                .andExpect(jsonPath("$.data.content").value(noticeRegDto.getContent()))
                .andExpect(jsonPath("$.data.writer").value(noticeRegDto.getWriter()))
                .andExpect(jsonPath("$.data.noticeId").exists());
    }

    @Test
    @DisplayName("공지사항 등록 - 첨부파일 포함")
    void registerNoticeWithAttachmentList() throws Exception {
        // given
        NoticeRegDto noticeRegDto = NoticeTestFactory.createNoticeRegDto();
        String noticeRegDtoJson = objectMapper.writeValueAsString(noticeRegDto);

        MockMultipartFile noticeRegDtoPart = new MockMultipartFile(
                "noticeRegDto",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                noticeRegDtoJson.getBytes()
        );

        List<MockMultipartFile> attachmentList = NoticeTestFactory.createMockFileList();
        MockMultipartHttpServletRequestBuilder builder = multipart("/api/notices")
                                                      .file(noticeRegDtoPart);

        for (MockMultipartFile file : attachmentList) {
            builder.file(file);
        }

        mockMvc.perform(builder.contentType(MediaType.MULTIPART_FORM_DATA))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.noticeId").exists())
                .andExpect(jsonPath("$.data.title").value(noticeRegDto.getTitle()))
                .andExpect(jsonPath("$.data.content").value(noticeRegDto.getContent()))
                .andExpect(jsonPath("$.data.attachmentList").exists());
    }

    @Test
    @DisplayName("공지사항 목록 조회 - 검색 조건")
    void findNoticeListBySearchCondition() throws Exception {
        // given
        String keyword = "내용";

        NoticeListSearchDto noticeListSearchDto = new NoticeListSearchDto();
        noticeListSearchDto.setSearchType(SearchType.TITLE_OR_CONTENT);
        noticeListSearchDto.setKeyword(keyword);
        noticeListSearchDto.setPageSize(0);
        noticeListSearchDto.setPageNumber(10);

        // when & then
        mockMvc.perform(get("/api/notices",noticeListSearchDto))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.page.totalElements").isNumber());
    }

    @Test
    @DisplayName("공지사항 상세 조회 - 조회수 상승")
    void findNoticeByNoticeId() throws Exception {
        // given
        Notice notice = noticeList.get(0);

        // when & then
        mockMvc.perform(get("/api/notices/{noticeId}", notice.getNoticeId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.noticeId").value(notice.getNoticeId()))
                .andExpect(jsonPath("$.data.viewCount").value(1));
    }

    @Test
    @DisplayName("공지사항 상세 조회(관리자)")
    void findNoticeByIdWithAdminRole() throws Exception {
        // given
        Notice notice = noticeList.get(3);

        // when & then
        mockMvc.perform(get("/api/notices/admin/{noticeId}", notice.getNoticeId())
                        .header("X-USER-ROLE", "ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.noticeId").value(notice.getNoticeId()));
    }

    @Test
    @DisplayName("공지사항 수정")
    void modifyNoticeWithModData() throws Exception {
        // given
        NoticeModDto noticeModDto = NoticeTestFactory.createNoticeModDto();
        String noticeModDtoJson = objectMapper.writeValueAsString(noticeModDto);
        Notice notice = noticeList.get(0);

        MockMultipartFile noticeModDtoFile = new MockMultipartFile(
                "noticeModDto",
                "",
                "application/json",
                noticeModDtoJson.getBytes());

        MockHttpServletRequestBuilder builder = multipart("/api/notices/{noticeId}", notice.getNoticeId())
                .file(noticeModDtoFile);

        builder.with(request -> {
            request.setMethod("PUT");
            return request;
        });

        // when & then
        mockMvc.perform(builder)
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/notices/{noticeId}", notice.getNoticeId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value(noticeModDto.getTitle()))
                .andExpect(jsonPath("$.data.content").value(noticeModDto.getContent()));
    }

    @Test
    @DisplayName("공지사항 삭제")
    void deleteNoticeByNoticeId() throws Exception {
        // given
        Notice notice = noticeList.get(2);

        // when & then
        mockMvc.perform(delete("/api/notices/{noticeId}", notice.getNoticeId()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/notices/{noticeId}", notice.getNoticeId()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage").value("NOTICE_NOT_FOUND"));
    }
}
