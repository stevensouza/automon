# Automon
Automon combines the power of AOP (AspectJ) with monitoring tools  or logging tools that you already use to declaratively monitor the following:

* Your Java code,
* The JDK,
* Any jars used by your application. 

Some monitoring tools Automon currently works with are: JAMon, JavaSimon, Yammer Metrics, StatsD, Micrometer. Here are the current [implementations](https://github.com/stevensouza/automon/tree/master/automon/src/main/java/org/automon/implementations). If automon doesn't support your tool of intrest it can usually be supported by adding a simple class.

Note: [Micrometer](https://micrometer.io/docs) serves as a proxy for other monitoring/metering APIs and so through it automon does too.  As of 5/2019 the list of tools Micrometer can proxy includes: AppOptics, Atlas, Datadog, Dynatrace, Elastic, Ganglia, Graphite, Humio, Influx, JMX, KairosDB, New Relic, Prometheus, SignalFx, StatsD, Wavefront. 

**Automon is typically used to track method invocation time, and exception counts.** It is very easy to set-up and you should
be able to start monitoring your code within minutes.  The data will be stored and displayed using the monitoring tool of your choice. The following image shows the type of data Automon collects (The example below displays the data in JAMon, however the data can be displayed in whatever monitoring tool/api you choose.  For example here is same data displayed in [grahphite/StatsD](https://github.com/stevensouza/automon/blob/master/docs/automon_statsd.png)).

![Automon method and exception metrics displayed in JAMon](https://github.com/stevensouza/automon/blob/master/docs/automon_jamon.png)

**The following is a sample command that will monitor your program with Automon** (in this case using JAMon):
* java  -Dorg.aspectj.weaver.loadtime.configuration=file:aop.xml -javaagent:aspectjweaver.jar -classpath automon-{version}.jar:myapplication.jar:jamon-2.81.jar com.mypackage.MyClass
* Running with Micrometer, Yammer Metrics, StatsD, JavaSimon etc. you would simply use their respective jars instead of the JAMon jar
* aop.xml (AspectJ file) is where you define what parts of your program you want monitored (through 'pointcuts'). It is often quite simple.
    * Here is a sample pointcut monitoring all methods in all classes in package 'com.stevesouza':  `<pointcut name="profile" expression="within(com.stevesouza..*)"/>`
    * And a full AOP config file [Example monitoring JDK classes (JDBC, IO, Net packages), and custom classes:]( https://github.com/stevensouza/automon/blob/master/examples/config/automon-aop.xml)
* aspectjweaver.jar is required for AspectJ to monitor the code.

See 'Getting Started' below and [examples](https://github.com/stevensouza/automon/tree/master/examples) for instructions on how to run Automon with CodaHale/Yammer Metrics, JavaSimon, NewRelic, StatsD, Tomcat and Jetty.

**It is important to note that Automon is complimentary to monitoring and logging tools. Automon performs no monitoring on its own.**
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

Both approaches replace the original class files with ones that have monitoring added to them.  Regardless of whether LTW, or BTW is used the
 generated class files are identical.  LTW is more flexible as it lets you use the powerful AspectJ pointcut language at
 runtime to specify what classes you want to monitor. It also lets you monitor jdk classes and 3rd party library classes that you don't own such as java.net, jdbc, hadoop, spark etc. BTW only let's you monitor your own source code.

Here is a short video that shows how to monitor your code using Automon with LTW: [Automon demo](http://youtu.be/RdR0EdezS74)

Getting Started
-----------------------------------
The quickest way to get started with Automon is to download this distribution, and go to the [examples](https://github.com/stevensouza/automon/tree/master/examples)
directory and run the sample programs (*.sh).  There are more directions on running the examples in the 'examples' directories README file.

If you are using **Spring** the following maven module shows how to [monitor Spring beans with Automon](https://github.com/stevensouza/automon/tree/master/spring_aop).  In particular look at the Spring [applicationContext.xml](https://github.com/stevensouza/automon/blob/master/spring_aop/src/main/resources/applicationContext.xml) file to see how to specify which Spring beans to monitor.

Automon does not require Spring though. **Running a non-Spring program** with Automon is easy too.  You simply...

* Put automon-{version}.jar in your classpath,
* And make either aspectjweaver.jar (LTW), or aspectjrt.jar (BTW) available.

The [examples](https://github.com/stevensouza/automon/tree/master/examples) directory
shows how to invoke your programs using both LTW, and BTW.

**Load Time Weaving** (LTW) also involves providing an ajc-aop.xml config file.  Review the [config files](https://github.com/stevensouza/automon/tree/master/examples/config)
for more information on them. The following maven projects generate plain (unwoven) java jars.  Each of them has a *.sh script
 in the [examples](https://github.com/stevensouza/automon/tree/master/examples) directory that lets you run the the java code with LTW.

* [helloworld_unwoven_jamon](https://github.com/stevensouza/automon/tree/master/helloworld_unwoven_jamon) - A simple program monitored
with Jamon.  If you pass a command line argument to run the program in a loop the program will run long enough that you can look
at the Jamon metrics MBeans in the Jconsole.
* [unwoven_jdk](https://github.com/stevensouza/automon/tree/master/unwoven_jdk) - A simple program that when used with LTW will monitor
Java IO, Http requests, and JDBC calls.
* [webapp_unwoven](https://github.com/stevensouza/automon/tree/master/webapp_unwoven) - A web application (war) that can be installed
in a web container like Tomcat or Jetty. It monitors calls to the jdk (jdbc, io, and http requests) and custom classes.  See
[README.md](https://github.com/stevensouza/automon/tree/master/webapp_unwoven) for more information.


**Build Time Weaving** (BTW) - And finally if you want to use Build Time Weaving in your maven build process refer to these BTW sample projects (In the examples I use 'woven' and BTW synonymously):

* [helloworld_woven](https://github.com/stevensouza/automon/tree/master/helloworld_woven) - A simple project that
has a dependency on Automon and a simple jar that contains a HelloWorld application.  The output of this project is a jar
  that contains AspectJ BTW woven code.
* [spring_woven](https://github.com/stevensouza/automon/tree/master/spring_woven) - This project shows how you can weave a Spring project at build time.
* Another example using [Spring woven and Apache Camel](https://github.com/stevensouza/camel).  See more details in the comments section for camel_experiment6_soap. This example let's you instrument/monitor any class that you own and not just Spring beans.

The [examples](https://github.com/stevensouza/automon/tree/master/examples) directory has scripts (*.sh) to run these programs.

An interview that covers Automon can be found [here](http://jaxenter.com/advanced-java-monitoring-with-automon-116079.html).

Maven
-----------------------------------

Incorporate Automon into your maven project by adding the following dependency ([using the most current version](http://search.maven.org/#search%7Cga%7C1%7Cautomon)).

```xml
      <dependency>
          <groupId>org.automon</groupId>
          <artifactId>automon</artifactId>
          <version>1.0.3</version>
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
