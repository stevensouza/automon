#!/bin/sh
#/Applications/myapps/jetty/jetty-distribution-9.2.6.v20141205/bin/jetty.sh start
#!/bin/sh
export JETTY_HOME=/Applications/myapps/jetty/jetty-distribution-9.2.1.v20140609
export JAVA_OPTIONS=' -server -Xms256m -Xmx512m -Dorg.aspectj.weaver.loadtime.configuration=file:/Applications/myapps/jetty/jetty-distribution-9.2.1.v20140609/resources/automon-aop.xml -javaagent:/Applications/myapps/jetty/jetty-distribution-9.2.1.v20140609/resources/aspectjweaver-${ASPECTJ_VERSION}.jar '
$JETTY_HOME/bin/jetty.sh start
