#!/bin/bash
name="docbuilder"
port="8233"
mvn clean package
docker build -t docbuilder:v0.1 .
docker stop ${name}
docker rm ${name}
docker run -i -t --name ${name} -p ${port}:8233 docbuilder:v0.1