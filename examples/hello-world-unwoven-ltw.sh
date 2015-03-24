#!/bin/sh

# The following executes the HelloWorld using load time weaving (ltw).  The aspectj javaagent is required.
# ltw is the most flexible way of using aspectj and is controled by passing an aspectj xml config line into the program.
java   -Dorg.automon=sysout -Dorg.aspectj.weaver.loadtime.configuration=file:config/hello-world-unwoven-aop.xml -javaagent:libs/aspectjweaver.jar -classpath ../automon/target/automon-1.0.jar:../helloworld_unwoven/target/helloworld_unwoven-1.0.jar com.stevesouza.helloworld.HelloWorld
# to run the program in a loop for 100 times (allows time to look at automon jmx in jconsole)
#java   -Dorg.automon=sysout -Dorg.aspectj.weaver.loadtime.configuration=file:config/hello-world-unwoven-aop.xml -javaagent:libs/aspectjweaver.jar -classpath ../automon/target/automon-1.0.jar:../helloworld_unwoven/target/helloworld_unwoven-1.0.jar com.stevesouza.helloworld.HelloWorld 100
