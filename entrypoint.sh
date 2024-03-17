#!/bin/bash

# Run Maven tests to generate the result folder
mvn clean test

# Start the Nginx server
nginx -g 'daemon off;'
