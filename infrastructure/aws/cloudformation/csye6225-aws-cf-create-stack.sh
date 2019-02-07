#!/usr/bin/env bash

#vpcblock="10.0.0.0/16"

#subnetblock_1="10.1.0.0/24"
#subnetblock_2="10.2.0.0/24"
#subnetblock_3="10.3.0.0/24"

#zone_1="us-east-1a"
#zone_2="us-east-1b"
#zone_3="us-east-1c"
#AWS_REGION="us-east-1"

echo "Please Enter the Stack Name: "
read stackname

aws cloudformation create-stack --template-body file://./csye6225-cf-networking.json --stack-name "$stackname"


