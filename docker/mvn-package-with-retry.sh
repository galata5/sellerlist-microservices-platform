#!/bin/sh
set -eu

module="${1:?module name is required}"

attempt=1
while [ "$attempt" -le 3 ]; do
  if mvn -B -ntp -DskipTests -pl "$module" -am package; then
    exit 0
  fi

  echo "Maven build failed for ${module} on attempt ${attempt}, retrying..." >&2
  rm -rf /root/.m2/repository/org/springframework/boot \
         /root/.m2/repository/org/apache/maven \
         /root/.m2/repository/com/fasterxml \
         /root/.m2/repository/net/java/dev/jna || true
  attempt=$((attempt + 1))
  sleep 5
done

echo "Maven build failed for ${module} after 3 attempts" >&2
exit 1
