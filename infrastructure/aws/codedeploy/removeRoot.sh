#!/bin/bash

set -e

sudo systemctl stop tomcat
sudo rm -rf /opt/tomcat/apache-tomcat-9.0.16/webapps/ROOT
sudo systemctl start tomcat