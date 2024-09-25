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

A short [AspectJ tutorial](https://github.com/stevensouza/automon/blob/master/docs/aspectj-tutorial.md)
 and a a short [SLF4J and Log4j2 tutorial](https://github.com/stevensouza/automon/blob/master/docs/slf4j-log4j2-tutorial.md) can be found at these links.
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

LTW offers greater flexibility, allowing you to dynamically define pointcuts and trace or monitor even third-party libraries and JDK classes.
See the [Automon Tutorial](https://github.com/stevensouza/automon/blob/master/docs/automon-tutorial.md) for more detailed examples on how Automon works.

**It is important to note that Automon is complimentary to monitoring and logging tools. Automon performs no monitoring on its own and leverages slf4j for tracing.**
It serves as a bridge between AspectJ (AspectJ defines 'what to trace or monitor') and monitoring and logging tools (which define 'how to trace or monitor').
You can also easily provide your own monitoring tool by implementing the simple [OpenMon](https://github.com/stevensouza/automon/blob/master/automon/src/main/java/org/automon/implementations/OpenMon.java)
interface.

The following diagram shows an AspectJ pointcut that will monitor all methods (any return type, any number of arguments)
in the 'com.mycompany' package as well as its subpackages (..) using your monitoring tool of choice.
![Automon](https://github.com/stevensouza/automon/blob/master/docs/automon_bridge.png)

And finally, Automon can be dynamically enabled/disabled via the Automon MXBean (JMX).

## What are some examples of what Automon can do?
- **Monitoring:** Automon monitoring is typically used to track method invocation time, and exception counts. It is very easy to set-up. You should
be able to start monitoring your code within minutes.  The data will be stored and displayed using the monitoring tool of your choice. 
The following image shows the type of data Automon collects (The example below displays the data in JAMon, however the data can be displayed in whatever monitoring tool/api you choose.  For example here is same data displayed in [grahphite/StatsD](https://github.com/stevensouza/automon/blob/master/docs/automon_statsd.png)).

![Automon method and exception metrics displayed in JAMon](https://github.com/stevensouza/automon/blob/master/docs/automon_jamon.png)

* **Tracing:** Here is an example of the output sent to an application log file when the `BasicContextTracingAspect` is used (note there are other tracing aspects such as 'FullContextTracingAspect'). Note that the following trace tracks method execution but it can also track many other join points such as instance variable get/set.

    * **Example:**
      Automon automatically logged method entry (BEFORE) and exit (AFTER) for the `getFirstName()` method.
      It also kept as metadata the calls that led to `getFirstName()` being called (`main(..)` called `run(..)` which called `getFirstName()`).
      The method exit (AFTER) tracks how long the method invocation took. In this example the [RequestIdAspect](https://github.com/stevensouza/automon/blob/master/automon/src/main/java/org/automon/aspects/tracing/spring/RequestIdAspect.java) was also used to create
      a unique request UUID that lasts the duration of the request. You can see how this trace output was generated by looking at
      [examples/hello-world-tracebasiccontext-ltw.sh](https://github.com/stevensouza/automon/tree/master/examples/hello-world-tracebasiccontext-ltw.sh)

      Sample output:
      ```
      c.m.MyTracingBasicContextAspect INFO 08:51:56.273 - BEFORE MDC: {NDC0=HelloWorld.main(..), NDC1=HelloWorld.run(..), NDC2=HelloWorld.getFirstName(), kind=method-execution, requestId=5f7c836b-0283-42b7-b52c-1819e0ef855c} 
      c.m.MyTracingBasicContextAspect INFO 08:51:56.274 - AFTER MDC: {NDC0=HelloWorld.main(..), NDC1=HelloWorld.run(..), NDC2=HelloWorld.getFirstName(), executionTimeMs=0, kind=method-execution, requestId=5f7c836b-0283-42b7-b52c-1819e0ef855c}
      ```

Automon Source Code
-----------------------------------

The code that generates the automon-{version}.jar file is contained in [this directory](https://github.com/stevensouza/automon/tree/master/automon). All other modules are
examples how to use Automon.

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


Support
-----------------------------------
And finally, if you need support contact us at [Issues](https://github.com/stevensouza/automon/issues) or email us
at admin@automon.org.

Thanks, and happy tracing and monitoring!

Steve

