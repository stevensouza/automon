#!/bin/sh

. ./set_versions.sh

# Call the function to set the variables
set_versions

export CATALINA_HOME=/Applications/myapps/tomcat/apache-tomcat-8.0.8
export ASPECTJ_HOME=$CATALINA_HOME

export CATALINA_OPTS=' -server -Xms256m -Xmx512m -Dorg.aspectj.weaver.loadtime.configuration=file:$ASPECTJ_HOME/conf/automon-aop.xml -javaagent:$ASPECTJ_HOME/lib/aspectjweaver-${ASPECTJ_VERSION}.jar '
# export CATALINA_OPTS='-server -Xms256m -Xmx512m'
$CATALINA_HOME/bin/startup.sh
