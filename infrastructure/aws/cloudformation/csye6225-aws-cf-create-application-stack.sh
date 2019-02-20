#!/usr/bin/env bash

set -e

echo "Please Enter the Application Stack Name: "
read appstack

echo "please Enter the network stack name: "
read networkstack

echo "Please enter the key pair name for your stack: "
read keyname


aws cloudformation deploy --template ./csye6225-cf-application.json --stack-name "$appstack" --parameter-overrides NetworkStackParameters="$networkstack" KeyPairName="$keyname"


