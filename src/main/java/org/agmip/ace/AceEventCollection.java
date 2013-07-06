package org.agmip.ace;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import org.agmip.ace.util.JsonFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AceEventCollection extends AbstractCollection<AceEvent> {
    private static final Logger log = LoggerFactory.getLogger(AceEventCollection.class);
    private List<AceEvent> events;
    public AceEventCollection(byte[] source) throws IOException {
        super();
        this.events = new ArrayList<AceEvent>();
        this.parseEvents(source);
    }

    public AceEventCollection(List<AceEvent> source) {
        super();
        this.events = source;
    }

    @Override
    public int size() {
        return events.size();
    }

    @Override
    public Iterator<AceEvent> iterator() {
        return events.iterator();
    }
    
    public List<AceEvent> asList() {
        return events;
    }

    public AceEventCollection filterByEvent(AceEventType eventType) {
        List<AceEvent> filteredEvent = new ArrayList<AceEvent>();
        for(AceEvent event : this.events) {
            if(event.getEventType() == eventType) {
                filteredEvent.add(event);
            }
        }
        return new AceEventCollection(filteredEvent);
    }

    public AceEventCollection sort() {
        // Yes this is messy and copying things, but I don't want to
        // modify the current structure.
        List<AceEvent> sortedList = new ArrayList<AceEvent>(this.events);
        Collections.sort(sortedList);
        return new AceEventCollection(sortedList);
    }

    private void parseEvents(byte[] source) throws IOException {
        ByteArrayOutputStream eventStream = new ByteArrayOutputStream();
        JsonParser p = JsonFactoryImpl.INSTANCE.getParser(source);
        JsonToken t;

        t = p.nextToken();
        if (t != JsonToken.START_ARRAY) {
            log.error("Invalid event block found");
            return;
        }
        while (t != null) {
            if (t == JsonToken.START_OBJECT) {
                JsonGenerator g = JsonFactoryImpl.INSTANCE.getGenerator(eventStream);
                g.copyCurrentStructure(p);
                g.flush();
                byte[] event = eventStream.toByteArray();
                g.close();
                this.events.add(new AceEvent(event));
                eventStream.reset();
            }
            t = p.nextToken();
        }
        p.close();
    }
}