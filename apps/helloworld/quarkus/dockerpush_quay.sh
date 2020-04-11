#!/bin/bash

# use docker images | grep quarkus to get the image ID for $1

docker login quay.io

docker tag $1 quay.io/rhdevelopers/myboot:v2

docker push quay.io/rhdevelopers/myboot:v2

echo 'quay.io marks repositories as private by default'
echo 'to update https://screencast.com/t/uAooYnghlW'