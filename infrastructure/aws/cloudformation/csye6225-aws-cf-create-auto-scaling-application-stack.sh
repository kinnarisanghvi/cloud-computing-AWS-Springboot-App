#!/usr/bin/env bash

set -e

echo "Please Enter the Auto-Scaling Stack Name: "
read autoscalestack

AMIID=$(aws ec2 describe-images --owners self --query 'sort_by(Images, &CreationDate)[-1].ImageId' --output text)
echo "AMI ID:${AMIID}"

echo "Enter bucketname"
read bucketName

hostedzoneid=$(aws route53 list-hosted-zones --query HostedZones[0].Id --output=text | awk -F '/' '{ print $3 }')
echo "Hosted zone id: $hostedzoneid"

hostedzonename=$(aws route53 list-hosted-zones --query "HostedZones[0].Name" --output text)
echo "Hosted zone name: $hostedzonename"

keypair=$(aws ec2 describe-key-pairs --query "KeyPairs[0].KeyName" --output text)
echo "Key pair name: $keypair"

accountid=$(aws sts get-caller-identity --output text --query 'Account')
echo "AWS AccountId: $accountid"

export VPCID=$(aws ec2 describe-vpcs --filters "Name=cidr,Values=10.0.0.0/16" --query "Vpcs[*].[CidrBlock, VpcId][-1]" --output text|grep 10.0.0.0/16|awk '{print $2}')

echo "vpcId : $VPCID"

export subnetID1=$(aws ec2 describe-subnets --filters "Name=vpc-id,Values=$VPCID" --query 'Subnets[*].[SubnetId, VpcId, AvailabilityZone, CidrBlock]' --output text|grep 10.0.1.0/24|grep us-east-1a|awk '{print $1}')

echo "subnetid1 : ${subnetID1}"

export subnetID2=$(aws ec2 describe-subnets --filters "Name=vpc-id,Values=$VPCID" --query 'Subnets[*].[SubnetId, VpcId, AvailabilityZone, CidrBlock]' --output text|grep 10.0.2.0/24|grep us-east-1b|awk '{print $1}')

echo "subnetid2 : ${subnetID2}"


export subnetID3=$(aws ec2 describe-subnets --filters "Name=vpc-id,Values=$VPCID" --query 'Subnets[*].[SubnetId, VpcId, AvailabilityZone, CidrBlock]' --output text|grep 10.0.3.0/24|grep us-east-1c|awk '{print $1}')
echo "subnetid3 : ${subnetID3}"

certificatearn=$(aws acm list-certificates --query "CertificateSummaryList[0].CertificateArn" --output text)
echo "Certificate Arn is $certificatearn"

certificatearn2=$(aws acm list-certificates --query "CertificateSummaryList[1].CertificateArn" --output text)
echo "Certificate Arn is $certificatearn2"

echo "upload nested stack to s3"
aws s3 cp ./waf-owasp.yaml s3://csye6225-spring2019-"$bucketName".me.csye6225.com/ 

aws cloudformation deploy --template ./csye6225-cf-auto-scaling-application.json --capabilities CAPABILITY_NAMED_IAM --stack-name "$autoscalestack" --parameter-overrides KeyPairName="$keypair" bucketName="$bucketName" AccountId="$accountid" EC2ImageId="$ec2ImageId" EC2InstanceType="$ec2InstanceType" HostedZoneId="$hostedzoneid" HostedZoneName="$hostedzonename" CertificateArn="$certificatearn" AssociatePublicAddress="$associatePublicAddress" VPCID="$VPCID" subnetID1="$subnetID1" subnetID2="$subnetID2" subnetID3="$subnetID3" AMIID="$AMIID" CertificateArn="$certificatearn" CertificateArn2="$certificatearn2"



