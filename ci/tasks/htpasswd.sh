#!/usr/bin/env bash
set -eux

sudo apt-get -y install --no-install-recommends apache2-utils

htpasswd -Bbc ./password-site/Staticfile.auth $SITE_USER $SITE_PASSWORD 

more ./password-site/Staticfile.auth