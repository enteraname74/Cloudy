#!/usr/bin/env bash

set -euo pipefail

if [[ $# -ne 1 ]]; then
    echo "Usage: $0 <path-to-env-file>" >&2
    exit 1
fi

env_file="$1"

if [[ ! -f "$env_file" ]]; then
    echo "Environment file not found: $env_file" >&2
    exit 1
fi

script_dir="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"

set -a
# shellcheck disable=SC1090
source "$env_file"
set +a

exec "$script_dir/gradlew" :controller:run