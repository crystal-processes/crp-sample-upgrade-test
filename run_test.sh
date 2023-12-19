#!/bin/bash
set -e

echo '--- Testing upgrade from release-0.1.0 -> release-0.2.0'
# start postgres database to store version 0.1.0 data
docker-compose -f commons/docker-compose/postgres-docker-compose.yml up -d
# generate v 0.1.0 data
 ./mvnw --projects release-0.1.0 clean test -Dspring.profiles.active=generateData
# test process execution on the version 0.2.0
 ./mvnw --projects release-0.2.0 clean test

 echo '--- Testing upgrade from release-0.2.0 -> release-0.2.1'
 # generate v 0.2.0 data
  ./mvnw --projects release-0.2.0 clean test -Dspring.profiles.active=generateData
 # test process execution on the version 0.2.0
  ./mvnw --projects release-0.2.1 clean test


 # drop the test database
  docker exec -it "$(docker ps | grep docker-compose-db | awk '{print $1}')" psql -U flowable -d postgres -c "DROP DATABASE flowable"
 # shut down postgres database
  docker-compose -f commons/docker-compose/postgres-docker-compose.yml down