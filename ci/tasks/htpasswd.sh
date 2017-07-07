#!/usr/bin/env bash
set -eux

htpasswd $SITE_USER $SITE_PASSWORD > ./password-site/Staticfile.auth

more ./password-site/Staticfile.auth