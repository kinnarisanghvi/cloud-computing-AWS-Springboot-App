# CSYE 6225 - Spring 2019

## Team Information

| Name | NEU ID | Email Address |
| --- | --- | --- |
| Jinansi Thakkar| 001835505 | thakkar.j@husky.neu.edu |
| Kinnari Sanghvi| 001837528| sanghvi.ki@husky.neu.edu |
| Vignesh Raghuraman| 001837157| raghuraman.v@husky.neu.edu |
| Karan Magdani | 001250476 | magdani.k@husky.neu.edu |

## Technology Stack
The application is developed using spring boot and uses rest controllers to acheive use case objectives.

## Build Instructions
Application is built using Maven.

## Deploy Instructions
Circle CI has been implemented to trigger builds on any commit to the branches tereby facilating latest code movement to AWS EC2 instance.
The war file generated is stored in Amazon S3 bucket instance which is then moved using AWS Codedeploy.

## Running Tests.


## CI/CD
CI- Circle CI is used for Continuous Implementation.
CD- AWS Codedeploy is used to facilitate Continuous Deployment.
