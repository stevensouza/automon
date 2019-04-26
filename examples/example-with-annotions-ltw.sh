#!/bin/sh
# The following executes a simple program and monitors any classes/methods with the automon Monitor annotation
# Load time weaving is performed so the aspectjweaver.jar is used.
# The program being monitored is in playground-1.0.jar
java   -Dorg.automon=sysout -Dorg.aspectj.weaver.loadtime.configuration=file:config/ajc-aop.xml -javaagent:libs/aspectjweaver.jar -classpath ../automon/target/automon-1.0.3.jar:libs/playground-1.0.jar com.stevesouza.automon.annotations.AnnotationTester
