#!/bin/sh
# The following executes a simple program and monitors any classes/methods with the automon Monitor annotation
# Load time weaving is performed so the aspectjweaver.jar is used.
# The program being monitored is in playground-1.0.jar

#!/bin/sh

# Source the shared script to define the function
. ./set_versions.sh

# Call the function to set the variables
set_versions

# Execute the Java command using the environment variables and the provided aop.xml file
java   -Dorg.automon=sysout  \
       -Dorg.aspectj.weaver.loadtime.configuration=file:config/ajc-aop.xml \
       -javaagent:libs/aspectjweaver-${ASPECTJ_VERSION}.jar \
       -classpath ../automon/target/automon-${AUTOMON_VERSION}.jar:libs/playground-1.0.jar \
       com.stevesouza.automon.annotations.AnnotationTester
