FROM quay.io/mhildenb/dev-demo-base:latest

USER root

# Install necessary tools to run antora
RUN dnf -y install npm && npm i -g @antora/cli@2.3 @antora/site-generator-default@2.3 && npm rm --global npx && npm install --global npx && npm install --global gulp

USER jboss

# Subdirectory where local-config files should reside (matched to gitignore to ensure no secrets are checked in)
ENV CONFIG_SUBDIR "local-config"
ENV DEMO_HOME "/workspaces/kubernetes-tutorial/"

# this is done in the base image already (to support the demo shell images too), but for those that make
# local changes to .zshrc they should not have to rebuild the base
COPY .zshrc.example $HOME/.zshrc