#!/bin/bash

CURRENT_DIRECTORY=`dirname $0`

java "-javaagent:${CURRENT_DIRECTORY}/../agent/target/tyrion-agent-jar-with-dependencies.jar=output-file=${CURRENT_DIRECTORY}/logs/simple-test-locks.log;enabled-at-startup=true" -cp ${CURRENT_DIRECTORY}/classes SimpleTest

unset CURRENT_DIRECTORY

