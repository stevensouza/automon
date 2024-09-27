# Automon Tutorial

## Table of Contents
1. [Introduction](#introduction)
2. [AspectJ Weaving](#aspectj-weaving)
    - ['select' Pointcut](#select-pointcut)
3. [Getting Started](#getting-started)
    - [Spring AOP](#spring-aop)
4. [Load Time Weaving](#load-time-weaving)
    - [Advantages of Load-Time Weaving](#advantages-of-load-time-weaving)
    - [When to Use Load-Time Weaving](#when-to-use-load-time-weaving)
    - [Load-Time Weaving Examples](#load-time-weaving-examples)
5. [Build-Time Weaving](#build-time-weaving)
    - [Advantages of Build-Time Weaving](#advantages-of-build-time-weaving)
    - [When to Use Build-Time Weaving](#when-to-use-build-time-weaving)
    - [Build-Time Weaving Examples](#build-time-weaving-examples)
6. [Configuration](#configuration)
7. [JMX Integration](#jmx-integration)
8. [Advanced Usage](#advanced-usage)
    - [Custom Pointcuts](#custom-pointcuts)
9. [Conclusion](#conclusion)
10. [Glossary](#glossary)

## Introduction

Automon is a powerful tool that combines the capabilities of AspectJ with various monitoring and logging frameworks to provide comprehensive application monitoring and tracing. This tutorial will guide you through using Automon for both tracing and monitoring, demonstrating load-time and build-time weaving techniques.

## AspectJ Weaving
Automon is built on top of the powerful AspectJ library. 
AspectJ 'weaves' Automon tracing and monitoring code (called Aspects) into your classes.  This can be done...

* at runtime with the Load Time Weaver (LTW)
* at build time with the Build Time Weaver (BTW).

This tutorial will cover both LTW and BTW. The LTW example performs tracing and the BTW example performs monitoring
however both tracing and monitoring can work with either LTW or BTW.

Both approaches replace the original class files with ones that have tracing/monitoring added to them.  Regardless of whether LTW, or BTW is used the
generated class files are identical.  LTW is more flexible as it lets you use the powerful AspectJ pointcut language at
runtime to specify what classes you want to monitor. It also lets you monitor jdk classes and 3rd party library classes that you don't own such as java.net, jdbc, hadoop, spark etc. BTW only let's you monitor your own source code.

Here is a short video that shows how to monitor your code using Automon with LTW: [Automon demo](http://youtu.be/RdR0EdezS74)

### 'select' Pointcut
Whether you use LTW or BTW you will have to inherit from one of the Automon abstract aspects and provide the 'select' pointcut 
which indicates what code you would like to monitor or trace.  Here is an example of how you could do that in java code (this aspect could be used
for either LTW or BTW). 
```java
package com.mypackage.aspects;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.automon.aspects.monitoring.MonitoringAspect;

@Aspect
public class ConcreteMonitoringAspect extends MonitoringAspect {
  // Monitor all methods in the HelloWorld class regardless of return values and arguments.  
  @Pointcut("execution(public * com.stevesouza.helloworld.HelloWorld.*(..))")
  public void select() {
  }

}
```
or for LTW you could specify the 'select' pointcut in the aop.xml file 
```xml
<!--This example shows how to do a request id aspect and basic tracing by providing the 'select' pointcut -->
<aspectj>
  <aspects>
     <concrete-aspect name="com.myorganization.MyRequestIdAspect" extends="org.automon.aspects.tracing.aspectj.RequestIdAspect">
          <pointcut name="select"  expression="execution(* com.stevesouza.helloworld.HelloWorld.main(..))"/>
      </concrete-aspect>

     <concrete-aspect name="com.myorganization.MyTracingBasicContextAspect" extends="org.automon.aspects.tracing.spring.BasicContextTracingAspect">
            <pointcut name="select"  expression="execution(* com.stevesouza.helloworld.HelloWorld.*(..))"/>
      </concrete-aspect>
  </aspects>
  
  <weaver>
      <include within="com.stevesouza..*"/>
  </weaver>
</aspectj>
```
Look at the various `*.sh` [examples](https://github.com/stevensouza/automon/tree/master/examples) or maven modules to see more examples.

## Getting Started
The quickest way to get started with Automon is to download this distribution, run `mvn clean install` from the parent directory
and go to the [examples](https://github.com/stevensouza/automon/tree/master/examples) directory and run the sample programs (*.sh). There are more directions on running the examples  
[examples](https://github.com/stevensouza/automon/tree/master/examples) directory `README' file.

### Spring AOP
If you are using **Spring** the following maven module shows how to [monitor Spring beans with Automon](https://github.com/stevensouza/automon/tree/master/spring_aop).  
* This [directory contains the aspects](https://github.com/stevensouza/automon/tree/master/spring_aop/src/main/java/org/automon/spring_aop/aspects) that will trace and monitor the Spring code.
  * Note Spring only supports the `execution` pointcut on Spring bean public methods.  This is a limitation of Spring and not the Automon aspects themselves which can work on any pointcut type as well as on non-public parts of a codebase.
* This [directory contains the application source code](https://github.com/stevensouza/automon/tree/master/spring_aop/src/main/java/org/automon/spring_aop) being monitored.
* This  [applicationContext.xml](https://github.com/stevensouza/automon/blob/master/spring_aop/src/main/resources/applicationContext.xml) file configures specifies which Spring beans to trace/monitor.
* Look at this [pom.xml ](https://github.com/stevensouza/automon/tree/master/spring_aop) file to see how a Spring project should be configured.

Automon does not require Spring though. **Running a non-Spring program** with Automon is easy too.  You simply...

* Put automon-{version}.jar in your classpath,
* And make either aspectjweaver.jar (LTW), or aspectjrt.jar (BTW) available.
* And include a monitoring tool dependency (i.e. micrometer, JAMon etc.) if you are performing Automon monitoring or slf4j and a logging tool dependency like log4j2 if you would like to perform Automon tracing.

You can also look in the various demo maven modules [pom.xml](https://github.com/stevensouza/automon/blob/master/spring_woven/pom.xml) file to see what dependencies are needed
to run Automon. The Automon dependency itself will look something like this
```xml
      <dependency>
          <groupId>org.automon</groupId>
          <artifactId>automon</artifactId>
          <version>2.0.0</version>
      </dependency>
```

The [examples](https://github.com/stevensouza/automon/tree/master/examples) directory shows how to invoke your programs using both LTW, and BTW.


### Load Time Weaving

#### Advantages of Load-Time Weaving

1. **Flexibility**: LTW allows you to add or modify aspects without recompiling your application.
2. **Runtime decision-making**: You can decide whether to apply aspects at runtime.
3. **Easier testing**: You can run your application with or without aspects easily.
4. **No build-time dependencies**: Your build process doesn't need to be aware of AspectJ.

#### When to Use Load-Time Weaving

- When you need to add monitoring or tracing to third-party libraries or classes you don't own.
- In development environments where quick iteration and testing with different aspect configurations is valuable.
- When you want to keep your source code and compiled classes free of woven aspects.

#### Load-Time Weaving Examples
(LTW) also involves providing an `ajc-aop.xml` config file.  Review the [config files](https://github.com/stevensouza/automon/tree/master/examples/config)
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

And each of the modules has its own `README.md` file with instructions on how to run their program.

### Build-Time Weaving

#### Advantages of Build-Time Weaving

1. **Performance**: BTW typically has better runtime performance as the weaving is done at compile-time.
2. **Verification**: Errors in aspect definitions are caught at compile-time.
3. **Simpler deployment**: The woven classes can be deployed without additional runtime dependencies.

#### When to Use Build-Time Weaving

- For production environments where runtime performance is critical.
- When you want to catch potential errors in your aspect definitions early in the development process.
- When you have full control over the source code and build process of the application you're monitoring.

#### Build-Time Weaving Examples
(BTW) - And finally if you want to use Build Time Weaving in your maven build process refer to these BTW sample projects (In the examples I use 'woven' and BTW synonymously):

* [helloworld_woven](https://github.com/stevensouza/automon/tree/master/helloworld_woven) - A simple project that
  has a dependency on Automon and a simple jar that contains a HelloWorld application.  The output of this project is a jar
  that contains AspectJ BTW woven code.
* [spring_woven](https://github.com/stevensouza/automon/tree/master/spring_woven) - This project shows how you can weave a Spring project at build time too.
* Another example using [Spring woven and Apache Camel](https://github.com/stevensouza/camel).  See more details in the comments section for camel_experiment6_soap. This example let's you instrument/monitor any class that you own and not just Spring beans.

The [examples](https://github.com/stevensouza/automon/tree/master/examples) directory has scripts (*.sh) to run these programs. And each of the modules has its own `README.md` file 
with instructions on how to run their program.

An interview that covers Automon can be found [here](http://jaxenter.com/advanced-java-monitoring-with-automon-116079.html).

### Configuration

* Automon monitoring will pick a supported monitoring tool if it is in the classpath or you can explicitly provide it 
either with a command line argument or via the `automon.properties` file. Here is an example of using the [command line argument](https://github.com/stevensouza/automon/blob/master/examples/hello-world-sysout-ltw.sh#L18)
 and here is an example of what you could put in your `automon.properties` file `org.automon=org.mypackage.MyOpenMon`.
* Both monitoring and tracing can be disabled at startup by using the concrete aspect name in the the following way in the `automon.properties` file.

```properties
org.mypackage.MyBasicContextTracingAspect.enable=false
org.mypackage.MyBasicContextTracingAspect.enableLogging=false
```

### JMX Integration

Automon provides JMX integration for runtime configuration. You can use JConsole or any other JMX client to enable/disable aspects, enable/disable logging or change the OpenMon implementation at runtime depending on the type of 
aspect under management.

For example the following screen snapshot shows two different Automon aspects that can be enabled/disabled at runtime
(`request_id_native` which adds a unique request id to a trace and `trace_log_basic_context_spring` that adds other 
context to the trace such as the method being invoked). Logging can also be enabled/disabled at runtime for any tracing aspect.
![Automon JMX Example](https://github.com/stevensouza/automon/blob/master/docs/automon_jmx.png)
To enable JMX, add the following system property when running your application:

```bash
-Dcom.sun.management.jmxremote
```

Then, you can connect to your application using JConsole and navigate to the Automon MBeans to configure the aspects.


## Advanced Usage

### Custom Pointcuts

You can create more complex pointcuts to target specific methods or classes. Automon, leveraging AspectJ, supports various pointcut designators. Note that when using Spring AOP, only the `execution` pointcut is fully supported. Here are some additional pointcut types you can use with AspectJ:

1. **execution**: Matches the execution of methods.
    - Example: `execution(* com.example.Service.*(..))`
    - Explanation: Matches the execution of any method in the `Service` class.

2. **call**: Matches method call join points.
    - Example: `call(* com.example.Service.doSomething())`
    - Explanation: Matches when the `doSomething()` method of `Service` is called, rather than when it's executed.

3. **set**: Matches field set join points.
    - Example: `set(* com.example.Model.*)`
    - Explanation: Matches when any field in the `Model` class is set.

4. **get**: Matches field get join points.
    - Example: `get(private * com.example.Model.*)`
    - Explanation: Matches when any private field in the `Model` class is accessed.

You can combine these pointcuts for more precise targeting:

```java
@Pointcut("execution(* com.example..*.*(..))")
public void allMethodsInPackage() {}

@Pointcut("@annotation(com.example.Monitor)")
public void annotatedMethods() {}

@Pointcut("call(* com.example.Service.doSomething())")
public void serviceMethodCalls() {}

@Pointcut("set(private * com.example.Model.*)")
public void modelFieldSets() {}

@Pointcut("(allMethodsInPackage() && annotatedMethods()) || serviceMethodCalls() || modelFieldSets()")
public void select() {}
```

This combined pointcut will match:
- All methods in the `com.example` package (and its subpackages) that are annotated with `@Monitor`
- Calls to the `doSomething()` method of the `Service` class
- Sets of any private field in the `Model` class

Remember that while these advanced pointcuts provide powerful capabilities with AspectJ, they may not be available when using Spring AOP, which primarily supports method execution pointcuts.


## Conclusion

This tutorial has demonstrated how to use Automon for both tracing and monitoring, using load-time and build-time weaving techniques. We've explored the advantages and use cases for both weaving methods, allowing you to choose the best approach for your specific needs.

Automon provides a powerful way to add monitoring and tracing to your Java applications with minimal code changes, leveraging the strengths of AspectJ and various monitoring frameworks. By using load-time weaving, you gain flexibility and ease of testing, while build-time weaving offers better runtime performance and early error detection.

Remember to consult the Automon documentation for more advanced features and configurations. Happy monitoring and tracing!

## Glossary

Aspect-Oriented Programming Concepts:
- **AOP**: A programming paradigm that increases modularity by separating cross-cutting concerns.
- **Cross-cutting concerns**: Functionalities or behaviors in a program that span multiple modules and can't be cleanly separated from the main business logic, such as logging, security, or transaction management.
- **Aspect**: A module encapsulating cross-cutting concerns, containing pointcuts and advice.
- **Join Point**: A specific moment during program execution where aspect code can be applied.
- **Pointcut**: A set of join points where advice should be applied.
- **Advice**: Code that executes at specified join points, defining actions to be taken.
- **Weaving**: The process of applying aspects to a target object to create a new proxied object.

AOP Technologies and Tools:
- **AspectJ**: An aspect-oriented programming extension for Java.
- **Spring AOP**: The aspect-oriented programming framework in the Spring Framework.
- **Automon**: A Java library combining AOP (AspectJ) with monitoring and logging tools for declarative code monitoring and tracing.

Weaving Types:
- **BTW**: Build-Time Weaving, where aspects are woven during the build process.
- **LTW**: Load-Time Weaving, where aspects are woven at runtime when classes are loaded.

Related Technology:
- **JMX**: Java Management Extensions, providing tools for managing and monitoring Java applications.