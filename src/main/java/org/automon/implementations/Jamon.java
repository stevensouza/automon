package org.automon.implementations;

import com.jamonapi.MonKey;
import com.jamonapi.MonKeyImp;
import com.jamonapi.Monitor;
import com.jamonapi.MonitorFactory;
import org.aspectj.lang.JoinPoint;
import org.automon.utils.AutomonExpirable;
import org.automon.utils.Utils;

import java.util.List;

/**
 * Created by stevesouza on 2/26/15.
 */
public class Jamon extends OpenMonBase<Monitor> {

    @Override
    public Monitor start(JoinPoint.StaticPart  jp) {
        return MonitorFactory.start(jp.toString());
    }

    @Override
    public void stop(Monitor mon) {
        mon.stop();
    }


    @Override
    public void stop(Monitor mon, Throwable throwable) {
        mon.stop();
        put(throwable);
        mon.getMonKey().setDetails(get(throwable));
    }

    @Override
    protected void trackException(JoinPoint jp, Throwable throwable) {
        AutomonExpirable exceptionContext = populateArgNamesAndValues_InExceptionContext(jp, throwable);
        List<String> labels = getLabels(throwable);
        for (String label : labels) {
            MonKey key = new MonKeyImp(label, exceptionContext, "Exception");
            MonitorFactory.add(key, 1);
        }
    }

    private AutomonExpirable populateArgNamesAndValues_InExceptionContext(JoinPoint jp, Throwable throwable) {
        AutomonExpirable exceptionContext = get(throwable);
        if (exceptionContext.getArgNamesAndValues()==null){
            exceptionContext.setArgNamesAndValues(Utils.getArgNameValuePairs(jp));
        }

        return exceptionContext;
    }

}
