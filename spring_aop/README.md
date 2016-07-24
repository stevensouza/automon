# Using Automon With Spring
This maven module creates a simple Spring program with one Spring bean and monitors it.  You can running the program by:
* Check out the code and run: mvn clean package
* From the 'examples' directory run: . spring-aop.sh
* Alternativey from the 'spring_aop' directory run: mvn -Dorg.automon="sysout" exec:java -Dexec.mainClass="org.automon.spring_aop.SpringMain" -Dexec.classpathScope=runtime -X

What you monitor is defined in the [Spring applicationContext.xml](https://github.com/stevensouza/automon/blob/master/spring_aop/src/main/resources/applicationContext.xml) file.
