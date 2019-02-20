#!/usr/bin/env bash

set -e

echo "Please Enter the Application Stack Name: "
read appstack

echo "Please enter the key pair name for your stack: "
read keyname

echo "Please enter the VPCID: "
read VPCID

echo "Please enter the subnetID1:"
read subnetID1 

echo "Please enter subnetId2:"
read subnetID2

echo "Please enter your AMI ID"
read AMIID

export vpcId=$(aws ec2 describe-vpcs --filters "Name=tag-key,Values=Name" --query "Vpcs[*].[CidrBlock, VpcId]" --output text|grep 10.0.0.0/16|awk '{print $2}')
echo "vpcId : $vpcId"

export subnetId1=$(aws ec2 describe-subnets --filters "Name=vpc-id,Values=$vpcId" --query 'Subnets[*].[SubnetId, VpcId, AvailabilityZone, CidrBlock]' --output text|grep 10.0.1.0/24|grep us-east-1a|awk '{print $1}')

echo "subnetid : ${subnetId1}"

export subnetId2=$(aws ec2 describe-subnets --filters "Name=vpc-id,Values=$vpcId" --query 'Subnets[*].[SubnetId, VpcId, AvailabilityZone, CidrBlock]' --output text|grep 10.0.2.0/24|grep us-east-1b|awk '{print $1}')

echo "subnetid2 : ${subnetId2}"

aws cloudformation deploy --template ./csye6225-cf-application.json --stack-name "$appstack" --parameter-overrides KeyPairName="$keyname" VPCID="$VPCID" subnetID1="$subnetID1" subnetID2="$subnetID2" AMIID="$AMIID"

