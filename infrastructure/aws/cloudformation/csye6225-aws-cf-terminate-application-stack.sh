#!/usr/bin/env bash

set -e

export STACK_NAME=$1
export STACK_ID=$(aws cloudformation delete-stack --stack-name "$STACK_NAME")
echo "$STACK_ID"
echo "Deleting Stackname"

export STACK_STATUS=$(aws cloudformation describe-stacks --stack-name $STACK_NAME --query "Stacks[][ [StackStatus ] ][]" --output text)
	while [ ${STACK_STATUS} != "DELETE_COMPLETE" ]; 
        do
        STACK_STATUS=$(aws cloudformation describe-stacks --stack-name $STACK_NAME --query "Stacks[][ [StackStatus ] ][]" --output text)
        echo $STACK_STATUS
        done
        echo "Stack deleted successfully!"
                exit 1

