package com.rsupport.notice.dto.notice;

import com.rsupport.notice.constant.annotation.OnlyNumber;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoticeModDto {

    @NotBlank
    @Size(max = 100)
    private String title;

    @NotBlank
    private String content;

    @OnlyNumber
    @Size(min = 8, max = 8)
    private String startDate;

    @OnlyNumber
    @Size(min = 8, max = 8)
    private String endDate;

    @NotBlank
    @Size(max = 50)
    private String writer;
}
