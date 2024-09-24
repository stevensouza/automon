# SLF4J and Log4j2 Tutorial

## Table of Contents
1. Introduction
2. SLF4J's Role
3. MDC (Mapped Diagnostic Context)
4. NDC (Nested Diagnostic Context)
5. Log4j2 Appenders
6. Sample log4j2.xml Configuration
7. Sample Java Program
8. Example Log Output

## 1. Introduction

This tutorial covers the basics of using SLF4J (Simple Logging Facade for Java) with Log4j2, including advanced features like MDC and NDC. We'll also provide sample configurations and a demo program to illustrate these concepts in action.

## 2. SLF4J's Role

SLF4J (Simple Logging Facade for Java) is an abstraction layer for various logging frameworks. Its main roles are:

- Providing a common interface for logging, allowing you to switch between different logging implementations without changing your code.
- Offering a simple and performant API for logging.
- Supporting parameterized logging messages to avoid unnecessary string concatenation.

To use SLF4J with Log4j2, you need the following dependencies:

```xml
<dependencies>
    <dependency>
        <groupId>org.apache.logging.log4j</groupId>
        <artifactId>log4j-core</artifactId>
        <version>${log4j.version}</version>
    </dependency>
    <dependency>
        <groupId>org.apache.logging.log4j</groupId>
        <artifactId>log4j-api</artifactId>
        <version>${log4j.version}</version>
    </dependency>
    <dependency>
        <groupId>org.apache.logging.log4j</groupId>
        <artifactId>log4j-slf4j2-impl</artifactId>
        <version>${log4j.version}</version>
    </dependency>
    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-api</artifactId>
        <version>${slf4j.version}</version>
    </dependency>
            <!--  Needed for SLF4J NDC capabilities-->
    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-ext</artifactId>
        <version>${slf4j.version}</version>
    </dependency>
</dependencies>
```

## 3. MDC (Mapped Diagnostic Context)

MDC is a feature that allows you to enrich log messages with contextual information. It's particularly useful in multi-threaded applications or when processing multiple requests.

Key points about MDC:
- It's a map of key-value pairs associated with the current thread.
- Values set in MDC are automatically included in log messages (when configured).
- It's thread-safe and works well in concurrent environments.

Usage:
```java
import org.slf4j.MDC;

MDC.put("user", "john_doe");
logger.info("User logged in");
MDC.clear();
```

## 4. NDC (Nested Diagnostic Context)

NDC is similar to MDC but uses a stack instead of a map. It's useful for tracking hierarchical information.

Key points about NDC:
- It maintains a stack of contextual information.
- Each new context is pushed onto the stack and can be popped off when no longer needed.
- It's less commonly used than MDC in modern applications.

Usage:
```java
import org.slf4j.NDC;

NDC.push("outer_context");
NDC.push("inner_context");
logger.info("Nested context log");
NDC.pop();
NDC.pop();
```

Note: Log4j2 uses `ThreadContext` for both MDC and NDC functionality.

## 5. Log4j2 Appenders

Appenders in Log4j2 are responsible for publishing log events to various destinations. Some common appenders include:

- ConsoleAppender: Writes log events to System.out or System.err
- FileAppender: Writes log events to a file
- RollingFileAppender: Writes log events to a file, rolling over based on time or size
- AsyncAppender: Asynchronous wrapper for other appenders to improve performance
- JDBCAppender: Writes log events to a database

## 6. Sample log4j2.xml Configuration

Here's a sample `log4j2.xml` configuration that uses a PatternLayout to display MDC/NDC information:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="WARN">
    <Appenders>
        <Console name="Console" target="SYSTEM_OUT">
            <PatternLayout pattern="%d{HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n MDC: %X NDC: %x"/>
        </Console>
        <File name="File" fileName="app.log">
            <PatternLayout pattern="%d{HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n MDC: %X NDC: %x"/>
        </File>
    </Appenders>
    <Loggers>
        <Root level="info">
            <AppenderRef ref="Console"/>
            <AppenderRef ref="File"/>
        </Root>
    </Loggers>
</Configuration>
```

In this configuration:
- `%X` prints all MDC key-value pairs
- `%x` prints the NDC stack

## 7. Sample Java Program

Here's a simple Java program that demonstrates the use of SLF4J, MDC, and NDC:

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.slf4j.NDC;

public class LoggingDemo {
    private static final Logger logger = LoggerFactory.getLogger(LoggingDemo.class);

    public static void main(String[] args) {
        // Using MDC
        MDC.put("user", "john_doe");
        MDC.put("session", "ABC123");
        
        logger.info("User logged in");

        // Using NDC
        NDC.push("outer_context");
        NDC.push("inner_context");
        
        logger.info("Processing order");
        
        // Simulating an error
        try {
            throw new RuntimeException("Order processing failed");
        } catch (RuntimeException e) {
            logger.error("Error occurred", e);
        }

        NDC.pop();
        NDC.pop();

        MDC.clear();
    }
}
```

## 8. Example Log Output

Based on the configuration and sample program above, the log output might look like this:

```
21:45:30.123 [main] INFO  LoggingDemo - User logged in
 MDC: {user=john_doe, session=ABC123} NDC: 
21:45:30.128 [main] INFO  LoggingDemo - Processing order
 MDC: {user=john_doe, session=ABC123} NDC: outer_context inner_context
21:45:30.130 [main] ERROR LoggingDemo - Error occurred
 MDC: {user=john_doe, session=ABC123} NDC: outer_context inner_context
java.lang.RuntimeException: Order processing failed
    at LoggingDemo.main(LoggingDemo.java:23)
    ...
```

This output demonstrates how MDC and NDC information is included in the log messages, providing valuable context for each log entry.
