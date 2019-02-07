#CloudFormation Scripts

### 1. Create Stack 
- `csye6225-aws-cf-create-stack.sh`
- Takes name of stack as input and creates a cloudformation stack
- `aws cloudformation create-stack --template-body file://./csye6225-cf-networking.json --stack-name "$stackname"`
Steps to run the csye6225-aws-cf-create-stack.sh file :

1. On command prompt, go into the folder csye6225-spring2019/infrastructure/aws/cloudformation/
2. ./csye6225-aws-cf-create-stack.sh 
3. You will be prompted to enter stack name
4. Enter a stack name and hit enter

### 2. Terminate Stack
- `csye6225-aws-cf-terminate-stack.sh`
- Takes name of stack as input and deletes an existing cloudformation stack
- `aws cloudformation delete-stack --stack-name "$stackname"`
Steps to run the csye6225-aws-cf-terminate-stack.sh file :
1. On command prompt, go into the folder csye6225-spring2019/infrastructure/aws/cloudformation/
2. ./csye6225-aws-cf-terminate-stack.sh 
3. You will be prompted to enter stack name
4. Enter your stack name and hit enter    

