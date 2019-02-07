#CloudFormation Scripts

### 1. Create Stack and Terminate Stack
- `csye6225-aws-cf-create-stack.sh`
    - Takes name of stack as input and creates a cloudformation stack
    - `aws cloudformation create-stack --template-body file://./csye6225-cf-networking.json --stack-name "$stackname"`

- `csye6225-aws-cf-terminate-stack.sh`
    - Takes name of stack as input and deletes an existing cloudformation stack
    - `aws cloudformation delete-stack --stack-name "$stackname"`

----------

### 2. Network Setup and Teardown
- `csye6225-aws-networking-setup.sh`
    - Takes multiple values (`vpcName`, `availabilityZone`, `subnet`, `internetGateway`, `routingTable`) from user as input and sets up network
- `csye6225-aws-networking-teardown.sh`
    - Takes multiple values (`vpcName`, `availabilityZone`, `subnet`, `internetGateway`, `routingTable`) from user as input and tears down the network.
----------
