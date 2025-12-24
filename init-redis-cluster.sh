#!/bin/sh
echo "Waiting for Redis nodes to be ready..."
for i in 1 2 3; do
  until redis-cli -h redis-$i -p 6379 ping; do
    echo waiting for redis-$i...
    sleep 1
  done
done
echo "All nodes ready, creating cluster..."
yes yes | redis-cli --cluster create redis-1:6379 redis-2:6379 redis-3:6379 --cluster-replicas 0
echo "Cluster created successfully."
