#!/usr/bin/env bash

accountid=$(aws sts get-caller-identity --output text --query 'Account')
echo "AWS AccountId: $accountid"

aws cloudformation deploy --template ./csye6225-cf-cicd-stack.json --capabilities CAPABILITY_NAMED_IAM --stack-name "$appstack" --parameter-overrides AccountId="$accountid"
