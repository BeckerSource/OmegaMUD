#!/bin/bash

# convert tabs to 4-spaces (expand) and remove trailing line spaces...
find ./src -type f -name '*.java' -exec bash -c ' \
    expand -t 4 "$0" > "$0.bak" && mv "$0.bak" "$0" && \
    sed -i "s/ *$//" $0 \
    ' {} \;
