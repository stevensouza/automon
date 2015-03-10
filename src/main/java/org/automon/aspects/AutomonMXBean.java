package org.automon.aspects;



/**
 * Created by stevesouza on 3/9/15.
 */
public interface AutomonMXBean {
    public boolean isEnabled();
    public void setOpenMon(String openMon);
    public String getOpenMon();
    public String getValidOpenMons();
}
