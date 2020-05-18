#!/bin/bash
path=$1
framerate=$2
outdir=$3
filename=$4
ffmpeg -hide_banner -y -i $path -vcodec libx264 -r $framerate $outdir/$filename 
chmod 775 $outdir/$filename

