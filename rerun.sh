#!/bin/sh
mvn clean install -f ./etl
docker-compose build
./clean.sh
docker-compose -f docker-compose.yml -f kafka/compose-cp.yml up
