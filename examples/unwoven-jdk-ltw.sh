#!/bin/sh

# The following executes the HelloWorld using load time weaving (ltw).  The aspectj javaagent is required.
# ltw is the most flexible way of using aspectj and is controled by passing an aspectj xml config line into the program.
#
# The example monitors jdk IO, and net classes.
java  -Dorg.automon=sysout -Dorg.aspectj.weaver.loadtime.configuration=file:config/unwoven-jdk-aop.xml -javaagent:libs/aspectjweaver.jar -classpath ../automon/target/automon-1.0.jar:../unwoven_jdk/target/unwoven_jdk-1.0.jar com.stevesouza.jdk.JdkHelloWorld README.md
