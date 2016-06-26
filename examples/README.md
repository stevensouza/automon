Automon Example Programs
========================

This directory contains sample programs (*.sh) that show how programs can easily be monitored/profiled/instrumented with Automon.
Before running the programs you must generate the needed jar files by running the following from the automon root directory:
mvn clean package

Some of the programs use Load Time Weaving (LTW) whereas others were woven at build time (BTW).  Weaving combines Automon's
monitoring aspects with any Java classes that you want to monitor.  LTW is the most flexible.  It adds monitoring capabilities to your
classes at runtime when they are loaded by the class loader.

See [helloworld_woven](https://github.com/stevensouza/automon/tree/master/helloworld_woven) and
[spring_woven](https://github.com/stevensouza/automon/tree/master/spring_woven) pom files for how to configure a program
for Build Time Weaving (BTW).  Examples of using LTW are in this 'examples' directory.

Note you can perform both BTW and LTW on existing jars, whether you own the code in them or not.

Load Time Weaving (LTW) Invocation
-----------------------------------

Here is a short video that shows how to monitor your code using Automon with LTW: [Automon demo](http://youtu.be/RdR0EdezS74)

The following is an example of invoking a program with LTW:

**java -Dorg.automon=sysout -Dorg.aspectj.weaver.loadtime.configuration=file:config/ajc-aop.xml -javaagent:libs/aspectjweaver.jar -classpath libs/automon-1.0.jar**:libs/playground-1.0.jar com.stevesouza.automon.annotations.AnnotationTester

* **-Dorg.automon=sysout** - Specifies what implementation should be used to monitor your program. In this case we are simply
printing all method invocations with System.out.println(..).  Should you specify one of the other monitoring options such as jamon, javasimon, metrics
make sure you include those jars in the classpath too. If this parameter is not specified then Automon will look in the classpath for any
default implementations (as of 1.0 that is Jamon, JavaSimon, Yammer Metrics) and use the first one it finds or disable Automon if
none are found.
* **-Dorg.aspectj.weaver.loadtime.configuration=file:config/ajc-aop.xml** - Standard AspectJ syntax for configuring the weaver. For
Automon you will put your 'pointcuts' here that specify what parts of your code you would like to monitor.  See the
[/config](https://github.com/stevensouza/automon/tree/master/examples/config) directory for a few examples.
Note org.automon=sysout, can also be put in the comments section of this file.
* **-javaagent:libs/aspectjweaver.jar** - Standard way of specifying for AspectJ to use LTW.
* **-classpath libs/automon-1.0.jar:libs/playground-1.0.jar** - In addition to whatever jars are needed to run your
program you should include automon-{version}.jar.  This small jar is the bridge between AspectJ and the library you chose to
monitor your program (for example JAMon, JavaSimon, Yammer Metrics).
* **com.stevesouza.automon.annotations.AnnotationTester** - Standard java indicating the class you would like to run.

See the various scripts in this directory to see how to use your favorite monitoring api with Automon (Examples: hello-world-unwoven-statsd-ltw.sh, hello-world-unwoven-metrics-ltw.sh, hello-world-unwoven-jamon-ltw.sh,...)

Build Time Weaving (BTW) Invocation
-----------------------------------
Build Time Weaving refers to when you have the weaver instrument your code at compile time using the ajc compiler
instead of the javac compiler.  This approach is static and so not as flexible as LTW.

The following is an example of running a program that was compiled using BTW:

**java -Dorg.automon=sysout -classpath libs/automon-1.0.jar:libs/aspectjrt.jar**:libs/helloworld_woven-1.0.jar com.stevesouza.helloworld.HelloWorld

There are two differences between running a program with BTW vs LTW.

* **-classpath ...:libs/aspectjrt.jar** - apectjrt.jar is put in the classpath for BTW, and -javaagent:libs/aspectjweaver.jar is removed from the command line
* **-Dorg.aspectj.weaver.loadtime.configuration=file:config/ajc-aop.xml** - This is removed for BTW as the specified pointcuts to use
have already been incorporated into the class files.

That's it! Despite the syntactic differences between BTW and LTW, your resulting woven program is identical.

Resources
---------
For more information on AspectJ refer to the following links:

* [AspectJ Documentation](http://eclipse.org/aspectj/doc/released/progguide/index.html).
* The most important part to learn is the AspectJ pointcut language which allows you to select what code you would like
to monitor. I highly recommend [AspectJ in Action](http://www.amazon.com/AspectJ-Action-Enterprise-Spring-Applications/dp/1933988053/ref=sr_1_1?ie=UTF8&qid=1426500440&sr=8-1&keywords=aspectj+in+action).
  Here is an online chapter on [pointcuts](https://www.java.net/today/2003/12/26/ch3AspectJSyntaxBasics.pdf) from a previous version of the book.
