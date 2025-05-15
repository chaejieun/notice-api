package com.rsupport.notice.repository;

import com.rsupport.notice.entity.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

    void deleteByNoticeNoticeId(Long noticeId);
}
