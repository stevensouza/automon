#!/bin/sh

# Compile-time weaving - The following executes the HelloWorld woven application which is written using spring.
# This means aspectj aspects were woven in at compile time and are part of the classes. aspectjrt.jar needs to be in the classpath
# as well as any needed spring classes.
java   -Dorg.automon=sysout -classpath libs/automon-1.0.jar:libs/spring_woven-1.0.jar:libs/aspectjrt.jar:libs/spring-core-4.1.1.RELEASE.jar:libs/spring-context-4.1.1.RELEASE.jar:libs/spring-beans-4.1.1.RELEASE.jar:libs/commons-logging-api-1.1.jar:libs/spring-expression-4.1.1.RELEASE.jar org.automon.spring.HelloWorld

# to run the program in a loop for 100 times (allows time to look at automon jmx in jconsole)
# java   -Dorg.automon=sysout -classpath libs/automon-1.0.jar:libs/spring_woven-1.0.jar:libs/aspectjrt.jar:libs/spring-core-4.1.1.RELEASE.jar:libs/spring-context-4.1.1.RELEASE.jar:libs/spring-beans-4.1.1.RELEASE.jar:libs/commons-logging-api-1.1.jar:libs/spring-expression-4.1.1.RELEASE.jar org.automon.spring.HelloWorld 100
