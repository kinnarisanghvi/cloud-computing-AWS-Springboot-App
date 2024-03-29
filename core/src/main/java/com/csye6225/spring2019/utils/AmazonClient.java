package com.csye6225.spring2019.utils;


import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.Date;

@Service
@Profile("dev")

public class AmazonClient {

    private AmazonS3 s3client;

    @Autowired
    SNSPublisherService publisherService;

    @Value("${aws.region}")
    private String awsRegion;

    @Value("${aws.s3.audio.bucket}")
    private String awsS3AudioBucket;


    @PostConstruct
    private void initializeAmazon() {
        this.s3client = new AmazonS3Client(new InstanceProfileCredentialsProvider(false));
    }


    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(System.getProperty("java.io.tmpdir") + System.getProperty("file.separator") +
                file.getOriginalFilename());
        file.transferTo(convFile);
        return convFile;

    }
    private void uploadFileTos3bucket(String fileName, File file) {
        s3client.putObject(new PutObjectRequest(awsS3AudioBucket, fileName, file)
                .withCannedAcl(CannedAccessControlList.PublicRead));

    }

    public String uploadFile(MultipartFile multipartFile) {

        String fileUrl = "";
        try {
            File file = convertMultiPartToFile(multipartFile);
            String fileName = generateFileName(multipartFile);
            fileUrl = "https://s3.amazonaws.com"+ "/" + awsS3AudioBucket + "/" + fileName;
            System.out.println("file s3 upload url: "+ fileUrl);
            uploadFileTos3bucket(fileName, file);
            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileUrl;
    }

    private String generateFileName(MultipartFile multiPart) {
        return new Date().getTime() + "-" + multiPart.getOriginalFilename().replace(" ", "_");
    }

    public String deleteFileFromS3Bucket(String fileUrl) {
        String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
        s3client.deleteObject(new DeleteObjectRequest(awsS3AudioBucket , fileName));
        return "Successfully deleted";
    }

    public String publishSNSTopic(String key,String emailID) {
        try {
            this.publisherService.publish("{ \"emailID\": \"" + emailID + "\"}",  SNSPublisherService.TOPIC_RESETPASSWORD);
        } catch (AmazonServiceException e) {
            e.printStackTrace();
        }
        return "Topic Published";
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }



}