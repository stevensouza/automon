#!/bin/sh

# Compile-time weaving - The following executes the HelloWorld woven application.  This means aspectj aspects were woven in at compile time and
# are part of the classes. aspectjrt.jar needs to be in the classpath.
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
java -jar -Dorg.automon=sysout  ../helloworld_woven//target/helloworld_woven-${AUTOMON_VERSION}-shaded.jar ${num_loops}