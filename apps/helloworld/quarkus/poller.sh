#!/bin/bash

while true
do 
  curl $(minikube -p 9steps ip):$(kubectl get svc myapp -ojsonpath="{.spec.ports[?(@.port==8080)].nodePort}")
  sleep .2;
done

