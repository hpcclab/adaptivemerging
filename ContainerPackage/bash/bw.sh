#!/bin/bash
path=$1
outdir=$2
filename=$3
ffmpeg -hide_banner -y -i $path -vf hue=s=0 -vcodec libx264 -acodec copy -copyts -muxdelay 0 $outdir/$filename 


chmod 775 $outdir/$filename

