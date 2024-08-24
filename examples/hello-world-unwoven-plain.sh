#!/bin/sh

# The following executes the HelloWorld unwoven application.  It doesn't use aspectJ.  This is to allow a user to
# see the difference between a plain inocation of a program and an aspectj invocation of the same program.
# Source the shared script to define the function
. ./set_versions.sh

# Call the function to set the variables
set_versions

num_loops="$1"
if [ -z "$num_loops" ]; then
    echo "Default num_loops of 5 will be used"
    num_loops=5
fi

# Execute the Java command
java   -classpath ../helloworld_unwoven/target/helloworld_unwoven-${AUTOMON_VERSION}.jar \
       com.stevesouza.helloworld.HelloWorld ${num_loops}