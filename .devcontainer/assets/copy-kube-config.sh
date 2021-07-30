#!/bin/bash -i

# set -euo pipefail

# Copies localhost's ~/.kube/config file into the container and swap out localhost
# for host.docker.internal whenever a new shell starts to keep them in sync.
if [ "$SYNC_LOCALHOST_KUBECONFIG" = "true" ] && [ -d "/usr/local/share/kube-localhost" ]; then
    mkdir -p $HOME/.kube
    cp -r /usr/local/share/kube-localhost/* $HOME/.kube
    # chown -R $(id -u) $HOME/.kube
    sed -i -e "s/localhost/host.docker.internal/g" $HOME/.kube/config
    sed -i -e "s/127.0.0.1/host.docker.internal/g" $HOME/.kube/config

    # If .minikube was mounted, set up client cert/key
    if [ -d "/usr/local/share/minikube-localhost" ]; then
        # chown -R $(id -u) $HOME/.minikube
        mkdir -p $HOME/.minikube
        cp -r /usr/local/share/minikube-localhost/ca.crt $HOME/.minikube
        # Location varies between versions of minikube
        if [ -f "/usr/local/share/minikube-localhost/client.crt" ]; then
            cp -r /usr/local/share/minikube-localhost/client.crt $HOME/.minikube
            cp -r /usr/local/share/minikube-localhost/client.key $HOME/.minikube
        elif [ -f "/usr/local/share/minikube-localhost/profiles/${SYNC_MINIKUBE_PROFILE}/client.crt" ]; then
            cp -r /usr/local/share/minikube-localhost/profiles/${SYNC_MINIKUBE_PROFILE}/client.crt $HOME/.minikube
            cp -r /usr/local/share/minikube-localhost/profiles/${SYNC_MINIKUBE_PROFILE}/client.key $HOME/.minikube
        fi
        # chown -R $(id -u) $HOME/.minikube

        # Point .kube/config to the correct locaiton of the certs
        sed -i -r "s|(\s*certificate-authority:\s).*|\\1$HOME\/.minikube\/ca.crt|g" $HOME/.kube/config
        sed -i -r "s|(\s*client-certificate:\s).*|\\1$HOME\/.minikube\/client.crt|g" $HOME/.kube/config
        sed -i -r "s|(\s*client-key:\s).*|\\1$HOME\/.minikube\/client.key|g" $HOME/.kube/config

        # sed -i -r "s|(\s*client-certificate:\s).*|\\1$HOME\/.minikube\/profiles\/$SYNC_MINIKUBE_PROFILE\/client.crt|g" $HOME/.kube/config
        # sed -i -r "s|(\s*client-key:\s).*|\\1$HOME\/.minikube\/profiles\/$SYNC_MINIKUBE_PROFILE\/client.key|g" $HOME/.kube/config
    fi
fi