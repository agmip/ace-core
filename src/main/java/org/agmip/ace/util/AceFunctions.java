package org.agmip.ace.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.agmip.ace.AceComponentType;
import org.agmip.ace.IAceBaseComponent;
import org.agmip.ace.lookup.LookupPath;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

public class AceFunctions {
    private static final HashFunction hf = Hashing.sha256();
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
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        JsonParser    p = JsonFactoryImpl.INSTANCE.getParser(source);
        JsonGenerator g = JsonFactoryImpl.INSTANCE.getGenerator(out);
        JsonToken     t = p.nextToken();
        
        while (t != null) {
            String currentName = p.getCurrentName();
            if (currentName == null || Collections.binarySearch(LookupPath.INSTANCE.getHashFilter(), currentName) < 0) {
                g.copyCurrentEvent(p);
            } 
            t = p.nextToken();
        }
        p.close();
        g.flush();
        g.close();
        return hf.newHasher().putBytes(out.toByteArray()).hash();
    }
    
    public static boolean verifyId(IAceBaseComponent source) throws IOException {
        String id = source.getId(false);
        if (id == null) {
            return false;
        }
        return id == generateId(source);
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
}
