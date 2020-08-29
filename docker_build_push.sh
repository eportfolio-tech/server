#!/bin/bash
cat token.txt | docker login https://docker.pkg.github.com -u haswf --password-stdin
docker build --build-arg JAR_FILE=build/libs/*.jar -t docker.pkg.github.com/haswf/comp30022backenddev/eportfolio-server-docker .
docker push docker.pkg.github.com/haswf/comp30022backenddev/eportfolio-server-docker:latest
