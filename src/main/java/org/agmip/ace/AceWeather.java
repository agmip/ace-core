package org.agmip.ace;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.agmip.ace.util.AceFunctions;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.google.common.hash.HashCode;

public class AceWeather extends AceComponent implements IAceBaseComponent {
    private String wid;
    private AceRecordCollection dailyWeather = null;
    private List<String> recordYears;
    private List<String> missingDates;
    private DateTimeFormatter agmipDateFormat = DateTimeFormat.forPattern("yyyyMMdd");

    public AceWeather() throws IOException {
        this(AceFunctions.getBlankComponent());
    }

    public AceWeather(byte[] source) throws IOException {
        super(source);
        this.getDailyWeather();
        this.extractSubcomponents();
        this.wid = this.getValue("wid");
        if(this.wid == null) {
            this.getId(true);
        }
        this.componentType = AceComponentType.ACE_WEATHER;
    }

    public String getId(boolean forceRegenerate) throws IOException {
        if (forceRegenerate || this.wid == null) {
            this.wid = this.generateId();
            this.update("wid", this.wid, true);
        }
        return this.wid;
    }

    public String getId() throws IOException {
        return this.getId(false);
    }

    public String generateId() throws IOException {
        HashCode currentHash = this.getRawComponentHash();
        for (AceRecord r: this.getDailyWeather()) {
            currentHash = AceFunctions.generateHCId(currentHash.asBytes(), r.getRawComponentHash().asBytes());
        }
        return currentHash.toString();
    }

    public boolean validId() throws IOException {
        if (this.wid == null) return false;
        return this.wid.equals(this.generateId());
    }

    public AceComponentType getComponentType() {
        return this.componentType;
    }

    private void extractSubcomponents() throws IOException {
        ByteArrayOutputStream baseOut = new ByteArrayOutputStream();
        JsonParser p = this.getParser();
        JsonGenerator g = this.getGenerator(baseOut);
        JsonToken t;

        t = p.nextToken();

        while (t != null) {
            String currentName = p.getCurrentName();
            if(currentName != null && t == JsonToken.FIELD_NAME &&
                    currentName.equals("dailyWeather")) {
                p.nextToken();
                p.skipChildren();
            } else {
                g.copyCurrentEvent(p);
            }
            t = p.nextToken();
        }
        p.close();
        g.flush();
        g.close();
        this.component = baseOut.toByteArray();
        this.getId(true);
        baseOut = null;
    }

    public AceRecordCollection getDailyWeather() throws IOException {
        if (this.dailyWeather == null) {
            this.dailyWeather = this.getRecords("dailyWeather");
        }
        return this.dailyWeather;
    }

    public List<String> getRecordYears() throws IOException {
        if (this.recordYears == null) {
            AceRecordCollection daily = getDailyWeather();
            this.recordYears = new ArrayList<String>();
            for(AceRecord r : daily) {
                String date = r.getValue("w_date");
                if (date != null) {
                    String year = date.substring(0, 4);
                    if (! recordYears.contains(year)) {
                        recordYears.add(year);
                    }
                }
            }
        }
        return this.recordYears;
    }

    public List<String> getMissingDates() throws IOException {
        // TODO: This needs to be memoized... we shouldn't calculate this every time.
        this.missingDates = new ArrayList<String>();
        Iterator<AceRecord> i = this.getDailyWeather().iterator();
        AceRecord r;
        DateTime currentDate;

        if (i.hasNext()) {
            r = i.next();
            currentDate = agmipDateFormat.parseDateTime(r.getValue("w_date"));
            while(i.hasNext()) {
                r = i.next();
                DateTime nextDate = agmipDateFormat.parseDateTime(r.getValue("w_date"));
                currentDate = currentDate.plusDays(1);
                while(! currentDate.equals(nextDate)) {
                    this.missingDates.add(currentDate.toString(agmipDateFormat));
                    currentDate = currentDate.plusDays(1);
                }
            }
        }
        return this.missingDates;
    }

    public byte[] rebuildComponent() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        JsonParser p = this.getParser();
        JsonGenerator g = this.getGenerator(bos);
        JsonToken t = p.nextToken();
        while ( t != null) {
            if (t == JsonToken.END_OBJECT) {
                g.writeArrayFieldStart("dailyWeather");
                for(AceRecord r: this.getDailyWeather()) {
                    g.writeRawValue(new String(r.getRawComponent(), "UTF-8"));
                }
                g.writeEndArray();
            }
            g.copyCurrentEvent(p);
            t = p.nextToken();
        }
        p.close();
        g.flush();
        g.close();
        bos.close();
        if (bos.size() == 0) {
            return AceFunctions.getBlankComponent();
        } else {
            return bos.toByteArray();
        }
    }
}
