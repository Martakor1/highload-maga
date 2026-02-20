#!/bin/bash
set -e

echo "Waiting for master to be ready..."
until PGPASSWORD=shop_db pg_isready -h postgres -U shop_db; do
    sleep 2
done

echo "Master ready. Creating base backup..."
rm -rf /var/lib/postgresql/data/*

PGPASSWORD=replicapass pg_basebackup \
    -h postgres \
    -U replicator \
    -D /var/lib/postgresql/data \
    -Fp -Xs -P -R

chmod 700 /var/lib/postgresql/data
chown -R postgres:postgres /var/lib/postgresql/data

echo "Starting replica as postgres user..."
apk add --no-cache bash su-exec
exec su-exec postgres postgres -c hot_standby=on -c max_connections=200 -c shared_preload_libraries=pg_stat_statements -c pg_stat_statements.track=all