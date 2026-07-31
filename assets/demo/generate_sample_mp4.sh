#!/usr/bin/env bash
# generate_sample_mp4.sh
# Generate a 1-second sample MP4 video as a lightweight demo asset.
# Requires: ffmpeg (https://ffmpeg.org/)

set -euo pipefail

OUT_DIR="assets/demo"
OUT_FILE="$OUT_DIR/demo.mp4"

mkdir -p "$OUT_DIR"

echo "Generating 1-second sample MP4 at $OUT_FILE..."

# Create a 1-second solid-color video (320x240) encoded with H.264
# This command avoids requiring a local font and keeps the file tiny.
ffmpeg -y -f lavfi -i "color=c=0x1e293b:s=320x240:d=1" \
  -vf "format=yuv420p" \
  -movflags +faststart -c:v libx264 -preset veryfast -crf 28 \
  -t 1 "$OUT_FILE"

echo "Done. File created: $OUT_FILE"

echo "Note: To customize text overlay, install a font and modify the ffmpeg drawtext filter."
