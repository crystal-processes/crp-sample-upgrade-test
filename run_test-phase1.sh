#!/bin/bash
set -e

echo '--------------------------------------------------------------'
echo '--- Testing upgrade from release-0.1.0 -> release-0.2.4    ---'
echo '--- versioning test example, with variables                ---'
# start postgres database to store version 0.1.0 data
  docker-compose -f commons/docker-compose/postgres-docker-compose.yml up -d
  docker exec -it "$(docker ps | grep docker-compose-db | awk '{print $1}')" psql -U flowable -d postgres -c "DROP DATABASE IF EXISTS flowable"
  docker exec -it "$(docker ps | grep docker-compose-db | awk '{print $1}')" psql -U flowable -d postgres -c "CREATE DATABASE flowable"
# generate v 0.1.0 data
 ./mvnw --projects release-0.1.0 clean test -Dspring.profiles.active=generateData
# test process execution on the version 0.2.0
 ./mvnw --projects release-0.2.0 clean test

 echo '--------------------------------------------------------------'
 echo '--- Testing upgrade from release-0.2.0 -> release-0.2.1    ---'
 echo '--- java serialization                                     ---'
# generate v 0.2.0 data
  ./mvnw --projects release-0.2.0 clean test -Dspring.profiles.active=generateData
 # test process execution on the version 0.2.0
  ./mvnw --projects release-0.2.1 clean test

 echo '--------------------------------------------------------------'
 echo '--- Testing upgrade from release-0.2.2 -> release-0.2.3    ---'
 echo '--- json serialization                                     ---'
# generate v 0.2.2 data
  ./mvnw --projects release-0.2.2 clean test -Dspring.profiles.active=generateData
 # test process execution on the version 0.2.0
  ./mvnw --projects release-0.2.3 clean test

 echo '--------------------------------------------------------------'
 echo '--- Testing upgrade from release-0.2.3 -> release-0.2.4    ---'
 echo '--- reporting                                              ---'
 # drop the test database !!! 0.2.0 variable serialization is not supported !!!
  docker exec -it "$(docker ps | grep docker-compose-db | awk '{print $1}')" psql -U flowable -d postgres -c "DROP DATABASE flowable"
  docker exec -it "$(docker ps | grep docker-compose-db | awk '{print $1}')" psql -U flowable -d postgres -c "CREATE DATABASE flowable"
# generate v 0.2.1 data
  ./mvnw --projects release-0.2.1 clean test -Dspring.profiles.active=generateData
# generate v 0.2.2 data
  ./mvnw --projects release-0.2.2 clean test -Dspring.profiles.active=generateData
# generate v 0.2.3 data
  ./mvnw --projects release-0.2.3 clean test -Dspring.profiles.active=generateData
 # test process execution on the version 0.2.0
  ./mvnw --projects release-0.2.4 clean test

 # drop the test database
  docker exec -it "$(docker ps | grep docker-compose-db | awk '{print $1}')" psql -U flowable -d postgres -c "DROP DATABASE flowable"
 # shut down postgres database
  docker-compose -f commons/docker-compose/postgres-docker-compose.yml down