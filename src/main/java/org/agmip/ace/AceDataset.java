package org.agmip.ace;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.agmip.ace.util.AceFunctions;
/**
 * A container class that holds the complete set of {@link AceExperiment}s,
 * {@link AceSoil}s and {@link AceWeather}s and handles the mapping
 * between these components.
 */
public class AceDataset {
    private Map<String, AceWeather> weatherMap;
    private Map<String, AceSoil> soilMap;
    private Map<String, AceExperiment> experimentMap;
    private Map<String, String> widMap;
    private Map<String, String> sidMap;

    private byte majorVersion = 0;
    private byte minorVersion = 0;
    private byte revision = 0;

    /**
     * Create a blank dataset
     */
    public AceDataset() {
        this.weatherMap = new HashMap<>();
        this.soilMap    = new HashMap<>();
        this.experimentMap = new LinkedHashMap<>();
        this.widMap = new HashMap<>();
        this.sidMap = new HashMap<>();
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
    
    /**
     * Add a Weather Station to the dataset.
     * <p>
     * Add a new Weather Station from a {@code byte[]} consisting of the JSON
     * for that weather station data.
     *
     * @param source JSON weather station data
     */
    public void addWeather(byte[] source) throws IOException {
        AceWeather weather = new AceWeather(source);
        String wstId = weather.getValue("wst_id");
        this.weatherMap.put(weather.getId(), weather);
        this.widMap.put(wstId, weather.getId());
    }

    /**
     * Add a Soil to the dataset.
     * <p>
     * Add a new Soil from a {@code byte[]} consisting of the JSON
     * for that soil data.
     *
     * @param source JSON soil data
     */
    public void addSoil(byte[] source) throws IOException {
        AceSoil soil = new AceSoil(source);
        String soilId = soil.getValue("soil_id");
        this.soilMap.put(soil.getId(), soil);
        this.sidMap.put(soilId, soil.getId());
    }

    /**
     * Add an Experiment to the dataset.
     * <p>
     * Add a new Experiment from a {@code byte[]} consisting of the JSON
     * for that experiment data.
     *
     * @param source JSON experiment data
     */
    public void addExperiment(byte[] source) throws IOException {
        AceExperiment experiment = new AceExperiment(source);
        this.experimentMap.put(experiment.getId(), experiment);
    }

    /**
     * Return a list of all Weather Stations.
     * 
     * @return a list of {@link AceWeather}
     */
     public List<AceWeather> getWeathers() {
        return new ArrayList<AceWeather>(this.weatherMap.values());
    }
    
    /**
     * Return a list of all Weather Stations as {@link IAceBaseComponent}s.
     *
     * @return a list of {@link IAceBaseComponent} for weathers.
     */
    public List<IAceBaseComponent> getWeatherComponents() {
        return new ArrayList<IAceBaseComponent>(this.weatherMap.values());
    }
    

    /**
     * Return the original Weather Station mapping.
     * <p>
     * This returns the original binding information ({@code wst_id}) for
     * all weather stations. This should not be used by developers
     * of translators, instead use {@code AceExperiment#getWeather}.
     *
     * @return a map of all Weather Stations to {@code wst_id}
     */
    public Map<String, AceWeather> getWeatherMap() {
        return this.weatherMap;
    }

    /**
     * Return a list of all Soil Profiles.
     *
     * @return a list of {@link AceSoil}s
     */
    public List<AceSoil> getSoils() {
        return new ArrayList<AceSoil>(this.soilMap.values());
    }
    
    /**
     * Return a list of all Soil Profiles as {@link IAceBaseComponent}s.
     *
     * @return a list of {@link IAceBaseComponent} for soils
     */
    public List<IAceBaseComponent> getSoilComponents() {
        return new ArrayList<IAceBaseComponent>(this.soilMap.values());
    }

    /**
     * Return the original Soil Profile map.
     * <p>
     * This returns the original binding information ({@code soil_id} for
     * all soil profiles. This should not be used by translator developers,
     * instead user {@code AceExperiment#getSoil}.
     *
     * @return a map of all Soil Profiles to {@code soil_id}
     */
    public Map<String, AceSoil> getSoilMap() {
        return this.soilMap;
    }
    
    /**
     * Return a list of all Experiments.
     *
     * @return a list of {@link AceExperiment}s
     */
    public List<AceExperiment> getExperiments() {
        return new ArrayList<AceExperiment>(this.experimentMap.values());
    }
    
    /**
     * Return a list of all Experiments as {@link IAceBaseComponent}s.
     *
     * @return a list of {@link IAceBaseComponent}s for experiments
     */
    public List<IAceBaseComponent> getExperimentComponents() {
        return new ArrayList<IAceBaseComponent>(this.experimentMap.values());
    }
    
    /**
     * Return the original Experiment map.
     * <p>
     * This returns the original binding information ({@code exname}) for
     * all experiment. This should not be used by translator developers.
     * instead use {@link #getExperiments}.
     *
     * @return a map of all Experiments to {@code exname}
     */
    public Map<String, AceExperiment> getExperimentMap() {
        return this.experimentMap;
    }
    

    /**
     * Link all Experiments to the assocaited Soil Profile and Weather Station.
     * <p>
     * <strong>NOTE:</strong>This must be called prior to calling
     * {@link AceExperiment#getWeather} or {@link AceExperiment#getSoil} or
     * a {@code NullPointerException} will be thrown.
     * <p>
     * Loops through all the experiments to link the Soil Profile and Weather
     * station to each experiment. It first attempts to bind to the {@code wid}
     * or {@code sid} of the experiment, falling back on the {@code wst_id}
     * or {@code soil_id}. If no association is found,
     * {@link AceFunctions#getBlankComponent} is called.
     */
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
                String wstId = e.getValue("wst_id");
                wid = this.widMap.get(wstId);

                if (wstId != null && wid != null) {
                    e.setWeather(this.weatherMap.get(wid));
                } else {
                    e.setWeather(new AceWeather(AceFunctions.getBlankComponent()));
                }
            }

            if (sid != null) {
                AceSoil s = this.soilMap.get(sid);
                if (s != null) {
                    e.setSoil(s);
                } else {
                    e.setSoil(new AceSoil(AceFunctions.getBlankComponent()));
                }
            } else {
                String soilId = e.getValue("soil_id");
                sid = this.sidMap.get(soilId);

                if (soilId != null && sid != null) {
                    e.setSoil(this.soilMap.get(sid));
                } else {
                    e.setSoil(new AceSoil(AceFunctions.getBlankComponent()));
                }
            }
        }
    }

    /**
     * Fix Experiment to Soil Profile/Weather Station mapping after updates.
     * <p>
     * <strong>NOTE:</strong> This must be called after updating values which
     * will affect the hash.
     * <p>
     * <strong>NOTE:</strong> This method is called prior to generating a JSON
     * using any method found in {@link org.agmip.ace.io.AceGenerator}.
     * <p>
     * Re-associates and regenerates all ID's based on the hash for each
     * container.
     */
    public void fixAssociations() throws IOException {
        for (AceWeather w : this.getWeathers()) {
            w.getId(true);
        }
        for (AceSoil s: this.getSoils()) {
            s.getId(true);
        }
        // Reassociate the ids and regenerate the experiment ID
        for (AceExperiment e: this.getExperiments()) {
            String wid = e.getWeather().getId();
            String sid = e.getSoil().getId();
            e.update("wid", wid, true);
            e.update("sid", sid, true);
            e.getId(true);
        }
    }
}
