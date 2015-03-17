# Automon
Automon combines the power of AOP (AspectJ) with monitoring tools (JAMon, JavaSimon, Yammer Metrics, ...) or logging tools
(perf4j, log4j, sl4j, ...) that you already use to declaratively monitor...

* Your Java code,
* The JDK,
* Any jars used by your application

Automon is typically used to time monitor method invocations, and count exceptions. It is very easy to set-up and you should
be able to start monitoring your code within minutes.

It is important to note that Automon is complimentary to other monitoring tools
such as JAMon, JavaSimon, Yammer Metrics, and New Relic.  In fact you specify which of these tools you would like to
perform the monitoring.

Automon can also be dynamically enabled/disabled by accessing the Automon MXBean (JMX).

AspectJ Weaving
-----------------------------------
AspectJ 'weaves' Automon monitoring code into your classes.  This can be done at runtime with the Load Time Weaver (LTW)
or at build time with the Build Time Weaver (BTW).  Both of these cases generate the same class files.  LTW is more flexible
as it lets you use the powerful AspectJ pointcut language at runtime to specify what classes you want to monitor.

Getting Started
-----------------------------------
The quickest way to get started with Automon is to download this distribution, and go to the [examples](https://github.com/stevensouza/automon/tree/master/examples)
directory and run the sample programs (*.sh).  There are more directions on running the examples in the 'examples' directories README file.

**Running a program** with Automon is easy.  You simply put automon-{version}.jar in your classpath, and make either aspectjweaver.jar (LTW),
or aspectjrt.jar (BTW) available.  The [examples](https://github.com/stevensouza/automon/tree/master/examples) directory shows how to invoke your programs using both LTW, and BTW.

**Load Time Weaving** (LTW) also involves providing an ajc-aop.xml config file so review [config files](https://github.com/stevensouza/automon/tree/master/examples/config)
for more information on them.

**Build Time Weaving** (BTW) - And finally if you want to use Build Time Weaving in your maven build process refer to these BTW sample projects:

* [helloworld_woven](https://github.com/stevensouza/automon/tree/master/helloworld_woven) - A simple project that
has a dependency on Automon and a simple jar that contains a HelloWorld application.  The output of this project is a jar
  that contains AspectJ BTW woven code.
* [spring_woven](https://github.com/stevensouza/automon/tree/master/spring_woven) - This project shows how you can weave a Spring
project at build time.

Support
-----------------------------------
And finally, if you need support contact us at [Issues](https://github.com/stevensouza/automon/issues) or email us
at admin@automon.org.

Thanks, and happy monitoring!

Steve