#!/bin/sh

# This example uses Spring and Spring aop to monitor a class. See the spring_aop project for source code and how the app is configured.
# The spring_aop jar has all needed dependent jars bundled inside it.  It also
# has a main executable class defined: org.automon.spring_aop.SpringMain.  See the spring_aop project and associated pom.xml for
# more info.
# The following executes the spring_aop jar file and enables Automon system output

# Source the shared script to define the function
. ./set_versions.sh

# Call the function to set the variables
set_versions

# Execute the Java command using the environment variables
java   -Dorg.automon="sysout" \
       -jar ../spring_aop/target/spring_aop-${AUTOMON_VERSION}-shaded.jar
