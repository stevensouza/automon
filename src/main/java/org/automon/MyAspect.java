package org.automon;

import org.automon.implementations.Jamon;
import org.automon.implementations.Metrics;
import org.automon.implementations.NullImp;
import org.automon.implementations.JavaSimon;

/**
 * Created by stevesouza on 2/26/15.
 */
public class MyAspect {
    private OpenMon openMon;

    public OpenMon getOpenMon() {
        return openMon;
    }

    public void setOpenMon(OpenMon openMon) {
        this.openMon = openMon;
    }

    public void enable(boolean enable) {
        if (openMon==null)
            return;

        openMon.enable(enable);
    }

    public boolean isEnabled() {
        return (openMon != null) && openMon.isEnabled();
    }

    // && if (isEnabled())
    public void aroundAdvice() throws Exception {
        Object mon = openMon.start("com.stevesouza.myMethod");
        Thread.sleep(100);
        openMon.stop(mon);
    }

    // && if (isEnabled())

    public void afterThrowingAdvice() throws Exception {
        openMon.exception("com.stevesouza.myException");
    }

    public static void main(String[] args) throws Exception {
        MyAspect myAspect=new MyAspect();

        System.out.println("\nJamon");
        myAspect.setOpenMon(new Jamon());
        myAspect.aroundAdvice();
        myAspect.afterThrowingAdvice();

        System.out.println("\nJavasimon");
        myAspect.setOpenMon(new JavaSimon());
        myAspect.aroundAdvice();
        myAspect.afterThrowingAdvice();

        System.out.println("\nMetrics");
        myAspect.setOpenMon(new Metrics());
        myAspect.aroundAdvice();
        myAspect.afterThrowingAdvice();

        System.out.println("\nNull Implementation (noop)");
        myAspect.setOpenMon(new NullImp());
        myAspect.aroundAdvice();
        myAspect.afterThrowingAdvice();

        System.out.println("\nJamon disabled");
        myAspect.enable(false);
        myAspect.setOpenMon(new Jamon());
        myAspect.aroundAdvice();
        myAspect.afterThrowingAdvice();
    }
}
