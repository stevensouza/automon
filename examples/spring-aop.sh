#!/bin/sh

# This example uses Spring and Spring aop to monitor a class. See the spring_aop project for source code and how the app is configured.
# The spring_aop jar has all needed depedendent jars bundled inside it.  It also
# has a main executable class defined: org.automon.spring_aop.SpringMain.  See the spring_aop project and associated pom.xml for
# more info.
java -Dorg.automon="sysout" -jar ../spring_aop/target/spring_aop-1.0.3.jar
