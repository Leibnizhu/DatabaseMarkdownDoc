#!/bin/bash
name="docbuilder"
port="8233"
mvn clean package
echo "Building new docbuilder docker image..."
docker build -t docbuilder:v0.1 .
echo "Stopping old docbuilder docker container..."
docker stop ${name}
echo "Deleting old docbuilder docker container..."
docker rm ${name}
echo "Starting new docbuilder docker container..."
docker run -d --restart always --name ${name} -p ${port}:8233 docbuilder:v0.1