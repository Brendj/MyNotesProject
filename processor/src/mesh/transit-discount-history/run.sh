#!/bin/bash

dbConnection=jdbc:postgresql://localhost:5432/amoskow
dbUser=postgres
dbPass=postgres

java -jar transit-discount-history.jar \
 --DB_TYPE=postgres-prod \
 --spring.datasource.url=$dbConnection \
 --spring.datasource.username=$dbUser \
 --spring.datasource.password=$dbPass