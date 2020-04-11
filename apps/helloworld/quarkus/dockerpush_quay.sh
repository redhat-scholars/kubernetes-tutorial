#!/bin/bash

# use docker images | grep quarkus to get the image ID for $1

docker login quay.io

docker tag $1 quay.io/rhdevelopers/quarkus-demo:v2

docker push quay.io/rhdevelopers/quarkus-demo:v2

echo 'quay.io marks repositories as private by default'
echo 'to update https://screencast.com/t/uAooYnghlW'