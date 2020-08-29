#!/bin/bash
docker build --build-arg JAR_FILE=build/libs/*.jar -t docker.pkg.github.com/haswf/comp30022backenddev/eportfolio-server-docker .
echo "$DOCKER_TOKRN" | docker login https://docker.pkg.github.com -u haswf --password-stdin
docker_build_push
