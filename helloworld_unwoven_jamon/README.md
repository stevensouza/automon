Generates a jar that is used to demonstrate using Automon with JAMon. 
It can be executed in the following way. It just bundles the code with jamon but 
doesn't monitor anything with jamon. LTW and BTW can be used against the shaded jar to 
use jamon to monitor the code.
* mvn exec:java
* from the 'examples' directory
  * ./hello-world-jamon-ltw.sh
* fat/shaded jar  
  * java -jar target/helloworld_unwoven_jamon-2.0.0-shaded.jar 
  * java -jar target/helloworld_unwoven_jamon-2.0.0-shaded.jar 10