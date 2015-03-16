Automon Example Programs
========================

This directory contains sample programs (*.sh) that show how programs can easily be monitored/profiled/instrumented with Automon.
Some of the programs use Load Time Weaving (LTW) whereas others were woven at build time.  Weaving combines Automon's
monitoring with classes that you want monitoring.  LTW is the most flexible.  It adds monitoring capabilities to your
classes when they are loaded by the class loader.

See [helloworld_woven](https://github.com/stevensouza/automon/tree/master/helloworld_woven) and
[spring_woven](https://github.com/stevensouza/automon/tree/master/spring_woven) pom files for how to conifgure a program
for Build Time Weaving (BTW).  LTW is more flexible as you defer the weaving until runtime. Examples of using LTW are in
this directory.  Note you can perform both BTW and LTW on existing jars.

Load Time Weaving (LTW) Invocation
-----------------------------------
The following is an example of invoking a program with LTW:
**java   -Dorg.automon=sysout -Dorg.aspectj.weaver.loadtime.configuration=file:config/ajc-aop.xml -javaagent:libs/aspectjweaver.jar -classpath libs/automon-1.0-SNAPSHOT.jar:libs/playground-1.0-SNAPSHOT.jar com.stevesouza.automon.annotations.AnnotationTester**

* -Dorg.automon=sysout - Specifies what implementation should be used to monitor your program. In this case we are simply
printing all invocations with System.out.println(..);.  Should you specify one of the other monitoring options such as jamon, javasimon, metrics
make sure you include those jars in the classpath too.
* -Dorg.aspectj.weaver.loadtime.configuration=file:config/ajc-aop.xml - Standard AspectJ syntax for configuring the weaver. For
Automon you will put your 'pointcuts' here that specify what parts of your code you would like to monitor.  See the
/config directory for a few examples
* -javaagent:libs/aspectjweaver.jar - Standard way of specifying for AspectJ to use LTW.
* -classpath libs/automon-1.0-SNAPSHOT.jar:libs/playground-1.0-SNAPSHOT.jar - In addition to whatever jars are needed to run your
program you should include automon-{version}.jar.  This small jar is the bridge between AspectJ and the library you chose to
monitor your program (for example JAMon, JavaSimon, Yammer Metrics).
* com.stevesouza.automon.annotations.AnnotationTester - Standard java indicating the class you would like to run.

Build Time Weaving (BTW) Invocation
-----------------------------------
Build Time Weaving refers to when you have already had the weaver instrument your code at compile time using the ajc compiler
instead of the javac compiler.  This approach is static and so not as flexible as LTW.

The following is an example of running a program that was compiled using BTW:
**java   -Dorg.automon=sysout -classpath libs/automon-1.0-SNAPSHOT.jar:libs/helloworld_woven-1.0-SNAPSHOT.jar:libs/aspectjrt.jar org.automon.helloworld.HelloWorld**

There are two differences between running a program with BTW vs LTW.

* -classpath ...:libs/aspectjrt.jar - apectjrt.jar is put in the classpath for BTW, and -javaagent:libs/aspectjweaver.jar is removed from the command line
* -Dorg.aspectj.weaver.loadtime.configuration=file:config/ajc-aop.xml - This is removed for BTW as the specified pointcuts to use
have already been incorporated into the class files.

That's it! Despite the syntactic differences between BTW and LTW your resulting woven program is identical.

Resources
---------
For more information on AspectJ refer to the following links:

* [AspectJ Documentation](http://eclipse.org/aspectj/doc/released/progguide/index.html).
* The most important part for you to learn for AspectJ is the pointcut language which allows you to select what code you would like
to monitor. I highly recommend [AspectJ in Action](http://www.amazon.com/AspectJ-Action-Enterprise-Spring-Applications/dp/1933988053/ref=sr_1_1?ie=UTF8&qid=1426500440&sr=8-1&keywords=aspectj+in+action).
  Here is an online chapter on pointcuts from a previous version of the book [Pointcuts](https://www.java.net/today/2003/12/26/ch3AspectJSyntaxBasics.pdf).
