package org.agmip.ace;

import java.io.IOException;

public class AceEvent extends AceComponent implements Comparable<AceEvent> {
    private AceEventType eventType;
    private String eventDate;
    
    public AceEvent (String event, String date) throws IOException {
        this(String.format("{\"event\"=\"%1$s\",\"date\"=\"%2$s\"}", event, date).getBytes("UTF-8"));
    }

    public AceEvent(byte[] source) throws IOException {
        super(source);
        this.setEventType();
        this.eventDate = this.getValueOr("date", "");
        this.componentType = AceComponentType.ACE_EVENT;
    }

    public AceEventType getEventType() {
        return this.eventType;
    }

    @Override
    public int compareTo(AceEvent otherEvent) {
        return this.eventDate.compareTo(otherEvent.eventDate);
    }

    @Override
    public String toString() {
        return this.eventDate+": "+this.eventType.toString();
    }

    private void setEventType() throws IOException {
        String event = this.getValue("event");
        if (event.equals("planting")) {
            this.eventType = AceEventType.ACE_PLANTING_EVENT;
        } else if (event.equals("irrigation")) {
            this.eventType = AceEventType.ACE_IRRIGATION_EVENT;
        } else if (event.equals("fertilizer")) {
            this.eventType = AceEventType.ACE_FERTILIZER_EVENT;
        } else if (event.equals("tillage")) {
            this.eventType = AceEventType.ACE_TILLAGE_EVENT;
        } else if (event.equals("organic_matter")) {
            this.eventType = AceEventType.ACE_ORGANIC_MATTER_EVENT;
        } else if (event.equals("harvest")) {
            this.eventType = AceEventType.ACE_HARVEST_EVENT;
        } else if (event.equals("mulch_add")) {
            this.eventType = AceEventType.ACE_MULCH_ADD_EVENT;
        } else if (event.equals("mulch_remove")) {
            this.eventType = AceEventType.ACE_MULCH_REMOVE_EVENT;
        } else if (event.equals("chemical")) {
            this.eventType = AceEventType.ACE_CHEMICAL_EVENT;
        } else {
            this.eventType = AceEventType.ACE_INVALID_EVENT;
        }
    }
}