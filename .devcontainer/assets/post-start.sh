#!/bin/bash

WORKSPACE_FOLDER=$1

rsync -a ${WORKSPACE_FOLDER}/.devcontainer/workspace-setup/ ${WORKSPACE_FOLDER}/.vscode/ --ignore-existing

${WORKSPACE_FOLDER}/.devcontainer/assets/copy-kube-config.sh