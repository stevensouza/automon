#!/bin/sh

# The following executes the HelloWorld using load time weaving (ltw).  The aspectj javaagent is required.
# ltw is the most flexible way of using aspectj and is controled by passing an aspectj xml config line into the program.
# The example monitors a class using Jamon as opposed to sysout which is used in the other examples.  Monitoring with
# Yammer Metrics or JavaSimon could be done in a similar way. Note -Dorg.automon=jamon is not specified below.  Instead
# Automon recognizes that Jamon is available and so uses it.

java  -Dorg.aspectj.weaver.loadtime.configuration=file:config/hello-world-unwoven-aop.xml -javaagent:libs/aspectjweaver.jar -classpath ../automon/target/automon-1.0.2-SNAPSHOT.jar:../helloworld_unwoven/target/helloworld_unwoven-1.0.2-SNAPSHOT.jar:../helloworld_unwoven_jamon/target/helloworld_unwoven_jamon-1.0.2-SNAPSHOT.jar:libs/jamon-2.81.jar com.stevesouza.jamon.JamonHelloWorld
# to run the program in a loop for 1000 times (allows time to look at automon jmx in jconsole)
#java  -Dorg.aspectj.weaver.loadtime.configuration=file:config/hello-world-unwoven-aop.xml -javaagent:libs/aspectjweaver.jar -classpath ../automon/target/automon-1.0.2-SNAPSHOT.jar:../helloworld_unwoven/target/helloworld_unwoven-1.0.2-SNAPSHOT.jar:../helloworld_unwoven_jamon/target/helloworld_unwoven_jamon-1.0.2-SNAPSHOT.jar:libs/jamon-2.81.jar com.stevesouza.jamon.JamonHelloWorld 1000
