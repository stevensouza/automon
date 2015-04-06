# Running Automon in a web container
This directory contains a maven project that will build a war. Follow the steps below to monitor the web application.
Install the web application like you would any other war in your web container.
It has been tested with Jetty and Tomcat, but should also work with other containers.  From a browser go to the following link
 'http://localhost:8080/automon_demo/' (replacing localhost:8080 with your server and port information if it is different).  This page
 does the following:

* Makes http requests,
* Makes calls to IO classes,
* Executes sql calls via jdbc,
* Throws exceptions.

To see the code that is executed refer to [MyServlet](https://github.com/stevensouza/automon/blob/master/webapp_unwoven/src/main/java/com/stevesouza/webapp/MyServlet.java)

To monitor these calls with Automon simply modify the startup scripts to your web container.  Here are sample scripts for
[Jetty](https://github.com/stevensouza/automon/blob/master/examples/jettystart_ltw.sh) and [Tomcat](https://github.com/stevensouza/automon/blob/master/examples/tomcat8start_ltw.sh)
that you can use as a starting point for monitoring your own web applications in any web container. The pointcuts in this
 [AspectJ config file](https://github.com/stevensouza/automon/blob/master/examples/config/automon-aop.xml) can be modified to specify
 what to monitor.

The following screen snapshot shows what is monitored for the 'automon_demo' war using 'automon-aop.xml'. The data was captured
using JAMon, however any of the supported Automon monitoring APIs will also work.
![automon_demo](https://github.com/stevensouza/automon/blob/master/docs/automon_demo.png)
