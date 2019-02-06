#!/usr/bin/env bash

#vpcblock="10.0.0.0/16"

#subnetblock_1="10.1.0.0/24"
#subnetblock_2="10.2.0.0/24"
#subnetblock_3="10.3.0.0/24"

#zone_1="us-east-1a"
#zone_2="us-east-1b"
#zone_3="us-east-1c"
AWS_REGION="us-east-1"

echo "Please Enter the Stack Name: "
read stackname

echo " Please enter the VPC CIDR IP: "
read vpcblock

echo "Please enter the Subnet-1 for each availability zone using commas: (ex: 10.x.x.x/24,20.y.y.y.y/24,..)"
read subnetblock_1

echo "Please enter the Subnet-2 for each availability zone using commas: (ex: 10.x.x.x/24,20.y.y.y.y/24,..)"
read subnetblock_2

echo "Please enter the Subnet-3 for each availability zone using commas: (ex: 10.x.x.x/24,20.y.y.y.y/24,..)"#
read subnetblock_3

echo "Availability Zones for the selected region are: "
aws ec2 describe-availability-zones --region "$AWS_REGION"

echo "Please enter the Availability zone-1 in $AWS_REGION using commas: (ex: us-east-1a,us-east-1b,...)"
read zone_1

echo "Please enter the Availability zone-1 in $AWS_REGION using commas: (ex: us-east-1a,us-east-1b,...)"
read zone_2

echo "Please enter the Availability zone-1 in $AWS_REGION using commas: (ex: us-east-1a,us-east-1b,...)"
read zone_3

aws cloudformation deploy --template ./csye6225-cf-networking.json --stack-name "$stackname" --parameter-overrides vpcblock="$vpcblock" subnetip1="$subnetblock_1" subnetip2="$subnetblock_2" subnetip3="$subnetblock_3" zone1="$zone_1" zone2="$zone_2" zone3="$zone_3" AWS_REGION="$region"


