#!/bin/sh

CURRENT_DIRECTORY=`dirname $0`

rm -rf ${CURRENT_DIRECTORY}/{classes,logs}
mkdir -p ${CURRENT_DIRECTORY}/{classes,logs}
javac -classpath ${CURRENT_DIRECTORY}/classes -d ${CURRENT_DIRECTORY}/classes ${CURRENT_DIRECTORY}/src/*.java

unset CURRENT_DIRECTORY
