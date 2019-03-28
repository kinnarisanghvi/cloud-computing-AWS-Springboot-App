package com.csye6225.spring2019.utils;

import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClient;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClientBuilder;
import com.amazonaws.services.securitytoken.model.Credentials;
import com.amazonaws.services.securitytoken.model.GetSessionTokenRequest;
import com.amazonaws.services.securitytoken.model.GetSessionTokenResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Profile("dev")

public class AWSAppConfig {

    @Value("${aws.sns.topic.resetpassword.ARN}") String snsTopicResetPasswordARN;


    @Bean(name = "snsTopicResetPasswordARN")
    public String snsTopicARN() {
        return this.snsTopicResetPasswordARN;
    }

}