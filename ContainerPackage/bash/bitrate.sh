#!/bin/bash

ffmpeg -i input.avi -b:v 64k -bufsize 64k output.avi