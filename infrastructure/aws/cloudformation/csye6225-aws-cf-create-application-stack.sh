#!/usr/bin/env bash

set -e

echo "Please Enter the Application Stack Name: "
read appstack

echo "Please enter the key pair name for your stack:"
read keyname

#echo "Please enter your AMI ID"
#read AMIID

accountid=$(aws sts get-caller-identity --output text --query 'Account')
echo "AWS AccountId: $accountid"

export VPCID=$(aws ec2 describe-vpcs --filters "Name=tag-key,Values=Name" --query "Vpcs[*].[CidrBlock, VpcId][-1]" --output text|grep 10.0.0.0/16|awk '{print $2}')

echo "vpcId : $VPCID"

export subnetID1=$(aws ec2 describe-subnets --filters "Name=vpc-id,Values=$VPCID" --query 'Subnets[*].[SubnetId, VpcId, AvailabilityZone, CidrBlock]' --output text|grep 10.0.1.0/24|grep us-east-1a|awk '{print $1}')

echo "subnetid1 : ${subnetID1}"

export subnetID2=$(aws ec2 describe-subnets --filters "Name=vpc-id,Values=$VPCID" --query 'Subnets[*].[SubnetId, VpcId, AvailabilityZone, CidrBlock]' --output text|grep 10.0.2.0/24|grep us-east-1b|awk '{print $1}')

echo "subnetid2 : ${subnetID2}"

export subnetID3=$(aws ec2 describe-subnets --filters "Name=vpc-id,Values=$VPCID" --query 'Subnets[*].[SubnetId, VpcId, AvailabilityZone, CidrBlock]' --output text|grep 10.0.3.0/24|grep us-east-1c|awk '{print $1}')

echo "subnetid3 : ${subnetID3}"

echo "Creating Application stack"
        export AMIID=$(aws ec2 describe-images --query 'sort_by(Images, &CreationDate)[-1].ImageId' --output text)
        echo "AMI ID:${AMIID}"
	while [ ${AMIID} != "" ]; 
        do
        STACK_STATUS=$(aws cloudformation deploy --template ./csye6225-cf-application.json --stack-name "$appstack" --parameter-overrides KeyPairName="$keyname" AccountId="$accountid" VPCID="$VPCID" subnetID1="$subnetID1" subnetID2="$subnetID2" subnetID3="$subnetID3" AMIID="$AMIID")
        echo $STACK_STATUS
        done
        echo "Stack ${appstack} Created successfully!"
                exit 1



