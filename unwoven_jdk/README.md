Maven module that generates a java class that interacts with the file system and database (embedded hsqldb).
It is not woven. It can be run in the following ways:
* A script in the examples directory that uses this
with LTW. 
* java -jar target/unwoven_jdk-2.0.0-shaded.jar README.md
* mvn exec:java - will run the code without aspectj.
* From examples directory
  * ./jdk-ltw.sh
* The 'webapp_unwoven' module calls this code from a web app.  If the aspectj javaagent is installed
the code can also use automon aspects from a webserver like tomcat.