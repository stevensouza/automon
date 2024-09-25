# What is Automon?

Automon is a powerful Java library that combines the power of AOP (AspectJ) with monitoring or logging tools you already 
use to declaratively monitor and/or trace your Java code, the JDK, and any jars used by your application. It streamlines the process 
of monitoring and tracing, enabling you to gain valuable insights into your code's behavior without invasive modifications.

Version 1.0 of Automon only performed monitoring and version 2.0 added tracing.

## Why Automon?

- Non-invasive monitoring and tracing: No need to modify your existing code
- Flexibility: Works with various monitoring and logging tools you already use
- Comprehensive coverage: Monitor and trace your code, JDK, and third-party libraries/jars
- Performance insights: Easily identify bottlenecks and optimize your application
- Exception tracking: Catch and analyze exceptions for improved reliability

## Key Features

- **Performance Monitoring**: Track method execution times and identify performance bottlenecks.
- **Tracing**: Capture detailed logs of method calls, parameters, return values, and exceptions.
- **Exception Tracking**: Monitor and log exceptions, providing insights into error rates and potential issues.
- **Flexibility**: Works with various monitoring tools like Micrometer, JAMon, Yammer Metrics, StatsD, and Micrometer.
- **JDK and Third-Party Library Monitoring**: Monitor not just your code, but also JDK classes and third-party libraries/jars.
- **Spring Integration**: Seamlessly integrate with Spring applications.
- **Dynamic Enable/Disable**: Can be dynamically enabled or disabled via JMX.

# What are Automon's dependencies?

??Short AspectJ and SLF4J and Log4j2 tutorials can be found at these links.
<details>
<summary><strong>AspectJ (used for both Tracing and Monitoring)</strong></summary>

- **Aspect-Oriented Programming (AOP)** is a programming paradigm that addresses cross-cutting concerns, which are functionalities that span multiple parts of your application (e.g., logging, performance monitoring, security).
- **AspectJ** is a popular AOP framework for Java that provides a rich set of tools for defining and weaving aspects into your code.

Automon leverages AspectJ's capabilities to enable developers and administrators to not only define what parts of their 
code to observe (pointcuts), but also to seamlessly inject custom monitoring or tracing logic at those precise points (advice).
</details>

<details>
<summary><strong>SLF4J (used for Tracing only)</strong></summary>

SLF4J (Simple Logging Facade for Java) acts as a bridge between your application and the actual logging framework you 
choose to use. It allows you to switch logging implementations (like Log4j, Logback, etc.) without changing your code. 
This provides flexibility and makes your application independent of a specific logging library

'Tracing' is defined as the process of capturing detailed information about the execution flow of an application.
Automon utilizes SLF4J's flexibility to provide comprehensive tracing capabilities, capturing method entry and exit 
events along with crucial metadata such as method names, execution times, and parameter values, to name just a few.
</details>

<details>
<summary><strong>Monitoring tools (used for Monitoring only)</strong></summary>

'Monitoring' is defined as the practice of collecting and analyzing metrics to understand the behavior and health of an application.

