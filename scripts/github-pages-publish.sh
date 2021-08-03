#!/bin/bash

set -euo pipefail

declare SITE=${1:-github-pages-stage.yml}
declare REPO=${2:-$(git remote get-url origin)}
declare BRANCH="gh-pages"

echo "Removing old publish directory"
if [[ -d $DEMO_HOME/gh-publish ]]; then
    rm -rf $DEMO_HOME/gh-publish 
fi

echo "Removing antora cache directory"
if [[ -d $DEMO_HOME/.cache ]]; then
    rm -rf $DEMO_HOME/.cache 
fi

git clone -b ${BRANCH} ${REPO} $DEMO_HOME/gh-publish

echo "Generating the site documentation from ${SITE}"

antora generate --stacktrace $DEMO_HOME/${SITE} --to-dir $DEMO_HOME/gh-publish

echo "Pushing site to ${BRANCH} branch of ${REPO}"
cd $DEMO_HOME/gh-publish
git add --all .
git commit -m"Automated Publish" 
git push origin

echo "Site published successfully!"