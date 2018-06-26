#!/bin/bash
docker build -t docbuilder:v0.1 .
docker run -i -t --name docbuilder -p 8233:8233 docbuilder:v0.1 