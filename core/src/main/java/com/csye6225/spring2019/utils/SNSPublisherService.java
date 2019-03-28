package com.csye6225.spring2019.utils;

import com.amazonaws.AmazonServiceException;

public interface SNSPublisherService {
    //
    // Name of the topic
    //
    public static final String TOPIC_RESETPASSWORD = "password_reset";
    //
    // Publish Message API
    //
    void publish(String message, String topic) throws AmazonServiceException;
}