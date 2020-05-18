#!/bin/bash
path=$1
width=$2
height=$3
outdir=$4
filename=$5
ffmpeg -hide_banner -y -i $path -vcodec libx264 -acodec copy -s $width:$height -copyts -muxdelay 0 $outdir/$filename 
chmod 775 $outdir/$filename
