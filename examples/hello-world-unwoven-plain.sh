#!/bin/sh

# The following executes the HelloWorld unwoven application.  It doesn't use aspectJ.  This is to allow a user to
# see the difference between a plain inocation of a program and an aspectj invocation of the same program.
java   -classpath ../helloworld_unwoven/target/helloworld_unwoven-1.0.3.jar com.stevesouza.helloworld.HelloWorld

# to run the program in a loop for 100 times (allows time to look at automon jmx in jconsole)
#java   -classpath ../helloworld_unwoven/target/helloworld_unwoven-1.0.3.jar com.stevesouza.helloworld.HelloWorld 100
