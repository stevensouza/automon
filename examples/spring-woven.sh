#!/bin/sh

# Compile-time weaving - The following executes the HelloWorld woven application which is written using spring.
# This means aspectj aspects were woven in at compile time and are part of the classes. aspectjrt.jar needs to be in the classpath
# as well as any needed spring classes.
# Source the shared script to define the function
. ./set_versions.sh

# Call the function to set the variables
set_versions

# Execute the Java command using the environment variables
java -Dorg.automon=sysout -jar ../spring_woven/target/spring_woven-${AUTOMON_VERSION}-shaded.jar