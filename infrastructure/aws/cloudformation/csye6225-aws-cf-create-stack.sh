#!/usr/bin/env bash

#vpcblock="10.0.0.0/16"

#subnetblock_1="10.1.0.0/24"
#subnetblock_2="10.2.0.0/24"
#subnetblock_3="10.3.0.0/24"

#zone_1="us-east-1a"
#zone_2="us-east-1b"
#zone_3="us-east-1c"
#AWS_REGION="us-east-1"


export STACK_NAME=$1
export STACK_ID=$(aws cloudformation create-stack --template-body file://./csye6225-cf-networking.json --stack-name "$STACK_NAME")
echo "$STACK_ID"
echo "Creating Stackname"
        export STACK_STATUS=$(aws cloudformation describe-stacks --stack-name $STACK_NAME --query "Stacks[][ [StackStatus ] ][]" --output text)
	while [ ${STACK_STATUS} != "CREATE_COMPLETE" ]; 
        do
        STACK_STATUS=$(aws cloudformation describe-stacks --stack-name $STACK_NAME --query "Stacks[][ [StackStatus ] ][]" --output text)
        echo $STACK_STATUS
        done
        echo "Stack ${STACK_NAME} Created successfully!"
                exit 1
        


