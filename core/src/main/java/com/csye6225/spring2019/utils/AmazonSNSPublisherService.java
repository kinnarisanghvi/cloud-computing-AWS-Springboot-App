package com.csye6225.spring2019.utils;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.csye6225.spring2019.utils.SNSPublisherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;

@Service
public class AmazonSNSPublisherService implements SNSPublisherService {

    private AmazonSNS amazonSNS;
    private String snsTopicResetPasswordARN;

    @Autowired
    public AmazonSNSPublisherService(String snsTopicResetPasswordARN) {
//        this.amazonSNS = AmazonSNSClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(sessionCredentials)).build();
        this.amazonSNS = AmazonSNSClientBuilder.standard().withCredentials(new InstanceProfileCredentialsProvider(false)).build();
        this.snsTopicResetPasswordARN = snsTopicResetPasswordARN;
    }

    @Override
    public void publish(String message, String topic) throws AmazonServiceException {
        //
        // Get Appropriate Topic ARN
        //
        String snsTopic = getTopicARN(topic);
        //
        // Create Publish Message Request with TopicARN and Message
        //
        PublishRequest publishRequest = new PublishRequest(snsTopic, message);
        //
        // Publish the message
        //
        PublishResult publishResult = this.amazonSNS.publish(publishRequest);
        //
        // Evaluate the result: Print MessageId of message published to SNS topic
        //
        System.out.println("MessageId - " + publishResult.getMessageId());

    }

    private String getTopicARN(String topic) throws AmazonServiceException {
        switch(topic) {
            case TOPIC_RESETPASSWORD:
                return this.snsTopicResetPasswordARN;
            default:
                throw new AmazonServiceException("No matching topic supported!");
        }
    }
}