package org.automon.pointcuts;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * 3rd party libraries to simplify writing pointcuts.  Examples of 3rd party libraries would be hadoop, cassandra, hbase
 * or anything else that is provided by a 3rd party.
 */

@Aspect
public abstract class Libraries {
    @Pointcut("execution(public * org.apache.hadoop.hbase.client.Table+.*(..))")
    public void hbase() {

    }
}
