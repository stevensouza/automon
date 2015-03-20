package org.automon.pointcuts;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * <p>Standard pointcuts that are useful for typically monitored classes in the jdk.  Typically these would involve IO of
 * one form or another (i.e. files, jdbc, net/web)</p>
 *
 * <p>The 'execution' pointcut won't work for jdk class pointcuts, so instead all 'call' pointcuts are monitored which should have the same
 * effect.  Note 'call' is not supported in spring though (you can postprocess your spring jar however and let aspectj do the work).  Also note
 * the '+' at the end of the classname will also monitor any calls to any class that inherits from this one.  See AspectJ documentation
 * for more information.
 * </p>
 *
 *
 */

@Aspect
public abstract class Jdk {

    @Pointcut(
            "call(public * java.sql.Statement+.*(..)) || " +
            "call(public * java.sql.Connection+.*(..)) || " +
            "call(public * java.sql.Savepoint+.*(..))"
    )
    public void jdbc() {
    }

    @Pointcut(
            "call(public * java.io.Writer+.*(..)) || " +
            "call(public * java.io.Reader+.*(..)) || " +
            "call(public * java.io.OutputStream+.*(..)) || " +
            "call(public * java.io.InputStream+.*(..)) || " +
            "call(public * java.io.DataInput+.*(..)) || " +
            "call(public * java.io.DataOutput+.*(..))"
    )
    public void io() {
    }

    @Pointcut(
            "call(public * java.net.SocketImpl+.*(..)) || " +
            "call(public * java.net.ServerSocket+.*(..)) || " +
            "call(public * java.net.DatagramSocket+.*(..)) || " +
            "call(public * java.net.DatagramSocketImpl+.*(..)) || " +
            "call(public * java.net.Socket+.*(..)) || " +
            "call(public * java.net.URLConnection+.*(..))"
    )
    public void net() {
    }

}
