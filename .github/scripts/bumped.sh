#!/bin/bash

# Directory of this script
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" &>/dev/null && pwd)"

CURRENT_VERSION=$($DIR/get-version.sh)

if [[ "${BEFORE_VERSION}" == "${CURRENT_VERSION}" ]]; then
    echo "false"
else
    echo "true"
fi