This project uses Build Time Weaving (BTW) to put monitoring code in your java classes. To use the more typical Spring AOP monitoring 
look at the [Spring AOP Module](https://github.com/stevensouza/automon/tree/master/spring_aop).
- mvn exec:java 
  - Runs with aspects
- java -Dorg.automon=sysout -jar target/spring_woven-2.0.0-SNAPSHOT-shaded.jar
- examples directory:
  - ./spring-woven.sh