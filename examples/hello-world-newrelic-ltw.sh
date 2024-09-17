#!/bin/sh

# The following executes the HelloWorld using load time weaving (ltw).  The aspectj javaagent is required.
# ltw is the most flexible way of using aspectj and is controled by passing an aspectj xml config line into the program.
# The example monitors a class using NewRelicas opposed to sysout which is used in the other examples.  Monitoring with
# StatsD or JavaSimon could be done in a similar way. Note -Dorg.automon=newrelic is not specified below.  Instead
# Automon recognizes that Metrics is in the class path and so uses it.

# Source the shared script to define the function
. ./set_versions.sh

# Call the function to set the variables
set_versions

num_loops="$1"
if [ -z "$num_loops" ]; then
    echo "Default num_loops of 5 will be used"
    num_loops=5
fi

# Execute the Java command using the environment variables and the provided aop.xml file
java   -Dorg.aspectj.weaver.loadtime.configuration=file:config/hello-world-unwoven-aop.xml \
       -javaagent:libs/aspectjweaver-${ASPECTJ_VERSION}.jar \
       -classpath ../automon/target/automon-${AUTOMON_VERSION}.jar:../helloworld_unwoven/target/helloworld_unwoven-${AUTOMON_VERSION}-shaded.jar:libs/newrelic-api-3.29.0.jar \
       com.stevesouza.helloworld.HelloWorld ${num_loops}