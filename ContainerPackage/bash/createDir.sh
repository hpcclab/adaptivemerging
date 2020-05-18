#!/bin/bash

outdir=$1
absPath=$2
videoname=$3

mkdir $outdir
chmod 755 $outdir
cp $absPath/repositoryvideos/realVideo/$videoname/out.m3u8 $outdir
chmod 755 $outdir/out.m3u8
