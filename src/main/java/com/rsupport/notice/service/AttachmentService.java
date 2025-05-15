package com.rsupport.notice.service;

import com.rsupport.notice.config.exception.FileUploadException;
import com.rsupport.notice.constant.annotation.enums.ErrorCode;
import com.rsupport.notice.entity.Attachment;
import com.rsupport.notice.entity.Notice;
import com.rsupport.notice.repository.AttachmentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class AttachmentService {

    @Value("${file.path}")
    private String baseUploadDir;

    private final AttachmentRepository attachmentRepository;

    public void registerFileList(List<MultipartFile> multipartFileList, Notice notice) {
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String uploadDir = Paths.get(System.getProperty("user.dir"), baseUploadDir, datePath).toString();

        File dir = new File(uploadDir);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (!created) {
                throw new FileUploadException(ErrorCode.ATTACHMENT_DIR_FAILED, uploadDir);
            }
        }

        List<Attachment> attachmentList = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFileList) {
            String originalName = multipartFile.getOriginalFilename();
            String uuid = UUID.randomUUID().toString();
            String fileName = uuid + "_" + originalName;
            String filePath = uploadDir + "/" + fileName;
            String fileSize = String.valueOf(multipartFile.getSize());

            try {
                multipartFile.transferTo(new File(filePath));
            } catch (IOException e) {
                throw new FileUploadException(ErrorCode.ATTACHMENT_UPLOAD_FAILED, originalName);
            }

            Attachment attachment = Attachment.builder()
                    .originalName(originalName)
                    .fileName(fileName)
                    .filePath(filePath)
                    .fileSize(fileSize)
                    .notice(notice)
                    .build();

            attachmentList.add(attachment);
        }

        notice.registerAttachmentList(attachmentList);

        attachmentRepository.saveAll(attachmentList);
    }
}
