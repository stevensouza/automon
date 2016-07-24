#!/bin/sh

# Compile-time weaving - The following executes the HelloWorld woven application.  This means aspectj aspects were woven in at compile time and
# are part of the classes. aspectjrt.jar needs to be in the classpath.
java   -Dorg.automon=sysout -classpath ../automon/target/automon-1.0.3-SNAPSHOT.jar:../helloworld_woven/target/helloworld_woven-1.0.3-SNAPSHOT.jar:libs/aspectjrt.jar com.stevesouza.helloworld.HelloWorld

# to run the program in a loop for 100 times (allows time to look at automon jmx in jconsole)
# java   -Dorg.automon=sysout -classpath ../automon/target/automon-1.0.3-SNAPSHOT.jar:../helloworld_woven/target/helloworld_woven-1.0.3-SNAPSHOT.jar:libs/aspectjrt.jar com.stevesouza.helloworld.HelloWorld 100
