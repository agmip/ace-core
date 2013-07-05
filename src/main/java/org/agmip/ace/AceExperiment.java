package org.agmip.ace;

import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.util.List;

import org.agmip.ace.util.AceFunctions;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.google.common.collect.Lists;

public class AceExperiment extends AceComponent implements IAceBaseComponent {
    private String eid;
    private AceWeather weather;
    private AceSoil    soil;
    private AceInitialConditions ic;
    private AceObservedData observed;
    private AceEventCollection events;

    public AceExperiment(byte[] source) throws IOException {
        super(source);
        this.eid = this.getValue("eid");
        this.componentType = AceComponentType.ACE_EXPERIMENT;
        if(this.eid == null) {
            this.update("eid", AceFunctions.generateId(source), true);
        }
        this.extractSubcomponents();
    }

    public String getId() {
        return this.eid;
    }
    
    public AceComponentType getComponentType() {
        return this.componentType;
    }
    
    public AceWeather getWeather() {
        return this.weather;
    }

    public AceSoil getSoil() {
        return this.soil;
    }

    public void setWeather(AceWeather weather) {
        this.weather = weather;
    }

    public void setSoil(AceSoil soil) {
        this.soil = soil;
    }

    public AceInitialConditions getInitialConditions() throws IOException {
        return this.ic;
    }

    public AceObservedData getOberservedData() {
        return this.observed;
    }

    public AceEventCollection getEvents() {
        return this.events;
    }

    private void extractSubcomponents() throws IOException {
        JsonParser p = this.getParser();
        JsonToken t;

        t = p.nextToken();

        while (t != null) {
            String currentName = p.getCurrentName();
            if(currentName != null && t == JsonToken.FIELD_NAME &&
                    (currentName.equals("initial_conditions") ||
                     currentName.equals("observed") ||
                     currentName.equals("events"))) {
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                t = p.nextToken();
                JsonGenerator subcomponent = this.getGenerator(outStream);
                subcomponent.copyCurrentStructure(p);
                subcomponent.flush();
                byte[] out = outStream.toByteArray();
                subcomponent.close();
                if (currentName.equals("initial_conditions")) {
                    this.ic = new AceInitialConditions(out);
                } else if (currentName.equals("observed")) {
                    this.observed = new AceObservedData(out);
                } else if (currentName.equals("events")) {
                    this.events = new AceEventCollection(out);
                }
            } 
            t = p.nextToken();
        }
        p.close();
    }
    
    @Override
    public AceExperiment update(String key, String newValue, boolean addIfMissing) throws IOException {
        super.update(key, newValue, addIfMissing);
        if (key == "eid") {
            this.eid = newValue;
        } else {
            List<String> id = Lists.newArrayList("eid");
            this.component = AceFunctions.removeKeys(this.component, id);
            this.update("eid", AceFunctions.generateId(this.component), true);
            this.hasUpdate = true;
        }
        return this;
    }
}
