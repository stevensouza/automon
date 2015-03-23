#!/bin/sh

# The following executes the HelloWorld using load time weaving (ltw).  The aspectj javaagent is required.
# ltw is the most flexible way of using aspectj and is controled by passing an aspectj xml config line into the program.
# The example monitors a class using Jamon as opposed to sysout which is used in the other examples.  Monitoring with
# Yammer Metrics or JavaSimon could be done in a similar way. Note -Dorg.automon=jamon is not specified below.  Instead
# Automon recognizes that Jamon is available and so uses it.

java  -Dorg.aspectj.weaver.loadtime.configuration=file:config/hello-world-unwoven-aop.xml -javaagent:libs/aspectjweaver.jar -classpath libs/automon-1.0.jar:libs/helloworld_unwoven-1.0.jar:libs/helloworld_unwoven_jamon-1.0.jar:libs/jamon-2.81.jar org.automon.jamon.JamonHelloWorld
# to run the program in a loop for 100 times (allows time to look at automon jmx in jconsole)
# java  -Dorg.aspectj.weaver.loadtime.configuration=file:config/hello-world-unwoven-aop.xml -javaagent:libs/aspectjweaver.jar -classpath libs/automon-1.0.jar:libs/helloworld_unwoven-1.0.jar:libs/helloworld_unwoven_jamon-1.0.jar:libs/jamon-2.81.jar org.automon.jamon.JamonHelloWorld 1000
