package org.agmip.ace;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.agmip.ace.util.AceFunctions;

public class AceDataset {
    private Map<String, AceWeather> weatherMap;
    private Map<String, AceSoil> soilMap;
    private Map<String, AceExperiment> experimentMap;
    private byte majorVersion = 0;
    private byte minorVersion = 0;
    private byte revision = 0;

    public AceDataset() {
        this.weatherMap = new HashMap<String, AceWeather>();
        this.soilMap    = new HashMap<String, AceSoil>();
        this.experimentMap = new HashMap<String, AceExperiment>();
    }

    public void setMajorVersion(byte major) {
        this.majorVersion = major;
    }
    
    public void setMinorVersion(byte minor) {
        this.minorVersion = minor;
    }
    
    public void setRevision(byte revision) {
        this.revision = revision;
    }
    
    public byte getMajorVersion() {
        return this.majorVersion;
    }
    
    public byte getMinorVersion() {
        return this.minorVersion;
    }
    
    public byte getRevision() {
        return this.revision;
    }
    
    public String getVersion() {
        return this.majorVersion+"."+this.minorVersion+"."+this.revision;
    }
    
    public void addWeather(byte[] source) throws IOException {
        AceWeather weather = new AceWeather(source);
        this.weatherMap.put(weather.getId(), weather);
    }

    public void addSoil(byte[] source) throws IOException {
        AceSoil soil = new AceSoil(source);
        this.soilMap.put(soil.getId(), soil);
    }

    public void addExperiment(byte[] source) throws IOException {
        AceExperiment experiment = new AceExperiment(source);
        this.experimentMap.put(experiment.getId(), experiment);
    }
    

    public List<AceWeather> getWeathers() {
        return new ArrayList<AceWeather>(this.weatherMap.values());
    }
    
    public List<AceComponent> getWeatherComponents() {
        return new ArrayList<AceComponent>(this.weatherMap.values());
    }
    
    public Map<String, AceWeather> getWeatherMap() {
        return this.weatherMap;
    }

    public List<AceSoil> getSoils() {
        return new ArrayList<AceSoil>(this.soilMap.values());
    }
    
    public List<AceComponent> getSoilComponents() {
        return new ArrayList<AceComponent>(this.soilMap.values());
    }

    public Map<String, AceSoil> getSoilMap() {
        return this.soilMap;
    }
    
    public List<AceExperiment> getExperiments() {
        return new ArrayList<AceExperiment>(this.experimentMap.values());
    }
    
    public List<AceComponent> getExperimentComponents() {
        return new ArrayList<AceComponent>(this.experimentMap.values());
    }
    
    public Map<String, AceExperiment> getExperimentMap() {
        return this.experimentMap;
    }
    
    public void linkDataset() throws IOException {
        for(AceExperiment e : this.getExperiments()) {
            String wid = e.getValue("wid");
            String sid = e.getValue("sid");
            if (wid != null) {
                AceWeather w = this.weatherMap.get(wid);
                if (w != null) {
                    e.setWeather(w);
                } else {
                    e.setWeather(new AceWeather(AceFunctions.getBlankComponent()));
                }
            } else {
                e.setWeather(new AceWeather(AceFunctions.getBlankComponent()));
            }

            if (sid != null) {
                AceSoil s = this.soilMap.get(sid);
                if (s != null) {
                    e.setSoil(s);
                } else {
                    e.setSoil(new AceSoil(AceFunctions.getBlankComponent()));
                }
            } else {
                e.setSoil(new AceSoil(AceFunctions.getBlankComponent()));
            }
        }
    }
}
