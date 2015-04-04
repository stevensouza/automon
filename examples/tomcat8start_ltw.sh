#!/bin/sh
export CATALINA_HOME=/Applications/myapps/tomcat/apache-tomcat-8.0.8
export ASPECTJ_HOME=$CATALINA_HOME

export CATALINA_OPTS=' -server -Xms256m -Xmx512m -Dorg.aspectj.weaver.loadtime.configuration=file:$ASPECTJ_HOME/conf/automon-aop.xml -javaagent:$ASPECTJ_HOME/lib/aspectjweaver.jar '
# export CATALINA_OPTS='-server -Xms256m -Xmx512m'
$CATALINA_HOME/bin/startup.sh
