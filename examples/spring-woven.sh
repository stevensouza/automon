#!/bin/sh

# Compile-time weaving - The following executes the HelloWorld woven application which is written using spring.
# This means aspectj aspects were woven in at compile time and are part of the classes. aspectjrt.jar needs to be in the classpath
# as well as any needed spring classes.
# Source the shared script to define the function
. ./set_versions.sh

# Call the function to set the variables
set_versions

# Execute the Java command using the environment variables
java   -Dorg.automon=sysout  \
       -classpath ../automon/target/automon-${AUTOMON_VERSION}.jar:../spring_woven/target/spring_woven-${AUTOMON_VERSION}.jar:libs/aspectjrt.jar:libs/spring-core-4.1.1.RELEASE.jar:libs/spring-context-4.1.1.RELEASE.jar:libs/spring-beans-4.1.1.RELEASE.jar:libs/commons-logging-api-1.1.jar:libs/spring-expression-4.1.1.RELEASE.jar \
       com.stevesouza.spring.HelloWorld