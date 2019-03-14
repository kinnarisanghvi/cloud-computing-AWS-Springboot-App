#!/bin/bash

set -e

echo "bucketName=csye6225-spring2019-magdanik.me.csye6225.com" >> ~/.bashrc
sudo iptables -A PREROUTING -t nat -i eth0 -p tcp --dport 80 -j REDIRECT --to-port 8080
sudo systemctl start tomcat
