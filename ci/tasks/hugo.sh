#!/usr/bin/env bash
set -e
tar -xf hugo/*.tar.gz -C hugo
HUGO=$(find hugo -type f -name *_linux_amd64)
$HUGO version

cd documentation-repo
../$HUGO -Ds '' -t oewb

mv public ../compiled-site
mv Staticfile ../compiled-site/
mv manifest.yml ../compiled-site/
