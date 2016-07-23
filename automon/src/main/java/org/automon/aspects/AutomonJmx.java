/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.automon.aspects;

import org.automon.aspects.AutomonMXBean;

/**
    // Note the mxbean was done as an inner class due to compilation order and AutomonAspect.aj not being compiled and so
    // not available to Automon if it was an external class.  These methods are visible via the jconsole jmx console.
 *
 * @author stevesouza
 */
public class AutomonJmx  implements AutomonMXBean {
       
        private AutomonSpringAspect automonAspect;
        public AutomonJmx(AutomonSpringAspect automonAspect) {
            this.automonAspect = automonAspect;
        }

        @Override
        public boolean isEnabled() {
            return automonAspect.isEnabled();
        }

        @Override
        public void setOpenMon(String openMonKey) {
            automonAspect.setOpenMon(openMonKey);
        }

        @Override
        public String getOpenMon() {
            return automonAspect.getOpenMon().toString();
        }

        @Override
        public String getValidOpenMons() {
            return automonAspect.getOpenMonFactory().toString();
        }
}
