#!/bin/sh

CURRENT_DIRECTORY=`dirname $0`

javac -classpath classes -d classes ${CURRENT_DIRECTORY}/src/*.java

unset CURRENT_DIRECTORY
