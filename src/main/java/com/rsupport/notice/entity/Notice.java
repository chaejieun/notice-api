package com.rsupport.notice.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.rsupport.notice.dto.notice.NoticeModDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@Getter
@Setter
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long noticeId;

    private String title;
    private String content;

    private String startDate;
    private String endDate;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime regDate;

    @LastModifiedDate
    private LocalDateTime modifyDate;

    private String writer;
    private Integer viewCount = 0;

    @OneToMany(mappedBy = "notice", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Attachment> attachmentList;

    public void updateNotice(NoticeModDto dto) {
        this.title = dto.getTitle();
        this.content = dto.getContent();
        this.startDate = dto.getStartDate();
        this.endDate = dto.getEndDate();
        this.writer = dto.getWriter();
    }

    public void increaseViewCount() {
        this.viewCount += 1;
    }

    public void registerAttachmentList(List<Attachment> attachmentList) {
        this.attachmentList = attachmentList;
    }
}
