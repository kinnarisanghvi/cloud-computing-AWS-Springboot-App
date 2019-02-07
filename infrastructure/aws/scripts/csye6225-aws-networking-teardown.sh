#!/usr/bin/env bash

#Deleting VPC along with all the associated resources

#Asking User for all the ids and assigning them to Variables

echo "Please Enter the VPC ID: "

read vpc_id

echo "Please Enter the Route ID: "

read route_id

echo "Please Enter the Internet Gateway ID: "

read igy_id

echo "Please Enter the all 3 Subnet ids: "

read sub1 sub2 sub3

# Deleting Internet Gateway

aws ec2 detach-internet-gateway \
    --internet-gateway-id "$igy_id" \
    --vpc-id "$vpc_id"
echo "$igy_id" has been detached sucessfully

aws ec2 delete-internet-gateway \
    --internet-gateway-id "$igy_id"
echo "$igy_id" has been removed

# Deleting the Subnets

aws ec2 delete-subnet \
    --subnet-id "$sub1"
echo "$sub1" has been removed

aws ec2 delete-subnet \
    --subnet-id "$sub2"
echo "$sub2" has been removed

aws ec2 delete-subnet \
    --subnet-id "$sub3"
echo "$sub3" has been removed

# Deleting Route Table

aws ec2 delete-route-table \
    --route-table-id "$route_id"
echo "$route_id" has been removed

# Deleting VPC using the VPC ID

aws ec2 delete-vpc \
    --vpc-id "$vpc_id"
echo "$vpc_id" successfully removed
