#!/usr/bin/env bash
set -euo pipefail

sdk_dir=$(cd "$(dirname "$0")/.." && pwd -P)
starter_dir=${DINGTALK_STARTER_DIR:?Set DINGTALK_STARTER_DIR to the starter checkout}
maven_repo=$(mktemp -d)
trap 'rm -rf "$maven_repo"' EXIT

echo "[sdk] build & install into isolated Maven repository (guards against stale local snapshots)"
cd "$sdk_dir"
./mvnw -B --no-transfer-progress -Dmaven.repo.local="$maven_repo" clean install

sdk_version=$(./mvnw -q -DforceStdout help:evaluate -Dexpression=project.version 2>&1 | grep -oE '[0-9a-zA-Z][^[:space:]]*SNAPSHOT|[0-9a-zA-Z][^[:space:]]*\.RELEASE|[0-9]+\.[0-9]+[^[:space:]]*' | tail -n 1)
sdk_pom="$maven_repo/io/github/easy4j/dingtalk-sdk-extension/$sdk_version/dingtalk-sdk-extension-$sdk_version.pom"
if [ ! -f "$sdk_pom" ]; then
  echo "ERROR: SDK did not install into isolated repo (sdk_version='$sdk_version'). Expected to find: $sdk_pom" >&2
  echo "--- Listing installed sdk dir tree ---"
  find "$maven_repo/io/github/easy4j/dingtalk-sdk-extension" -maxdepth 3 -type f 2>/dev/null || true
  exit 1
fi
echo "[sdk] installed $sdk_version -> $sdk_pom"

echo "[starter] resolve & test against freshly-installed SDK"
cd "$starter_dir"
./mvnw -B --no-transfer-progress -U -Dmaven.repo.local="$maven_repo" clean test -DskipTests=false

echo "[gate] source-pair verification OK: sdk=$sdk_version built then consumed by starter build"
