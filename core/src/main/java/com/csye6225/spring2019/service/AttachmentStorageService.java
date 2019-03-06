package com.csye6225.spring2019.service;

import com.csye6225.spring2019.exception.FileStorageException;
import com.csye6225.spring2019.exception.ResourceNotFoundException;
import com.csye6225.spring2019.model.Attachment;
import com.csye6225.spring2019.model.Note;
import com.csye6225.spring2019.repository.AttachmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class AttachmentStorageService {

    @Autowired
    AttachmentRepository attachmentRepository;

    @Value("${file.upload.dir}")
    private String path;

    public Attachment storeFile(MultipartFile file, Note note) {
        // Normalize file name
        String fileName = file.getOriginalFilename();
        UUID uuid = UUID.randomUUID();
        String randomUUIDString = uuid.toString();
        try {
            // Check if the file's name contains invalid characters
            if(fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            if (file.isEmpty()) {
                throw new FileStorageException("Failed to store empty file " + fileName);
            }

            try {

                InputStream is = file.getInputStream();

                Files.copy(is, Paths.get(path +"/"+ fileName),
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {

                String msg = String.format("Failed to store file", file.getName());

                throw new FileStorageException(msg);
            }

            Attachment attachFile = new Attachment();


            attachFile.setAttachmentId(randomUUIDString);
            attachFile.setNote(note);
            attachFile.getNote().setNoteId(note.getNoteId());
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

    public boolean deleteFile(String attachmentId){

        try {
            Attachment attachment = attachmentRepository.getOne(attachmentId);
            if (attachment.equals(null)) {
                return false;
            }

            File file = new File(attachment.getUrl());
            if(file.delete()) {
                System.out.println(file.getName() + " is deleted!");
                attachmentRepository.deleteById(attachment.getAttachmentId());
                return true;
            } else {
                System.out.println("Delete operation is failed.");
                return false;
            }
        }
        catch(Exception e) {
            System.out.println("Failed to Delete image !!");
        }
        return false;
    }
}
