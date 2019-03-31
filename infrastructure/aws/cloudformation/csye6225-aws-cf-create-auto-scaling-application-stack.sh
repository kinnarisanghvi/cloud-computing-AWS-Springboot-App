#!/usr/bin/env bash

set -e

echo "Please Enter the Auto-Scaling Stack Name: "
read autoscalestack

echo "Please Enter the Network stack name: "
read networkstack

echo "Please Enter the CICD stack name: "
read cicdname

echo "True/False for AssociatePublicIpAddress: "
read associatePublicAddress

echo "Please Enter the Application Stack Name: "
read appstack

AMIID=$(aws ec2 describe-images --owners self --query 'sort_by(Images, &CreationDate)[-1].ImageId' --output text)
echo "AMI ID:${AMIID}"

bucketName=$(aws route53 list-hosted-zones --query "HostedZones[0].Name" --output text)
bucketName="code-deploy.$bucketName"
echo "Bucket name: $bucketName"

hostedzoneid=$(aws route53 list-hosted-zones --query HostedZones[0].Id --output=text | awk -F '/' '{ print $3 }')
echo "Hosted zone id: $hostedzoneid"

hostedzonename=$(aws route53 list-hosted-zones --query "HostedZones[0].Name" --output text)
echo "Hosted zone name: $hostedzonename"

keypair=$(aws ec2 describe-key-pairs --query "KeyPairs[0].KeyName" --output text)
echo "Key pair name: $keypair"

accountid=$(aws sts get-caller-identity --output text --query 'Account')
echo "AWS AccountId: $accountid"


aws cloudformation deploy --template ./csye6225-cf-auto-scaling-application.json --stack-name "$autoscalestack" --parameter-overrides NetworkStackParameters="$networkstack" CICDStackParameter="$cicdname" KeyPairName="$keypair" BucketName="$bucketName" AccountId="$accountid" LaunchConfigurationName="$launchConfigurationName" EC2ImageId="$ec2ImageId" EC2InstanceType="$ec2InstanceType" HostedZoneId="$hostedzoneid" HostedZoneName="$hostedzonename" CertificateArn="$certificatearn" ApplicationName="$codedeployapplicationname" AssociatePublicAddress="$associatePublicAddress"



