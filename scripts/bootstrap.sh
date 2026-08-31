#!/usr/bin/env bash
set -euo pipefail

# Simple bootstrap script for building all modules locally
# Usage: ./scripts/bootstrap.sh

echo "Running multi-module Maven build (parallel)..."
mvn -T 1C -B clean install

echo "Done. To run a single module: mvn -pl <module> -am clean install"