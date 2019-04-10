# CSYE 6225 - Spring 2019

## Team Information

| Name | NEU ID | Email Address |
| --- | --- | --- |
| Jinansi Thakkar| 001835505 | thakkar.j@husky.neu.edu |
| Kinnari Sanghvi| 001837528| sanghvi.ki@husky.neu.edu |
| Vignesh Raghuraman| 001837157| raghuraman.v@husky.neu.edu |
| Karan Magdani | 001250476 | magdani.k@husky.neu.edu |

## Technology Stack
The application is developed using spring boot and uses rest controllers to achieve use case objectives.
The application blocks any clear text requests and serves only HTTPS requests.  
Various AWS services are used to create and deploy the application.

## Build Instructions

Clone this repository into your local system. For building the application, you can use circleCI which deploys the application automatically for you. Maven dependencies will be downloaded when the application is deployed. 

Get a domain name and make sure it is verified with AWS.
Create an s3 bucket named 'csye6225-spring2019-YourName.me.csye6225.com'
Get an SSL certificate for your domain name.
Create the application stack by running Run the script 'csye6225-aws-cf-create-auto-scaling-application-stack.sh' present in 'csye6225-spring2019/infrastructure/aws/cloudformation/' path. You need to input desired stack name and 'YourName'(used to create the bucketname, this will basically create codeDeploy bucket name where the webApp.zip file is placed). 

## Deploy Instructions
Circle CI has been implemented to trigger builds on any commit to the branches thereby facilitating latest code movement to AWS EC2 instance.
The war file generated is stored in Amazon S3 bucket instance which is then moved using AWS Codedeploy.

Use curl command - 
curl -u 4191ca5fe7957b65d269404bc5b5a5dbbfdcbc57 -d build_parameters[CIRCLE_JOB]=build     https://circleci.com/api/v1.1/project/github/GITHUB-USERNAME/REPOSITORY-NAME/tree/BRANCH-NAME

This command will trigger circleCI to start deploying your web application

## Running Tests.
JMeter is used for load testing. 
In JMeter, make sure to add your domain name and add the correct path for 'csv data config' which specifies the path of your users.csv file your local system. 

## CI/CD
CircleCI is used for CI/CD. 

CI - 
In your config.yml file under the 'branches only' of the build section, update the BRANCH-NAME to trigger a build automatically. As soon a commit is made to that branch, a build is triggered.

CD -
As soon as a build is triggered, you should be able to see that all the maven dependencies are downloaded and there is a zip file present in the S3 bucket. Your web application will be available in the AWS console under deployments. 

## Final run

Once the deployment is completed, you can try hitting the domain name to check if your application is working.

