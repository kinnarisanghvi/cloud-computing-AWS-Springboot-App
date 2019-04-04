#!/bin/bash -e

#STACKPREFIX="WAF"
#STACKSCOPE="Regional"
#RULEACTION="BLOCK"
#INCLUDESPREFIX="/includes"
#ADMINURLPREFIX="/admin"
#ADMINREMOTECIDR="127.0.0.1/32"
#MAXEXPECTEDURISIZE=512
#MAXEXPECTEDQUERY=1024
#MAXEXPECTEDBODY=4096
#MAXEXPECTEDCOOKIESIZE=4093
#CSRFEXPECTEDHEADER="x-csrf-token"
#CSRFEXPECTEDSIZE=36

echo "Please Enter the WAF Stack Name: "
read wafstack

echo "Enter bucketname"
read bucketName

LOADBALANCER=$(aws elbv2 describe-load-balancers --query LoadBalancers[0].LoadBalancerArn --output text)

echo "ELBResourceARN: $LOADBALANCER"

echo "upload nested stack to s3"
aws s3 cp ./waf-owasp.yaml s3://csye6225-spring2019-"$bucketName".me.csye6225.com/ 

echo "Starting waf creation"
aws cloudformation create-stack --stack-name "$wafstack" --template-body file://./waf-owasp.yaml --parameters ParameterKey=LOADBALANCER,ParameterValue=$LOADBALANCER
aws cloudformation wait stack-create-complete --stack-name "$wafstack"
STACKDETAILS=$(aws cloudformation describe-stacks --stack-name "$wafstack" --query Stacks[0].StackId --output text)