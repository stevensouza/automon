#!/bin/sh
# The following executes a simple program and monitors any classes/methods with the automon Monitor annotation
# Load time weaving is performed so the aspectjweaver.jar is used.
# The program being monitored is in playground-1.0-SNAPSHOT.jar
java   -Dorg.automon=sysout -Dorg.aspectj.weaver.loadtime.configuration=file:ajc-aop.xml -javaagent:aspectjweaver.jar -classpath automon-1.0-SNAPSHOT.jar:playground-1.0-SNAPSHOT.jar com.stevesouza.automon.annotations.AnnotationTester
