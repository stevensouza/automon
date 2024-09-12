Maven module that generates a regular java class that is used in the 'examples' directory to demonstrate Load Time Weaving (LTW).

The shaded/fat jar includes slf4j and log4j2 and so can be used with any of the 
Tracing aspects.  You can run this program in various ways.
- mvn exec:java
- java -jar target/helloworld_unwoven-1.0.4-SNAPSHOT-shaded.jar 
  - replace '1.0.4-SNAPSHOT' with current version
- In various scripts that use this jar as a basis for load time weaving (LTW). For example
  hello-world-tracebasiccontext-ltw.sh.
- In various maven modules of this project.