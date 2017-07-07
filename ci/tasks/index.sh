#!/bin/bash

set -e -x

npm install -g lunr-hugo
mv ./compiled-site/public ./indexed-site
mv ./compiled-site/static ./indexed-site
mv ./compiled-site/Staticfile ./indexed-site/
mv ./compiled-site/manifest.yml ./indexed-site/


mkdir -p ./indexed-site/static/json
lunr-hugo -i "./compiled-site/content/**/*.md" -o ./indexed-site/static/json/search.json -l toml
