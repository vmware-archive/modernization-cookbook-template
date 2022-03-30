#!/usr/bin/env bash
script_path=$(dirname $0)

main() {
  pushd $script_path
    docker build -t fedora-node-python .
  popd
}

main
