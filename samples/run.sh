#!/bin/bash

CURRENT_DIRECTORY=`dirname $0`

grc -c conf.log java -javaagent:${CURRENT_DIRECTORY}/../agent/target/tyrion-agent-jar-with-dependencies.jar=output-file=${CURRENT_DIRECTORY}/simple-test-locks.log -cp ${CURRENT_DIRECTORY}/classes SimpleTest

unset CURRENT_DIRECTORY

