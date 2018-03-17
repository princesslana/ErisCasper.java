#!/usr/bin/env bash

set -e

openssl aes-256-cbc -K $encrypted_aa77755bd8a1_key -iv $encrypted_aa77755bd8a1_iv -in etc/codesigning.asc.enc -out etc/codesigning.asc -d
gpg --fast-import etc/codesigning.asc
