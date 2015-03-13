package org.automon.pointcuts;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * Standard pointcuts that are useful for typically monitored classes in the jdk.  Typically these would involve IO of
 * one form or another (i.e. files, jdbc, net/web)
 */

@Aspect
public abstract class Jdk {

    @Pointcut(
            "within(java.sql.Statement+) || " +
            "within(java.sql.Connection+)"
    )
    public void jdbc() {
    }

    @Pointcut(
            "within(java.io.Writer+) || " +
            "within(java.io.Reader+) || " +
            "within(java.io.OutputStream+) || " +
            "within(java.io.InputStream+) || " +
            "within(java.io.DataInput+) || " +
            "within(java.io.DataOutput+)"
    )
    public void io() {
    }

    @Pointcut(
            "within(java.net.SocketImpl+) || " +
            "within(java.net.ServerSocket+) || " +
            "within(java.net.DatagramSocket+) || " +
            "within(java.net.DatagramSocketImpl+) || " +
            "within(java.net.Socket+) || " +
            "within(java.net.URLConnection+)"
    )
    public void net() {
    }

}
