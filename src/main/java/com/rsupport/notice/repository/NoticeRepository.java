package com.rsupport.notice.repository;

import com.rsupport.notice.entity.Notice;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT n FROM Notice n WHERE n.noticeId = :noticeId")
    Optional<Notice> findWithLockByNoticeId(Long noticeId);
}
