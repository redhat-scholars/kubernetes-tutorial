# syntax = docker/dockerfile:1.0-experimental

#
# This is the base dockerfile to be used with the BUILDKIT to build the 
# image that the .devcontainer docker image is based on
# 
FROM registry.access.redhat.com/ubi8/openjdk-11:latest

USER root

# add a reference to fedora repo to install packages not part of the
# ubi8 repos
COPY assets/fedora.repo /etc/yum.repos.d/fedora.repo

RUN microdnf install dnf \
# install a smattering of useful packages (some of which are used later in dockerfile such as wget, zsh, and git)
    && dnf install -y skopeo wget jq iputils vi procps git \
# Install packages from fedora (outside unsubscribed ubi8)
    && dnf -y install --enablerepo fedora zsh tree \
# Install necessary tools to run antora    
    && dnf -y install npm && npm i -g @antora/cli@2.3 @antora/site-generator-default@2.3 && npm rm --global npx && npm install --global npx && npm install --global gulp \
# Install yum so that docker can be installed in the container
    && dnf -y install yum && yum install -y yum-utils \
# install docker repo
    && yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo \
# install docker client
    && yum install -y docker-ce-cli  \
# make sure jboss user has rights to run docker
    && usermod -aG docker jboss  \
# cleanup packages and yum
    && yum remove -y yum-utils && yum clean all && dnf clean all && rm -r /var/cache/dnf

# install specific version of yq (2.4.1)
RUN wget https://github.com/mikefarah/yq/releases/download/2.4.1/yq_linux_amd64 -O /usr/bin/yq && \
    chmod +x /usr/bin/yq 

# install stern
RUN cd /usr/local/bin && \
    wget https://github.com/wercker/stern/releases/download/1.11.0/stern_linux_amd64 -O /usr/local/bin/stern && \
    chmod 755 /usr/local/bin/stern && \
# install hey
    wget https://mwh-demo-assets.s3-ap-southeast-2.amazonaws.com/hey_linux_amd64 -O /usr/local/bin/hey && \
    chmod 755 /usr/local/bin/hey

# overwrite existing oc with the absolute newest version of the openshift client
RUN curl -L https://mirror.openshift.com/pub/openshift-v4/clients/ocp/latest/openshift-client-linux.tar.gz | \
    tar -xvzf - -C /usr/local/bin/ oc && chmod 755 /usr/local/bin/oc && ln -s /usr/local/bin/oc /usr/local/bin/kubectl

USER jboss

# install and configure ohmyzsh for jboss user
RUN wget https://github.com/robbyrussell/oh-my-zsh/raw/master/tools/install.sh -O - | zsh

# needed for krew commands
ENV PATH="$HOME/.krew/bin:$PATH"

# install kube ctx and kube ns via krew
RUN ( set -x; cd "$(mktemp -d)" && \
  OS="$(uname | tr '[:upper:]' '[:lower:]')" && \
  ARCH="$(uname -m | sed -e 's/x86_64/amd64/' -e 's/\(arm\)\(64\)\?.*/\1\2/' -e 's/aarch64$/arm64/')" && \
  curl -fsSLO "https://github.com/kubernetes-sigs/krew/releases/latest/download/krew.tar.gz" && \
  tar zxvf krew.tar.gz && \
  KREW=./krew-"${OS}_${ARCH}" && \
  "$KREW" install krew ) &&\
  kubectl krew install ctx && kubectl krew install ns

# Subdirectory where local-config files should reside (matched to gitignore to ensure no secrets are checked in)
ENV CONFIG_SUBDIR "local-config"
ENV DEMO_HOME "/workspaces/kubernetes-tutorial/"
# Use VSCode with kubectl edit commands
ENV KUBE_EDITOR="code -w"

# this is done in the base image already (to support the demo shell images too), but for those that make
# local changes to .zshrc they should not have to rebuild the base
COPY assets/.zshrc.example $HOME/.zshrc