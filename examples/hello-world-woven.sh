#!/bin/sh

# Compile-time weaving - The following executes the HelloWorld woven application.  This means aspectj aspects were woven in at compile time and
# are part of the classes. aspectjrt.jar needs to be in the classpath.
java   -Dorg.automon=sysout -classpath libs/automon-1.0.jar:libs/helloworld_woven-1.0.jar:libs/aspectjrt.jar org.automon.helloworld.HelloWorld

# to run the program in a loop for 100 times (allows time to look at automon jmx in jconsole)
# java   -Dorg.automon=sysout -classpath libs/automon-1.0.jar:libs/helloworld_woven-1.0.jar:libs/aspectjrt.jar org.automon.helloworld.HelloWorld 100
