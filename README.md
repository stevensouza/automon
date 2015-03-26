# Automon
Automon combines the power of AOP (AspectJ) with monitoring tools (JAMon, JavaSimon, Yammer Metrics, ...) or logging tools
(perf4j, log4j, sl4j, ...) that you already use to declaratively monitor the following:

* Your Java code,
* The JDK,
* Any jars used by your application

Automon is typically used to track method invocation time, and exception counts. It is very easy to set-up and you should
be able to start monitoring your code within minutes.

It is important to note that Automon is complimentary to monitoring and logging tools. Automon performs no monitoring on its own.
It serves as a bridge between AspectJ (AspectJ defines 'what to monitor') and monitoring and logging tools (which define 'how to monitor').
You can also easily provide your own monitoring tool by implementing the simple [OpenMon](https://github.com/stevensouza/automon/blob/master/automon/src/main/java/org/automon/implementations/OpenMon.java)
interface.

The following diagram shows an AspectJ pointcut that will monitor all methods (any return type, any number of arguments)
 in the 'com.mycompany' package as well as its subpackages (..) using your monitoring tool of choice.
![Automon](https://github.com/stevensouza/automon/blob/master/docs/automon_bridge.png)

Automon requires jdk 1.5 or higher.

And finally, Automon can be dynamically enabled/disabled via the Automon MXBean (JMX).

AspectJ Weaving
-----------------------------------
AspectJ 'weaves' Automon monitoring code into your classes.  This can be done...

* at runtime with the Load Time Weaver (LTW)
* at build time with the Build Time Weaver (BTW).

Both approaches replace the original class file with one that has monitoring added to it.  Regardless of whether LTW, or BTW is used the
 generated class files are identical.  LTW is more flexible as it lets you use the powerful AspectJ pointcut language at
 runtime to specify what classes you want to monitor.

Here is a short video that shows how to monitor your code using Automon with LTW: [Automon demo](http://youtu.be/RdR0EdezS74)

Getting Started
-----------------------------------
The quickest way to get started with Automon is to download this distribution, and go to the [examples](https://github.com/stevensouza/automon/tree/master/examples)
directory and run the sample programs (*.sh).  There are more directions on running the examples in the 'examples' directories README file.

**Running a program** with Automon is easy.  You simply...

* Put automon-{version}.jar in your classpath,
* And make either aspectjweaver.jar (LTW), or aspectjrt.jar (BTW) available.

The [examples](https://github.com/stevensouza/automon/tree/master/examples) directory
shows how to invoke your programs using both LTW, and BTW.

**Load Time Weaving** (LTW) also involves providing an ajc-aop.xml config file.  Review [config files](https://github.com/stevensouza/automon/tree/master/examples/config)
for more information on them.

**Build Time Weaving** (BTW) - And finally if you want to use Build Time Weaving in your maven build process refer to these BTW sample projects:

* [helloworld_woven](https://github.com/stevensouza/automon/tree/master/helloworld_woven) - A simple project that
has a dependency on Automon and a simple jar that contains a HelloWorld application.  The output of this project is a jar
  that contains AspectJ BTW woven code.
* [spring_woven](https://github.com/stevensouza/automon/tree/master/spring_woven) - This project shows how you can weave a Spring
project at build time.
* [helloworld_unwoven_jamon](https://github.com/stevensouza/automon/tree/master/helloworld_unwoven_jamon) - An simple program monitored
with Jamon.  If you pass a command line argument to run the program in a loop the program will run long enough that you can look
at the Jamon metrics MBeans in the Jconsole.
* [unwoven_jdk](https://github.com/stevensouza/automon/tree/master/unwoven_jdk) - A simple program that when used with LTW will monitor
Java IO, and Http requests.

The [examples](https://github.com/stevensouza/automon/tree/master/examples) directory has scripts (*.sh) to run these programs.

Maven
-----------------------------------

Incorporate Automon into your maven project by adding the following dependency (using the most current version).

```xml
      <dependency>
          <groupId>org.automon</groupId>
          <artifactId>automon</artifactId>
          <version>1.0</version>
      </dependency>
```


Automon Source Code
-----------------------------------

The code that generates the automon-{version}.jar file is contained in [this directory](https://github.com/stevensouza/automon/tree/master/automon).


Support
-----------------------------------
And finally, if you need support contact us at [Issues](https://github.com/stevensouza/automon/issues) or email us
at admin@automon.org.

Thanks, and happy monitoring!

Steve