#!/usr/bin/env bash
set -eux
tar -xf hugo/*.tar.gz -C hugo
HUGO=$(find hugo -type f -name hugo)
$HUGO version

cd documentation-repo
../$HUGO -Ds ''

mv public ../compile-site
mv Staticfile ../compile-site/
mv manifest.yml ../compile-site/
