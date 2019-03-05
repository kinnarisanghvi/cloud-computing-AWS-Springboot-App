package com.csye6225.spring2019.service;

import com.csye6225.spring2019.exception.FileStorageException;
import com.csye6225.spring2019.exception.ResourceNotFoundException;
import com.csye6225.spring2019.model.Attachment;
import com.csye6225.spring2019.repository.AttachmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.UUID;

@Service
public class AttachmentStorageService {

    @Autowired
    AttachmentRepository attachmentRepository;

    @Value("${file.upload.dir}")
    private String path;

    public Attachment storeFile(MultipartFile file) {
        // Normalize file name
        String fileName = file.getOriginalFilename();
        UUID uuid = UUID.randomUUID();
        String randomUUIDString = uuid.toString();
        try {
            // Check if the file's name contains invalid characters
            if(fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            Attachment attachFile = new Attachment();

            attachFile.setAttachmentId(randomUUIDString);
            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path(path+"/")
                    .path(attachFile.getAttachmentId())
                    .toUriString();
            attachFile.setUrl(fileDownloadUri);

            return attachmentRepository.save(attachFile);
        } catch (Exception ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }

    public Attachment getFile(String fileId) {
        return attachmentRepository.findById(fileId)
                .orElseThrow(() -> new ResourceNotFoundException("File not found with id " ,"fileId" , fileId));
    }
}
