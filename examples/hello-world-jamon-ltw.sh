#!/bin/sh

# The following executes the HelloWorld using load time weaving (ltw).  The aspectj javaagent is required.
# ltw is the most flexible way of using aspectj and is controled by passing an aspectj xml config line into the program.
# The example monitors a class using Jamon as opposed to sysout which is used in the other examples.  Monitoring with
# Yammer Metrics or JavaSimon could be done in a similar way. Note -Dorg.automon=jamon is not specified below.  Instead
# Automon recognizes that Jamon is available and so uses it.

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
       -classpath ../automon/target/automon-${AUTOMON_VERSION}.jar:../helloworld_unwoven/target/helloworld_unwoven-${AUTOMON_VERSION}.jar:../helloworld_unwoven_jamon/target/helloworld_unwoven_jamon-${AUTOMON_VERSION}.jar:libs/jamon-2.81.jar \
       com.stevesouza.jamon.JamonHelloWorld ${num_loops}