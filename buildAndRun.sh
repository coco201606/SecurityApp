#!/bin/sh
mvn clean package && docker build -t ohashi/SecurityApp .
docker rm -f SecurityApp || true && docker run -d -p 9080:9080 -p 9443:9443 --name SecurityApp ohashi/SecurityApp