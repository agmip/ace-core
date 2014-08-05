package org.agmip.ace.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.agmip.ace.AceComponent;
import org.agmip.ace.AceComponentType;
import org.agmip.ace.AceEvent;
import org.agmip.ace.AceEventType;
import org.agmip.ace.AceExperiment;
import org.agmip.ace.IAceBaseComponent;
import org.agmip.ace.lookup.LookupPath;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.common.hash.Hasher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AceFunctions {
    private static final HashFunction hf = Hashing.sha256();
    private static final Logger LOG = LoggerFactory.getLogger(AceFunctions.class);
    // DO NOT INSTATIATE THIS CLASS
    private AceFunctions() {
    }

    public static String generateId(IAceBaseComponent source) throws IOException {
        // First check if there is an Id already in place.
        String id = source.getId(false);
        if (id == null) {
            // Get the sha256 hash of the byte array
            return source.getId(true);
        } else {
            return id;
        }
    }

    public static String generateId(byte[] source) throws IOException {
        return generateHCId(source).toString();
    }

    public static HashCode generateHCId(byte[] first, byte[] second) {
        return hf.newHasher().putBytes(first).putBytes(second).hash();
    }

    public static HashCode generateHCId(byte[] source) throws IOException {
        JsonParser    p        = JsonFactoryImpl.INSTANCE.getParser(source);
        JsonToken     t        = p.nextToken();
        List<String>  ordering = new ArrayList<>();
	
        while (t != null) {
            String currentName = p.getCurrentName();
            if (t == JsonToken.VALUE_STRING && currentName != null && (! currentName.startsWith("~") && ! currentName.endsWith("~") && Collections.binarySearch(LookupPath.INSTANCE.getHashFilter(), currentName) < 0)) {
                ordering.add(currentName);
            }
            t = p.nextToken();
        }
        p.close();
        Collections.sort(ordering);
        Hasher hasher = hf.newHasher();
        AceComponent c = new AceComponent(source);
        for(String key : ordering) {
            String combined = key+c.getValue(key);
            hasher.putBytes(combined.getBytes("UTF-8"));
        }
        return hasher.hash();
    }

    public static byte[] removeKeys(IAceBaseComponent source, List<String> keysToRemove) throws IOException {
        return removeKeys(source.getRawComponent(), keysToRemove);
    }

    public static byte[] removeKeys(byte[] source, List<String> keys) throws IOException {
        Collections.sort(keys);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        JsonParser p = JsonFactoryImpl.INSTANCE.getParser(source);
        JsonGenerator g = JsonFactoryImpl.INSTANCE.getGenerator(out);
        JsonToken t = p.nextToken();

        while(t != null) {
            String currentName = p.getCurrentName();
            if (currentName == null || Collections.binarySearch(keys, currentName) < 0) {
                g.copyCurrentEvent(p);
            }
            t = p.nextToken();
        }
        p.close();
        g.flush();
        g.close();
        return out.toByteArray();
    }

    public static AceComponentType getComponentTypeFromKey(String key) {
        String path = LookupPath.INSTANCE.getPath(key);
        if (path == null) {
        	return AceComponentType.ACE_EXPERIMENT;
        }
        if (path.contains("@")) {
            if (path.contains("events")) {
                return AceComponentType.ACE_EVENT;
            } else {
                return AceComponentType.ACE_RECORD;
            }
        } else if (path.contains("initial")) {
            return AceComponentType.ACE_INITIALCONDITIONS;
        } else if (path.contains("weather")) {
            return AceComponentType.ACE_WEATHER;
        } else if (path.contains("soil")) {
            return AceComponentType.ACE_SOIL;
        } else {
            return AceComponentType.ACE_EXPERIMENT;
        }
    }

    public static AceComponentType getBaseComponentTypeFromKey(String key) {
        String path = LookupPath.INSTANCE.getPath(key);
        if (path == null) {
        	return AceComponentType.ACE_EXPERIMENT;
        }
        if (path.contains("weather")) {
            return AceComponentType.ACE_WEATHER;
        } else if (path.contains("soil")) {
            return AceComponentType.ACE_SOIL;
        } else {
            return AceComponentType.ACE_EXPERIMENT;
        }
    }

    public static byte[] getBlankComponent() throws IOException {
        return "{}".getBytes("UTF-8");
    }

    public static byte[] getBlankSeries() throws IOException {
        return "[]".getBytes("UTF-8");
    }

    public static String deepGetValue(AceExperiment exp, String var) {
        AceComponent haystack = navigateToComponent(exp, var);
        if(var.equals("wid") || var.equals("sid")) {
            haystack = exp; // Pull it from the primary Experiment
        }
        if (haystack == null) {
            // Shouldn't happen, but log this
            LOG.error("Invalid haystack provided to deepGetValue() while looking for {}", var);
            return null;
        } else {
            if (haystack.componentType == AceComponentType.ACE_EVENT && LookupPath.INSTANCE.isDate(var)) {
                var = "date";
            }
            try {
                return haystack.getValue(var);
            } catch(IOException ex) {
                // IOException error, need to log
                return null;
            }
        }
    }

    public static AceComponent navigateToComponent(AceExperiment exp, String var) {
        String path = LookupPath.INSTANCE.getPath(var);
        if (path == null) {
            path = "";
        }
        try {
            AceComponent haystack = null;
            if (path.contains("@")) {
                if (path.contains("!")) {
                    String[] splitPath = path.split("!");
                    if (splitPath.length != 2) {
                        // This is an error.
                        return null;
                    } else {
                        if (LookupPath.INSTANCE.isDate(var)) {
                            var = "date";
                        }
                        List<AceEvent> events = exp.getEvents()
                            .filterByEvent(AceEventType.valueOf("ACE_"+splitPath[1].toUpperCase()+"_EVENT"))
                            .asList();
                        if (!events.isEmpty()) {
                            haystack = events.get(0);
                        } else {
                            return null;
                        }
                    }
                } else {
                    // This is currently nested and we will not check it
                    return null;
                }
            } else {
                if (path.contains("soil")) {
                    haystack = exp.getSoil();
                } else if (path.contains("weather")) {
                    haystack = exp.getWeather();
                } else if (path.contains("initial")) {
                    haystack = exp.getInitialConditions();
                } else if (path.contains("observed")) {
                    haystack = exp.getOberservedData();
                } else {
                    haystack = exp;
                }
            }
            //LOG.debug("Haystack found: {}", haystack);
            return haystack;
        } catch (IOException ex) {
            // Need to log the error here.
            return null;
        }
    }
}
