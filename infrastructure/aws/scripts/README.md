 Network Setup and Teardown

### 1. Network Setup 

   -`csye6225-aws-networking-setup.sh`
   
   -Takes name of stack and 3 availabality zones as input and creates a network
   
   Steps to run the csye6225-aws-networking-setup.sh file :
   1. On command prompt, go into the folder csye6225-spring2019/infrastructure/aws/scripts/
   2. Run ./csye6225-aws-networking-setup.sh 'your_stack_name' 'availabilityZone1' 'availabilityZone2' 'availabilityZone3'
   3. Hit enter
   

### 2. Teardown network

   -`csye6225-aws-networking-teardown.sh`
   
   -Takes VPC ID, Internet Gateway ID, Route table ID, IDs of all the Subnets as input and deletes an existing VPC network.
   
   Steps to run the csye6225-aws-networking-setup.sh file :
   1. On command prompt, go into the folder csye6225-spring2019/infrastructure/aws/scripts/
   2. run csye6225-aws-networking-teardown.sh
   3. You will be prompted to enter VPC ID, Internet Gateway ID, Route table ID and IDs of all the subnets   
   4. Your VPC will be deleted

