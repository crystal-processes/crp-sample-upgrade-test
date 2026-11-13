#!/bin/bash
set -e

echo '--------------------------------------------------------------'
echo '--- Generate data for release-0.3.0                        ---'
echo '--- versioning with JPA variables                          ---'
#  docker-compose -f commons/docker-compose/postgres-docker-compose.yml up -d
  docker exec -it "$(docker ps | grep docker-compose-db | awk '{print $1}')" psql -U flowable -d postgres -c "DROP DATABASE IF EXISTS flowable"
  docker exec -it "$(docker ps | grep docker-compose-db | awk '{print $1}')" psql -U flowable -d postgres -c "CREATE DATABASE flowable"
# generate v 0.2.5 data
 ./mvnw --projects release-0.3.0 clean test -Dspring.profiles.active=generateData

 echo '--------------------------------------------------------------'
 echo '--- Testing upgrade from release-0.3.0 -> release-0.3.1    ---'
 echo '--- Migrate                                                ---'
# test process execution on the version 0.3.0
#  ./mvnw --projects release-0.3.1 clean test

# drop the test database
#  docker exec -it "$(docker ps | grep docker-compose-db | awk '{print $1}')" psql -U flowable -d postgres -c "DROP DATABASE flowable"
## shut down postgres database
#  docker-compose -f commons/docker-compose/postgres-docker-compose.yml down