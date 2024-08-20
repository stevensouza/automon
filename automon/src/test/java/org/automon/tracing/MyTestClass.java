package org.automon.tracing;

public class MyTestClass {

    public String name() {
        return "name: "+first("steve")+" "+last("souza");
    }
    public String first(String name) {
        return name ;
    }

    public String last(String name) {
        return name;
    }


}
