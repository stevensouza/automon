# Using Automon With Spring
Note it is important to inherit from the annotation style aspects in order for them to work properl in Spring.  
The spring aspects to inherit from are in the package 'org.automon.tracing.spring.*' (not 'org.automon.tracing.aspectj.*')
This maven module creates a simple Spring program with one Spring bean and monitors and traces it.  You can running the program by:
* Check out the code and run: mvn clean package
* From the 'examples' directory run: ./spring-aop.sh
* From shaded jar (this is done from spring-aop.sh): java  -Dorg.automon="sysout" -jar ../spring_aop/target/spring_aop-${AUTOMON_VERSION}-shaded.jar
* Alternatively from the 'spring_aop' directory run: 
  * mvn exec:java 
    * defaults to -Dorg.automon="sysout" 
  * mvn -Dorg.automon="sysout" exec:java 
* java -Dorg.automon="sysout" -jar target/spring_aop-1.0.4-SNAPSHOT-shaded.jar

What you monitor is defined in the [Spring applicationContext.xml](https://github.com/stevensouza/automon/blob/master/spring_aop/src/main/resources/applicationContext.xml) file.
