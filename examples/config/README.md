Automon LTW Config XML Files
========================

By configuring AspectJ LTW (Load Time Weaving) xml files you can tell Automon which parts of your program you would like to monitor simply by
 providing 2 pointcuts (user_monitor, and user_exceptions). This directory contains xml files that you can use as starting points for using Automon and AspectJ LTW to monitor your Java programs.


 If you are unfamiliar with pointcuts please refer to AspectJ documentation.

 By overriding the following pointcuts in the provided xml files you can quickly start monitoring your programs:

 * **user_monitor** - Any code that is specified with this pointcut will be timed by calling Automon's 'start' method when the code is
 entered and 'stop' when the method is completed.  JAMon, JavaSimon and Yammer Metrics (or your own implementation) will be called to monitor the
 performance of any classes or methods that are identified by the the pointcut.
 * **user_exceptions** - Any code that is specified with this pointcut will call Automon's code that specifies an exception has been thrown.
 Automon implementations of JAMon, JavaSimon, and Yammer Metrics use this capability to count exceptions.

 Typically the user_monitor and user_exceptions would use the same pointcut though they don't have to.

 The xml files can be very simple. See [hello-world-unwoven-aop.xml](https://github.com/stevensouza/automon/blob/master/examples/config/hello-world-unwoven-aop.xml) for
 a simple example that you can modify to monitor your own programs.  [ajc-aop.xml](https://github.com/stevensouza/automon/blob/master/examples/config/ajc-aop.xml) is a more complete example
 that also has many pointcuts defined and so it should simplify the process of writing pointcuts for you.

 Here is an example of how you would pass this xml configuration information to the AspectJ LTW:

 **java   -Dorg.automon=sysout -Dorg.aspectj.weaver.loadtime.configuration=file:config/ajc-aop.xml -javaagent:libs/aspectjweaver.jar -classpath libs/automon-1.0.jar:libs/playground-1.0.jar com.stevesouza.automon.annotations.AnnotationTester**

Here is a short video that shows how to monitor your code using Automon with LTW: [Automon demo](http://youtu.be/RdR0EdezS74)
