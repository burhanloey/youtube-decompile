#!/usr/bin/env bash

lein do clean, cljsbuild once min

cp -R resources/public/* docs/

echo "Copied to docs/ folder."
