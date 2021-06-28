FROM quay.io/mhildenb/dev-demo-base:latest

USER root

# Install necessary tools to run antora
RUN dnf -y install npm && npm i -g @antora/cli@2.3 @antora/site-generator-default@2.3 && npm rm --global npx && npm install --global npx && npm install --global gulp

RUN dnf -y install yum && yum install -y yum-utils && \
# install docker repo
    yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo && \
# install docker client
    yum install -y docker-ce-cli && \
# make sure jboss user has rights to run docker
    usermod -aG docker jboss && \
# cleanup packages and yum
    yum remove -y yum-utils && yum clean all && dnf clean all

# install specific version of yq (2.4.1)
RUN wget https://github.com/mikefarah/yq/releases/download/2.4.1/yq_linux_amd64 -O /usr/bin/yq && \
    chmod +x /usr/bin/yq 

USER jboss

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

# this is done in the base image already (to support the demo shell images too), but for those that make
# local changes to .zshrc they should not have to rebuild the base
COPY .zshrc.example $HOME/.zshrc