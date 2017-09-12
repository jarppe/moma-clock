#!/usr/bin/env zsh

set -e

rm -fr docs target/prod
mkdir -p docs/js
lein cljsbuild once prod
cp resources/public/* docs
cp target/prod/resources/public/js/main.js docs/js
