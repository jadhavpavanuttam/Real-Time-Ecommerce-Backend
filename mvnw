#!/bin/sh
set -e
MVN_VERSION=3.9.11
BASE="$HOME/.m2/wrapper/dists/apache-maven-$MVN_VERSION-bin"
MAVEN_HOME="$BASE/apache-maven-$MVN_VERSION"
if [ ! -x "$MAVEN_HOME/bin/mvn" ]; then
  mkdir -p "$BASE"
  echo "Maven $MVN_VERSION is not bundled; install Maven or allow this script to download it."
  if command -v mvn >/dev/null 2>&1; then exec mvn "$@"; fi
  curl -fsSL "https://archive.apache.org/dist/maven/maven-3/$MVN_VERSION/binaries/apache-maven-$MVN_VERSION-bin.tar.gz" -o /tmp/maven.tgz
  tar -xzf /tmp/maven.tgz -C "$BASE"
fi
exec "$MAVEN_HOME/bin/mvn" "$@"
