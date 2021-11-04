@echo off
call mvn clean package
call docker build -t ohashi/SecurityApp .
call docker rm -f SecurityApp
call docker run -d -p 9080:9080 -p 9443:9443 --name SecurityApp ohashi/SecurityApp