# Automon Tutorial

## Table of Contents
1. [Introduction](#introduction)
2. [Getting Started](#getting-started)
    - [Dependencies](#dependencies)
    - [Configuration](#configuration)
3. [ExampleService Class](#exampleservice-class)
4. [Tracing with Automon (Load-Time Weaving)](#tracing-with-automon-load-time-weaving)
    - [Advantages of Load-Time Weaving](#advantages-of-load-time-weaving)
    - [When to Use Load-Time Weaving](#when-to-use-load-time-weaving)
    - [Using BasicContextTracingAspect with LTW](#using-basiccontexttracingaspect-with-ltw)
5. [Monitoring with Automon (Build-Time Weaving)](#monitoring-with-automon-build-time-weaving)
    - [Advantages of Build-Time Weaving](#advantages-of-build-time-weaving)
    - [When to Use Build-Time Weaving](#when-to-use-build-time-weaving)
    - [Using MonitoringAspect with Micrometer](#using-monitoringaspect-with-micrometer)
6. [Advanced Usage](#advanced-usage)
    - [Custom Pointcuts](#custom-pointcuts)
    - [JMX Integration](#jmx-integration)
7. [Conclusion](#conclusion)

## Introduction

Automon is a powerful tool that combines the capabilities of AspectJ with various monitoring and logging frameworks to provide comprehensive application monitoring and tracing. This tutorial will guide you through using Automon for both tracing and monitoring, demonstrating load-time and build-time weaving techniques.

## Getting Started

### Dependencies

To use Automon, you'll need to include the following dependencies in your project:

```xml
<dependencies>
    <dependency>
        <groupId>org.automon</groupId>
        <artifactId>automon</artifactId>
        <version>2.0.0</version>
    </dependency>
    <dependency>
        <groupId>org.aspectj</groupId>
        <artifactId>aspectjrt</artifactId>
        <version>1.9.7</version>
    </dependency>
    <dependency>
        <groupId>org.aspectj</groupId>
        <artifactId>aspectjweaver</artifactId>
        <version>1.9.7</version>
    </dependency>
    <!-- For Micrometer integration -->
    <dependency>
        <groupId>io.micrometer</groupId>
        <artifactId>micrometer-core</artifactId>
        <version>1.9.0</version>
    </dependency>
    <!-- SLF4J and Log4j2 for logging -->
    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-api</artifactId>
        <version>1.7.32</version>
    </dependency>
    <dependency>
        <groupId>org.apache.logging.log4j</groupId>
        <artifactId>log4j-core</artifactId>
        <version>2.17.1</version>
    </dependency>
    <dependency>
        <groupId>org.apache.logging.log4j</groupId>
        <artifactId>log4j-slf4j-impl</artifactId>
        <version>2.17.1</version>
    </dependency>
</dependencies>
```

### Configuration

Create an `automon.properties` file in your `src/main/resources` directory:

```properties
# Enable Automon
org.automon.aspectj.enabled=true
```

## ExampleService Class

Let's create a simple `ExampleService` class that we'll use for both our load-time and build-time weaving examples:

```java
package com.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExampleService {
    private static final Logger logger = LoggerFactory.getLogger(ExampleService.class);

    public void doSomething() throws InterruptedException {
        logger.info("Doing something...");
        // Simulate some work
        Thread.sleep(100);
    }

    public void mightThrowException() throws Exception {
        logger.info("Might throw an exception...");
        if (Math.random() < 0.5) {
            throw new Exception("Random exception");
        }
    }

    public static void main(String[] args) {
        ExampleService service = new ExampleService();

        // Run some operations
        for (int i = 0; i < 10; i++) {
            try {
                service.doSomething();
                service.mightThrowException();
            } catch (Exception e) {
                logger.error("An error occurred: ", e);
            }
        }
    }
}
```

## Tracing with Automon (Load-Time Weaving)

### Advantages of Load-Time Weaving

1. **Flexibility**: LTW allows you to add or modify aspects without recompiling your application.
2. **Runtime decision-making**: You can decide whether to apply aspects at runtime.
3. **Easier testing**: You can run your application with or without aspects easily.
4. **No build-time dependencies**: Your build process doesn't need to be aware of AspectJ.

### When to Use Load-Time Weaving

- When you need to add monitoring or tracing to third-party libraries or classes you don't own.
- In development environments where quick iteration and testing with different aspect configurations is valuable.
- When you want to keep your source code and compiled classes free of woven aspects.

### Using BasicContextTracingAspect with LTW

For load-time weaving, create an `aop.xml` file in the `src/main/resources/META-INF` directory:

```xml
<!DOCTYPE aspectj PUBLIC "-//AspectJ//DTD//EN" "https://www.eclipse.org/aspectj/dtd/aspectj.dtd">
<aspectj>
    <weaver options="-verbose -showWeaveInfo">
        <include within="com.example.*"/>
    </weaver>
    <aspects>
        <concrete-aspect name="com.example.CustomBasicContextTracingAspect" extends="org.automon.aspects.tracing.aspectj.BasicContextTracingAspect">
            <pointcut name="select" expression="execution(* com.example.ExampleService.*(..))"/>
        </concrete-aspect>
    </aspects>
</aspectj>
```

To run the example with load-time weaving, use the following command:

```bash
java -javaagent:/path/to/aspectjweaver.jar -classpath /path/to/your/classes com.example.ExampleService
```

Make sure to replace `/path/to/aspectjweaver.jar` with the actual path to the AspectJ weaver JAR file, and `/path/to/your/classes` with the path to your compiled classes.

## Monitoring with Automon (Build-Time Weaving)

### Advantages of Build-Time Weaving

1. **Performance**: BTW typically has better runtime performance as the weaving is done at compile-time.
2. **Verification**: Errors in aspect definitions are caught at compile-time.
3. **Simpler deployment**: The woven classes can be deployed without additional runtime dependencies.

### When to Use Build-Time Weaving

- For production environments where runtime performance is critical.
- When you want to catch potential errors in your aspect definitions early in the development process.
- When you have full control over the source code and build process of the application you're monitoring.

### Using MonitoringAspect with Micrometer

First, let's create a custom MonitoringAspect:

```java
package com.example;

import org.automon.aspects.monitoring.MonitoringAspect;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class MicrometerMonitoringAspect extends MonitoringAspect {

    @Pointcut("execution(* com.example.ExampleService.*(..))")
    public void select() {}

}
```

For build-time weaving, you'll need to configure the AspectJ Maven plugin in your `pom.xml`:

```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>aspectj-maven-plugin</artifactId>
    <version>1.14.0</version>
    <configuration>
        <complianceLevel>1.8</complianceLevel>
        <source>1.8</source>
        <target>1.8</target>
        <showWeaveInfo>true</showWeaveInfo>
        <verbose>true</verbose>
        <aspectLibraries>
            <aspectLibrary>
                <groupId>org.automon</groupId>
                <artifactId>automon</artifactId>
            </aspectLibrary>
        </aspectLibraries>
    </configuration>
    <executions>
        <execution>
            <goals>
                <goal>compile</goal>
                <goal>test-compile</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

To run the example with build-time weaving, simply execute the `main` method of the `ExampleService` class after compiling with the AspectJ Maven plugin.

Note: Automon will automatically detect Micrometer if it's available on the classpath, so there's no need for explicit initialization or registration.

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

### JMX Integration

Automon provides JMX integration for runtime configuration. You can use JConsole or any other JMX client to enable/disable aspects or change the OpenMon implementation at runtime.

To enable JMX, add the following system property when running your application:

```bash
-Dcom.sun.management.jmxremote
```

Then, you can connect to your application using JConsole and navigate to the Automon MBeans to configure the aspects.

## Conclusion

This tutorial has demonstrated how to use Automon for both tracing and monitoring, using load-time and build-time weaving techniques. We've explored the advantages and use cases for both weaving methods, allowing you to choose the best approach for your specific needs.

Automon provides a powerful way to add monitoring and tracing to your Java applications with minimal code changes, leveraging the strengths of AspectJ and various monitoring frameworks. By using load-time weaving, you gain flexibility and ease of testing, while build-time weaving offers better runtime performance and early error detection.

Remember to consult the Automon documentation for more advanced features and configurations. Happy monitoring and tracing!