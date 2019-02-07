#!/usr/bin/env bash

set -e

echo "Please enter the Stack Name: "
read stackname

aws cloudformation delete-stack --stack-name "$stackname"

echo "Your Stack "$stackname" has been removed sucessfully"