#!/usr/bin/env bash
set -eux
tar -xf hugo/*.tar.gz -C hugo
HUGO=$(find hugo -type f -name hugo)
$HUGO version

cd documentation-repo
../$HUGO -Ds ''

mv public ../compiled-site
mv Staticfile ../compiled-site/
mv manifest.yml ../compiled-site/

if [ -f ../password-site/Staticfile.auth ]; then
   echo "Password file found - copying!"
   cp ../password-site/Staticfile.auth ../compiled-site/
fi
