#!/bin/bash
#create-aws-vpc

#variables used in script:
vpcName=$1
availabilityZone1=$2
availabilityZone2=$3
availabilityZone=$4
subnetName1=${1}-csye6225-subnet1
subnetName2=${1}-csye6225-subnet2
subnetName3=${1}-csye6225-subnet3
internetGateway=${1}-csye6225-IG
routeTable=${1}-rTable-csye6225-rTable

export VPC_ID=$(aws ec2 create-vpc --cidr-block 10.0.0.0/16 --instance-tenancy default --query 'Vpc.VpcId' --output text)

if [ $? -eq 0 ]
then 
	aws ec2 create-tags --resources $VPC_ID --tags Key=Name,Value=$vpcName
	echo "VPC created ${vpcName} successfully!"

	echo "Creating Subnet1..."
	export SUBNET1_ID=$(aws ec2 create-subnet --vpc-id $VPC_ID --availability-zone ${2} --cidr-block 10.0.1.0/24 --query 'Subnet.SubnetId' --output text)
	if [ $? -eq 0 ]
	then
		echo "Subnet 1 ${subnetName1} Created successfully!"
		aws ec2 create-tags --resources $SUBNET1_ID --tags Key=Name,Value=$subnetName1
	else
		echo "Creation of Subnet 1 Failed"
		exit 1
	fi

	echo "Creating Subnet2..."
	export SUBNET2_ID=$(aws ec2 create-subnet --vpc-id $VPC_ID --availability-zone ${3} --cidr-block 10.0.2.0/24 --query 'Subnet.SubnetId' --output text)
	if [ $? -eq 0 ]
	then
		echo "Subnet 2 ${subnetName2} Created successfully!"
		aws ec2 create-tags --resources $SUBNET2_ID --tags Key=Name,Value=$subnetName2
	else
		echo "Creation of Subnet 2 Failed"
		exit 1
	fi

	echo "Creating Subnet3..."
	export SUBNET3_ID=$(aws ec2 create-subnet --vpc-id $VPC_ID --availability-zone ${4} --cidr-block 10.0.3.0/24 --query 'Subnet.SubnetId' --output text)
	if [ $? -eq 0 ]
	then
		echo "Subnet 3 ${subnetName3} Created successfully!"
		aws ec2 create-tags --resources $SUBNET3_ID --tags Key=Name,Value=$subnetName3
	else
		echo "Creation of Subnet 3 Failed"
		exit 1
	fi

echo "Creating Internet Gateway"
	export IG_ID=$(aws ec2 create-internet-gateway --query 'InternetGateway.InternetGatewayId' --output text)
	echo $IG_ID
	if [ $? -eq 0 ]
	then
		echo "Internet Gateway ${internetGateway} created successfully!"
		aws ec2 create-tags --resources $IG_ID --tags Key=Name,Value=$internetGateway
	else
		echo "Creation of Internet Gateway Failed"
		exit 1
	fi

echo "Attaching Internet Gateway to VPC"
	aws ec2 attach-internet-gateway --vpc-id $VPC_ID --internet-gateway-id $IG_ID
	if [ $? -eq 0 ]
	then
		echo "Internet gateway attached to VPC successfully!"
	else
		echo "Internet gateway attached to VPC failed"
		exit 1
	fi

echo "Creating a public route table"
	export ROUTE_TABLE_ID=$(aws ec2 create-route-table --vpc-id $VPC_ID --query "RouteTable.RouteTableId" --output text)
	if [ $? -eq 0 ]
	then
		echo "Public Route Table ${routeTable} created successfully!"
		aws ec2 create-tags --resources $ROUTE_TABLE_ID --tags Key=Name,Value=$routeTable
	else
		echo "Creation of Route Table failed"
		exit 1
	fi

	echo "Attaching subnets to Public Route Table"
	aws ec2 associate-route-table --route-table-id $ROUTE_TABLE_ID --subnet-id $SUBNET1_ID
	if [ $? -eq 0 ]
	then
		echo "Attached Subnet1 ${subnetName1} to Public Route Table ${routeTable} successfully!"
	else
		echo "Failed to attach Subnet1 ${subnetName1} to Route Table ${routeTable} failed"
		exit 1
	fi

	aws ec2 associate-route-table --route-table-id $ROUTE_TABLE_ID --subnet-id $SUBNET2_ID
	if [ $? -eq 0 ]
	then
		echo "Attached Subnet2 ${subnetName2} to Public Route Table ${routeTable} successfully!"
	else
		echo "Failed to attach Subnet2 ${subnetName2} to Route Table ${routeTable} failed"
		exit 1
	fi

	aws ec2 associate-route-table --route-table-id $ROUTE_TABLE_ID --subnet-id $SUBNET3_ID
	if [ $? -eq 0 ]
	then
		echo "Attached Subnet3 ${subnetName3} to Public Route Table ${routeTable} successfully!"
	else
		echo "Failed to attach Subnet3 ${subnetName3} to Route Table ${routeTable} failed"
		exit 1
	fi

	echo "Creating a public route in the public route table"
	aws ec2 create-route --route-table-id $ROUTE_TABLE_ID --destination-cidr-block 0.0.0.0/0 --gateway-id $IG_ID
	if [ $? -eq 0 ]
	then
		echo "Public Route created successfully!"
	else
		echo "Creation of Route failed"
		exit 1
	fi
    
groupid=$(aws ec2 describe-security-groups --filters Name=vpc-id,Values=$VPC_ID --query "SecurityGroups[*].{ID:GroupId}" --output text)
    echo $groupid
    echo "Removing default security rule"
    aws ec2 revoke-security-group-ingress --group-id $groupid --protocol "-1" --port -1 --source-group $groupid
    aws ec2 revoke-security-group-egress --group-id $groupid --protocol "-1" --port -1 --cidr 0.0.0.0/0
    aws ec2 authorize-security-group-ingress --group-id $groupid --protocol tcp --port 22 --cidr 203.0.113.0/24
    # aws ec2 authorize-security-group-ingress --group-id sg-111aaa22 --protocol tcp --port 80 
else
	echo "Creation of VPC Failed"
	exit 1
fi


