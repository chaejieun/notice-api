package com.rsupport.notice.controller;

import com.rsupport.notice.config.exception.ForbiddenException;
import com.rsupport.notice.dto.common.ApiResult;
import com.rsupport.notice.dto.notice.*;
import com.rsupport.notice.entity.Notice;
import com.rsupport.notice.service.NoticeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import static com.rsupport.notice.constant.annotation.enums.ErrorCode.AUTHENTICATION_FAILED;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notices")
public class NoticeController {

    private final NoticeService noticeService;

    @PostMapping
    public ResponseEntity<ApiResult> registerNotice(@Valid @RequestPart(value = "noticeRegDto") NoticeRegDto noticeRegDto, @RequestPart(value = "attachmentList", required = false) List<MultipartFile> attachmentList) {
        ApiResult apiResult = new ApiResult();

        Notice notice = noticeService.registerNotice(noticeRegDto, attachmentList);
        apiResult.setData(notice);

        return ResponseEntity.ok(apiResult);
    }

    @GetMapping
    public ResponseEntity<ApiResult> searchNoticeList(NoticeListSearchDto noticeListSearchDto) {
        ApiResult apiResult = new ApiResult();

        PageRequest pageRequest = PageRequest.of(noticeListSearchDto.getPageNumber() - 1, noticeListSearchDto.getPageSize());

        Page<NoticeListDto> noticeList = noticeService.searchList(noticeListSearchDto, pageRequest);

        apiResult.setData(noticeList);

        return ResponseEntity.ok(apiResult);
    }

    @GetMapping("/{noticeId}")
    public ResponseEntity<ApiResult> searchNotice(@PathVariable("noticeId") Long noticeId) {
        ApiResult apiResult = new ApiResult();

        NoticeDto noticeDto = noticeService.getNoticeWithViewCountIncrease(noticeId);

        apiResult.setData(noticeDto);

        return ResponseEntity.ok(apiResult);
    }

    @GetMapping("/admin/{noticeId}")
    public ResponseEntity<ApiResult> adminNotice(@PathVariable("noticeId") Long noticeId, @RequestHeader(value = "X-USER-ROLE", defaultValue = "USER") String role) {
        ApiResult apiResult = new ApiResult();

        if (!"ADMIN".equals(role)) {
            throw new ForbiddenException(AUTHENTICATION_FAILED);
        }

        NoticeDto noticeDto = noticeService.search(noticeId);

        apiResult.setData(noticeDto);

        return ResponseEntity.ok(apiResult);
    }

    @PutMapping("/{noticeId}")
    public ResponseEntity<ApiResult> modifyNotice(@PathVariable("noticeId") Long noticeId, @Valid @RequestPart(value = "noticeModDto")  NoticeModDto noticeModDto, @RequestPart(value = "attachmentList", required = false) List<MultipartFile> attachmentList) {
        ApiResult apiResult = new ApiResult();

        noticeService.modifyNotice(noticeId, noticeModDto, attachmentList);

        return ResponseEntity.ok(apiResult);
    }

    @DeleteMapping("/{noticeId}")
    public ResponseEntity<ApiResult> deleteNotice(@PathVariable("noticeId") Long noticeId) {
        ApiResult apiResult = new ApiResult();

        noticeService.deleteNotice(noticeId);

        return ResponseEntity.ok(apiResult);
    }
}