Some monitoring tools Automon currently works with are: Micrometer JAMon, JavaSimon, Yammer Metrics, StatsD. Here are the current [implementations](https://github.com/stevensouza/automon/tree/master/automon/src/main/java/org/automon/implementations).
If automon doesn't support your tool of interest it can usually be supported by adding a simple class.

Note: [Micrometer](https://micrometer.io/docs) serves as a proxy for other monitoring/metering APIs and so through it automon does too.  As of 5/2019 the list of tools Micrometer can proxy includes: AppOptics, Atlas, Datadog, Dynatrace, Elastic, Ganglia, Graphite, Humio, Influx, JMX, KairosDB, New Relic, Prometheus, SignalFx, StatsD, Wavefront.

</details>

## How Automon Works

Automon uses AspectJ weaving to inject monitoring and tracing code into your application. This weaving can be done at:
- Runtime with Load Time Weaving (LTW)
- Build time with Build Time Weaving (BTW)

LTW offers greater flexibility, allowing you to dynamically define pointcuts and monitor even third-party libraries and JDK classes.

## Give some examples of what Automon can do??
- **Monitoring:** Automon is typically used to track method invocation time, and exception counts. It is very easy to set-up and you should
be able to start monitoring your code within minutes.  The data will be stored and displayed using the monitoring tool of your choice. The following image shows the type of data Automon collects (The example below displays the data in JAMon, however the data can be displayed in whatever monitoring tool/api you choose.  For example here is same data displayed in [grahphite/StatsD](https://github.com/stevensouza/automon/blob/master/docs/automon_statsd.png)).

![Automon method and exception metrics displayed in JAMon](https://github.com/stevensouza/automon/blob/master/docs/automon_jamon.png)

* **Tracing:** Here is an example of the output generated to an application log file when the `BasicContextTracingAspect` is used (note there are other tracing aspects such as 'FullContextTracingAspect'). Note that the following trace tracks method execution but it can also track many other join points such as instance variable get/set.

    * **Example:**??
      Automon automatically logged method entry (BEFORE) and exit (AFTER) for the `getFirstName()` method.
      It also kept as metadata the calls that led to its being called (`main(..)` called `run(..)` which called `getFirstName()`).
      The method exit tracks how long the method invocation took. In this example the `RequestIdAspect`?? was also used to create
      a unique request UUID that lasts the duration of the request. You can see how this trace output was generated by looking at
      `examples/hello-world-tracebasiccontext-ltw.sh`??

      Sample output:
      ```
      c.m.MyTracingBasicContextAspect INFO 08:51:56.273 - BEFORE MDC: {NDC0=HelloWorld.main(..), NDC1=HelloWorld.run(..), NDC2=HelloWorld.getFirstName(), kind=method-execution, requestId=5f7c836b-0283-42b7-b52c-1819e0ef855c} 
      c.m.MyTracingBasicContextAspect INFO 08:51:56.274 - AFTER MDC: {NDC0=HelloWorld.main(..), NDC1=HelloWorld.run(..), NDC2=HelloWorld.getFirstName(), executionTimeMs=0, kind=method-execution, requestId=5f7c836b-0283-42b7-b52c-1819e0ef855c}
      ```
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
          <version>2.0.0</version>
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

## Glossary
### Aspect-Oriented Programming (AOP) and AspectJ Concepts
* **AOP (Aspect-Oriented Programming)**: A programming paradigm that enables modularization of cross-cutting concerns.
* **Aspect**: A modular unit that encapsulates a cross-cutting concern, consisting of pointcuts and advice.
* **Advice**: The code that is executed at join points selected by a pointcut.
* **Join point**: A well-defined point in the execution of your program, such as a method call, field access, or exception handling.
* **Pointcut**: An expression that selects join points in your code where advice should be applied.
* **Weaving**: The process of combining aspects with your target code to perform some action such as monitoring or tracing.
* **AspectJ**: An AOP framework for Java.
* **BTW (Build-Time Weaving)**: Weaving aspects into your code during compilation.
* **LTW (Load-Time Weaving)**: Weaving aspects into your code at runtime.

### Monitoring/Observability/Logging/Tracing
* **Monitoring/Observability**: The practice of collecting and analyzing metrics and logs to understand the behavior and health of an application.
* **Monitoring tools**: Tools used to collect and visualize metrics about an application's performance and health.
* **Tracing**: The process of capturing detailed information about the execution flow of an application.
* **Log4j**: A popular logging framework for Java.
* **SLF4J**: A logging facade that provides a common interface for various logging frameworks.

### Software Development
* **Maven**: A build automation tool for Java projects.
* **Spring**: A popular framework for building enterprise Java applications.
* **Shaded jar**: An "uber-jar" that bundles all dependencies into a single JAR file.

