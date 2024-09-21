package org.automon.aspects.tracing;

import org.apache.logging.log4j.core.*;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.impl.Log4jLogEvent;
import org.apache.logging.log4j.message.SimpleMessage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A custom Log4j 2 appender that stores log events in a list.
 * <p>
 * This appender is primarily intended for testing purposes, allowing you to capture and
 * verify log messages generated during unit tests.
 */
@Plugin(name = "ListAppender", category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE, printObject = true)
public class ListAppender extends AbstractAppender {

    private final List<LogEvent> events = new ArrayList<>();

    /**
     * Constructs a new ListAppender with the given name, filter, layout, ignoreExceptions flag, and properties.
     *
     * @param name            The name of the appender.
     * @param filter          The filter to apply to log events (optional).
     * @param layout          The layout to use for formatting log events (optional).
     * @param ignoreExceptions Whether the appender should ignore exceptions during appending.
     * @param properties      Additional properties for the appender.
     */
    protected ListAppender(String name, Filter filter, Layout<? extends Serializable> layout,
                           boolean ignoreExceptions, Property[] properties) {
        super(name, filter, layout, ignoreExceptions, properties);
    }

    /**
     * Factory method for creating ListAppender instances.
     *
     * @param name            The name of the appender.
     * @param ignoreExceptions Whether the appender should ignore exceptions during appending.
     * @param layout          The layout to use for formatting log events (optional).
     * @param filter          The filter to apply to log events (optional).
     * @param properties      Additional properties for the appender (optional).
     * @return A new ListAppender instance.
     */
    @PluginFactory
    public static ListAppender createAppender(
            @PluginAttribute("name") String name,
            @PluginAttribute("ignoreExceptions") boolean ignoreExceptions,
            @PluginElement("Layout") Layout layout,
            @PluginElement("Filter") Filter filter,
            @PluginElement("Properties") Property[] properties) {
        return new ListAppender(name, filter, layout, ignoreExceptions, properties);
    }

    /**
     * Appends a log event to the internal list. Note a new Log4JLogEvent is made as the LogEvent passed
     * just contains the message from the log line (such as logger.info("just this") and it does not
     * contain the extra info such as NDC, MDC from the log4j2-test.xml file.
     *
     * @param event The log event to append.
     */
    @Override
    public void append(LogEvent event) {
        Layout<? extends Serializable> layout = getLayout();
        if (layout != null) { // Check if layout is configured
            String formattedMessage = (String) layout.toSerializable(event);
            // format the raw message with the ListAppender layout.
            LogEvent formattedEvent  = new Log4jLogEvent.Builder(event).
                    setMessage(new SimpleMessage(formattedMessage)).
                    build();
            events.add(formattedEvent.toImmutable()); // Store an immutable copy of the event
        } else {
            events.add(event.toImmutable()); // Store an immutable copy of the event
        }

    }

    /**
     * Gets the list of captured log events.
     *
     * @return The list of log events.
     */
    public List<LogEvent> getEvents() {
        return events;
    }

    /**
     * Clears the list of captured log events.
     */
    public void clear() {
        events.clear();
    }
}