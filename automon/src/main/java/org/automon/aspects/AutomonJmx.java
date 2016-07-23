/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.automon.aspects;

/**
    // Note the mxbean was done as an inner class due to compilation order and AutomonAspect.aj not being compiled and so
    // not available to Automon if it was an external class.  These methods are visible via the jconsole jmx console.
 *
 * @author stevesouza
 */
public class AutomonJmx  implements AutomonMXBean {
       
        private AutomonAspectBase automonAspectBase;
        public AutomonJmx(AutomonAspectBase automonAspectBase) {
            this.automonAspectBase = automonAspectBase;
        }

        @Override
        public boolean isEnabled() {
            return automonAspectBase.isEnabled();
        }

        @Override
        public void setOpenMon(String openMonKey) {
            automonAspectBase.setOpenMon(openMonKey);
        }

        @Override
        public String getOpenMon() {
            return automonAspectBase.getOpenMon().toString();
        }

        @Override
        public String getValidOpenMons() {
            return automonAspectBase.getOpenMonFactory().toString();
        }
}
