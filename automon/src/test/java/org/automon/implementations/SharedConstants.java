package org.automon.implementations;

import java.sql.SQLException;
import org.automon.utils.Utils;

/**
 * Monitoring label and exception shared in implementation tests.
 * @author stevesouza
 */
public interface SharedConstants {
    
   public static final String LABEL =  "execution(void com.stevesouza.helloworld.HelloWorld.my_method-1(String[],    Object[][]))";
   public static final Exception EXCEPTION = new SQLException("Login failure", "8001", 5501, new RuntimeException("my exception"));
   public static final String EXCEPTION_LABEL = Utils.getLabel(EXCEPTION);
    
}
