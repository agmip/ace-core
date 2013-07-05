package org.agmip.ace;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.agmip.ace.util.AceFunctions;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.google.common.collect.Lists;

public class AceWeather extends AceComponent implements IAceBaseComponent {
    private String wid;
    private AceRecordCollection dailyWeather = null;
    private List<String> recordYears;
    private List<String> missingDates;
    private DateTimeFormatter agmipDateFormat = DateTimeFormat.forPattern("yyyyMMdd");

    public AceWeather(byte[] source) throws IOException {
        super(source);
        this.wid = this.getValue("wid");
        if(this.wid == null) {
            this.update("wid", AceFunctions.generateId(source), true);
        }
        this.componentType = AceComponentType.ACE_WEATHER;
    }

    public String getId() {
        return this.wid;
    }
    
    public AceComponentType getComponentType() {
        return this.componentType;
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
    
    @Override
    public AceWeather update(String key, String newValue, boolean addIfMissing) throws IOException {
        super.update(key, newValue, addIfMissing);
        if (key == "wid") {
            this.wid = newValue;
        } else {
            List<String> id = Lists.newArrayList("wid");
            this.component = AceFunctions.removeKeys(this.component, id);
            this.update("wid", AceFunctions.generateId(this.component), true);
            this.hasUpdate = true;
        }
        return this;
    }
}
