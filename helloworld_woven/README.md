Maven module that generates a java class that has monitoring woven into the classes to demonstrate BTW (Build Time Weaving).  It can be run from the 'examples' directory.
- mvn exec:java
  - Runs with: org.automon=sysout
- shaded jar
  - java -jar -Dorg.automon=sysout  target/helloworld_woven-1.0.4-SNAPSHOT-shaded.jar 
  - java -jar -Dorg.automon=sysout  target/helloworld_woven-1.0.4-SNAPSHOT-shaded.jar 10
- examples dir 
  - ./hello-world-woven.sh