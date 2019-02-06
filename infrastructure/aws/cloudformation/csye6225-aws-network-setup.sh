#!/usr/bin/env bash

set -e

vpcblock="10.0.0.0/16"

subnetblock_1="10.0.1.0/24"
subnetblock_2="10.0.2.0/24"
subnetblock_3="10.0.3.0/24"

zone_1="us-east-1a"
zone_2="us-east-1b"
zone_3="us-east-1c"
AWS_REGION="us-east-1"

echo "Enter the network stack name"
read stackname

aws cloudformation deploy --template ./csye6225-cf-networking.json --stack-name "$stackname" --parameter-overrides subnetblock_1="$subnetip1" subnetblock_2="$subnetip2" subnetblock_3="$subnetip3" vpcblock="$vpcblock" zone_1="$zone1" zone_2="$zone2" zone_3="$zone3" AWS_REGION="$region"


