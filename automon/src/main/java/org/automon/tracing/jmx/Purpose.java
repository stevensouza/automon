package org.automon.tracing.jmx;

/**
 * This class defines a "purpose" which Automon uses for contextual information
 * within the JMX aspect names. Example of 'purpose' appears in this string:
 *  org.automon:type=aspect,purpose=trace_log_full_context,name=org.automon.AutomonAspect@63f25932
 */
public class Purpose {

    private String purpose;


    /**
     * Constructor that initializes the purpose with the provided value.
     *
     * @param purpose The initial purpose string.
     */
    public Purpose(String purpose) {
        this.purpose = purpose;
    }

    /**
     * Gets the current purpose string.
     *
     * @return The purpose string.
     */
    public String getPurpose() {
        return purpose;
    }

    /**
     * Sets the purpose string.
     *
     * @param purpose The new purpose string to set.
     */
    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }
}