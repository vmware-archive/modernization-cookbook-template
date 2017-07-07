#!/usr/bin/env bash
set -eu

if [[ -n "$SITE_USER" ]]; then
  echo "Site protection required - generating password file"
  sudo apt-get -y install --no-install-recommends apache2-utils
  htpasswd -bc ./password-site/Staticfile.auth $SITE_USER $SITE_PASSWORD 
fi