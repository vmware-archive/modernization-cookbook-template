# generate lunr search index
npm install lunr-hugo
mkdir -p static/json
node_modules/lunr-hugo/bin/index.js -i "content/**/*.md" -o static/json/search.json -l toml
