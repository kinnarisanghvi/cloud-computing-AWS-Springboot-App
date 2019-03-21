package com.csye6225.spring2019.service;

import com.csye6225.spring2019.controller.UserController;
import com.csye6225.spring2019.exception.FileStorageException;
import com.csye6225.spring2019.exception.ResourceNotFoundException;
import com.csye6225.spring2019.model.Attachment;
import com.csye6225.spring2019.model.Note;
import com.csye6225.spring2019.repository.AttachmentRepository;
import com.timgroup.statsd.StatsDClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.UUID;

@Service
@Profile("local")
public class AttachmentStorageService {

    @Autowired
    AttachmentRepository attachmentRepository;

    @Value("${file.upload.dir}")
    private String path;
    @Autowired
    private StatsDClient statsd;

    private final static Logger LOG = LoggerFactory.getLogger(AttachmentStorageService.class);

    private Path fileStorageLocation = null;

    public Attachment storeFile(MultipartFile file, Note note) {
        // Normalize file name
        LOG.info("Inside storeFile()");

        String fileName = new Date().getTime() + "-" + file.getOriginalFilename().replace(" ", "_");
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
                this.fileStorageLocation = Paths.get(path).toAbsolutePath().normalize();
                Path targetLocation = this.fileStorageLocation.resolve(fileName);
                InputStream is = file.getInputStream();

                System.out.println("IS : "+is);
                System.out.println("location : "+ targetLocation);

                Files.copy(is,targetLocation,
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {

                System.out.println(e);
                String msg = String.format("Failed to store file", file.getName()+ " "+ e.getMessage());
                System.out.println("msg : "+msg);
                LOG.error("Failed to store file");
                throw new FileStorageException(msg);
            }

            Attachment attachFile = new Attachment();


            attachFile.setId(randomUUIDString);
            attachFile.setNote(note);
            attachFile.getNote().setId(note.getId());
            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path(path+"/")
                    .path(attachFile.getId())
                    .toUriString();
            System.out.println("file download uri: "+ fileDownloadUri);
            attachFile.setUrl(this.fileStorageLocation.resolve(fileName).toString());
            LOG.info("File saved");
            return attachmentRepository.save(attachFile);
        } catch (Exception ex) {
            LOG.error("Could not store file");
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        }
    }


    public Attachment updateAttachment(String  attachmentId, MultipartFile file){
        Attachment attachment = attachmentRepository.getOne(attachmentId);
        String fileName = new Date().getTime() + "-" + file.getOriginalFilename().replace(" ", "_");
        try{
            if(fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }
            if (file.isEmpty()) {
                throw new FileStorageException("Failed to store empty file " + fileName);
            }

            try {
                this.fileStorageLocation = Paths.get(path).toAbsolutePath().normalize();
                Path targetLocation = this.fileStorageLocation.resolve(fileName);
                InputStream is = file.getInputStream();

                System.out.println("updated location : "+ targetLocation);

                Files.copy(is,targetLocation,
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {

                String msg = String.format("Failed to store file", file.getName());

                throw new FileStorageException(msg);
            }

            attachment.setUrl(this.fileStorageLocation.resolve(fileName).toString());
            return attachmentRepository.save(attachment);
        }catch (Exception ex){
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
            System.out.println("delete file " + attachment);
//            if (attachment.equals(null)) {
//                return false;
//            }

            File file = new File(attachment.getUrl());
            System.out.println("file: "+ file.getName());
            if(file.delete()) {
                System.out.println(file.getName() + " is deleted!");
                attachmentRepository.deleteById(attachment.getId());
                LOG.info("file deleted");
                return true;
            } else {
                System.out.println("Delete operation is failed.");
                return false;
            }
        }
        catch(Exception e) {
            LOG.error("Could not delete file");
            System.out.println("Failed to Delete image !!");
        }
        return false;
    }
}
