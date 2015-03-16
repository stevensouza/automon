# Automon
Automon combines the power of AOP (AspectJ) with any monitoring tools (JAMon, JavaSimon, Yammer Metrics, ...) or logging tools
(perf4j, log4j, sl4j, ...) that you already use to declaratively monitor your Java code, the JDK, and any jars used by your application.

Automon is typically used to time monitor method invocations, and count exceptions. It is very easy to set-up and you should
be able to monitor your code within minutes.  It is important to note that Automon is complimentary to other monitoring tools
such as JAMon, JavaSimon, Yammer Metrics, and New Relic.  In fact underneath the hood it can use these tools and more to
perform the monitoring.

AspectJ Weaving
-----------------------------------
AspectJ 'weaves' Automon monitoring code into your classes.  This can be done at runtime with the Load Time Weaver (LTW)
or at build time with the Build Time Weaver (BTW).  Both cases generate the same class files.  LTW is more flexible
as it lets you use the powerful AspectJ pointcut language at runtime to specify what classes you want to monitor.

Getting Started
-----------------------------------
The quickest way to get started is to download this distribution, and go to the [examples](https://github.com/stevensouza/automon/tree/master/examples)
directory and run the sample programs (*.sh).  There are more directions on running the examples in the 'examples' directories README file.

Running a program with Automon is easy.  You simply put the automon-{version}.jar in your classpath, and make either aspectjweaver.jar (LTW),
or aspectjrt.jar (BTW) available.  The 'examples' directory shows how to invoke your program using both LTW, and BTW.

Load Time Weaving (LTW) also involves providing an ajc-aop.xml file so review the [config files](https://github.com/stevensouza/automon/tree/master/examples/config)
for more information on them.

Build Time Weaving (BTW) - And finally if you want to use Build Time Weaving in your maven build process refer to these BTW sample projects:

* [helloworld_woven](https://github.com/stevensouza/automon/tree/master/helloworld_woven) - A simple project that combines
has a dependency on Automon and a simple jar that contains a HelloWorld application.  The output of this project is a jar
  that contains AspectJ woven code.
* [spring_woven](https://github.com/stevensouza/automon/tree/master/spring_woven) - This project shows how you can weave a Spring
project at build time.

Support
-----------------------------------
And finally if you would like support contact me at [Issues](https://github.com/stevensouza/automon/issues) or email me
at steve@stevesouza.com.

Thanks, and happy monitoring!

Steve