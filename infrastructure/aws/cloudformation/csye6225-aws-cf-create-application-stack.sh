#!/usr/bin/env bash

set -e

echo "Please Enter the Application Stack Name: "
read appstack

echo "Please enter the key pair name for your stack: "
read keyname

echo "Please enter the VPCID: "
read VPCID

echo "Please enter the subnetID: "
read subnetID 

echo "Please enter your AMI ID"
read AMIID

aws cloudformation deploy --template ./csye6225-cf-application.json --stack-name "$appstack" --parameter-overrides KeyPairName="$keyname" VPCID="$VPCID" subnetID="$subnetID" AMIID="$AMIID"

