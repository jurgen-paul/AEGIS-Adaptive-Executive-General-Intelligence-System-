# Demo video (placeholder + generator)

This directory contains a small helper to generate a 1-second sample MP4 demo video for the repository.

Why this exists
- A tiny generated MP4 is useful as a placeholder/demo asset.
- The repository cannot store large binary assets in the repo history without intention; this helper lets you generate the sample locally or in CI.

Files:
- generate_sample_mp4.sh — script to produce assets/demo/demo.mp4 using ffmpeg.
- DEMO_PLACEHOLDER.txt — explains where the generated file will be and how to replace it.

To generate the sample locally (requires ffmpeg):

```bash
# make the script executable once
chmod +x assets/demo/generate_sample_mp4.sh
# run the script
./assets/demo/generate_sample_mp4.sh
```
