#!/bin/sh

# The following executes the HelloWorld using load time weaving (ltw).  The aspectj javaagent is required.
# ltw is the most flexible way of using aspectj and is controled by passing an aspectj xml config line into the program.
#
# The example monitors jdk IO, and net classes.
# Source the shared script to define the function
. ./set_versions.sh

# Call the function to set the variables
set_versions

# Execute the Java command using the environment variables and the provided aop.xml file
java   -Dorg.automon=sysout  \
       -Dlog4j.configurationFile=config/log4j2.xml \
       -Dorg.aspectj.weaver.loadtime.configuration=file:config/unwoven-jdk-aop.xml \
       -javaagent:libs/aspectjweaver-${ASPECTJ_VERSION}.jar \
       -classpath ../automon/target/automon-${AUTOMON_VERSION}.jar:../unwoven_jdk/target/unwoven_jdk-${AUTOMON_VERSION}-shaded.jar \
       com.stevesouza.jdk.JdkHelloWorld README.md

