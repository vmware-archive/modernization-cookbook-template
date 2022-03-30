#!/usr/bin/env bash
set -eo pipefail

main() {
  content_dir="$(dirname $0)/../content"
  pngs=$(find "$content_dir" -name *.png)
  initial_size=$(du -sh "${content_dir}")

  find "$content_dir" -name *.png | while read png_file
  do
    pngcrush "${png_file}" "${png_file}.tmp"
    mv "${png_file}.tmp" "${png_file}"
  done

  final_size=$(du -sh "${content_dir}")

  echo "Initial size: ${initial_size}"
  echo "Final size: ${final_size}"
}

main