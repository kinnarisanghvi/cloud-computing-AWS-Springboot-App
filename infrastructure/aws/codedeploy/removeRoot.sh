#!/bin/bash

echo "ec2 instance is running deploying code"
sudo systemctl stop tomcat
sudo rm -rf /opt/tomcat/apache-tomcat-9.0.17/webapps/ROOT
sudo systemctl start tomcat
