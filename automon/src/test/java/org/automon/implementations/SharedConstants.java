package org.automon.implementations;

import org.automon.utils.Utils;

import java.sql.SQLException;

/**
 * Monitoring label and exception shared in implementation tests.
 *
 * @author stevesouza
 */
public interface SharedConstants {

    String LABEL = "execution(void com.stevesouza.helloworld.HelloWorld.my_method-1(String[],    Object[][]))";
    Exception EXCEPTION = new SQLException("Login failure", "8001", 5501, new RuntimeException("my exception"));
    String EXCEPTION_LABEL = Utils.getLabel(EXCEPTION);

}
