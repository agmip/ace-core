package org.agmip.ace;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.agmip.ace.util.AceFunctions;
import org.agmip.ace.util.JsonFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.google.common.hash.HashCode;

public class AceComponent {
    private static final Logger log = LoggerFactory
            .getLogger(AceComponent.class);
    protected byte[] component;
    protected boolean hasUpdate;

    public AceComponentType componentType;

    public AceComponent() throws IOException {
        this.component = "{}".getBytes("UTF-8");
        this.hasUpdate = false;
    }

    public AceComponent(byte[] component) throws IOException {
        this.component = component;
        this.hasUpdate = false;
    }
    
    public byte[] getRawComponent() throws IOException {
        return this.component;
    }

    public boolean isUpdated() {
        return this.hasUpdate;
    }

    public JsonParser getParser() throws IOException {
        return JsonFactoryImpl.INSTANCE.getParser(this.component);
    }

    public JsonGenerator getGenerator() throws IOException {
        return JsonFactoryImpl.INSTANCE
                .getGenerator(new ByteArrayOutputStream());
    }

    public JsonGenerator getGenerator(OutputStream stream) throws IOException {
        return JsonFactoryImpl.INSTANCE.getGenerator(stream);
    }

    public String getValueOr(String key, String alternateValue)
            throws IOException {
        String value = this.getValue(key);
        if (value == null) {
            return alternateValue;
        } else {
            return value;
        }
    }

    public String getValue(String key) throws IOException {
        JsonParser p = this.getParser();
        JsonToken t;

        t = p.nextToken();

        while (t != null) {
            if (t == JsonToken.FIELD_NAME && p.getCurrentName().equals(key)) {
                String value = p.nextTextValue();
                p.close();
                return value;
            }
            t = p.nextToken();
        }
        p.close();
        return null;
    }
    
    public HashCode getRawComponentHash() throws IOException {
        return AceFunctions.generateHCId(this.component);
    }

    public byte[] getRawRecords(String key) throws IOException {
        JsonParser p = this.getParser();
        JsonToken t;

        t = p.nextToken();

        while (t != null) {
            if (t == JsonToken.FIELD_NAME && p.getCurrentName().equals(key)) {
                t = p.nextToken();
                if (p.isExpectedStartArrayToken()) {
                    JsonGenerator g = this.getGenerator();
                    g.copyCurrentStructure(p);
                    g.flush();
                    byte[] subcomponent = ((ByteArrayOutputStream) g
                            .getOutputTarget()).toByteArray();
                    g.close();
                    p.close();
                    return subcomponent;
                } else {
                    log.error("Key {} does not start an array.", key);
                    return AceFunctions.getBlankSeries();
                }
            }
            t = p.nextToken();
        }
//        log.debug("Did not find key: {} in {}", key, new String(this.component, "UTF-8"));
        return AceFunctions.getBlankSeries();
    }

    public AceRecordCollection getRecords(String key) throws IOException {
        return new AceRecordCollection(this.getRawRecords(key));
    }

    public byte[] getRawSubcomponent(String key) throws IOException {
        JsonParser p = this.getParser();
        JsonToken t = p.nextToken();

        while (t != null) {
            if (t == JsonToken.FIELD_NAME && p.getCurrentName().equals(key)) {
                t = p.nextToken();
                if (t == JsonToken.START_OBJECT) {
                    JsonGenerator g = this.getGenerator();
                    g.copyCurrentStructure(p);
                    g.flush();
                    byte[] subcomponent = ((ByteArrayOutputStream) g
                            .getOutputTarget()).toByteArray();
                    g.close();
                    p.close();
                    return subcomponent;
                } else {
                    log.error("Key {} does not start an object.", key);
                    return AceFunctions.getBlankComponent();
                }
            }
            t = p.nextToken();
        }
//        log.debug("Did not find key: {} in {}", key, new String(this.component, "UTF-8"));
        return AceFunctions.getBlankComponent();
    }

    public AceComponent getSubcomponent(String key) throws IOException {
        return new AceComponent(this.getRawSubcomponent(key));
    }

    public String toString() {
        return new String(this.component);
    }

  
    public AceComponent update(String key, String newValue,
            boolean addIfMissing, boolean removeMode) throws IOException {
        AceComponentType updateComponentType = AceFunctions
                .getComponentTypeFromKey(key);
        if (updateComponentType == this.componentType || key.equals("wid")
                || key.equals("sid") || key.equals("eid")) {
            // The update can proceed.
            boolean updated = false;
            int nestedLevel = 0;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            JsonParser p = this.getParser();
            JsonGenerator g = this.getGenerator(out);
            JsonToken t = p.nextToken();

            while (t != null) {
                if (t == JsonToken.START_OBJECT) {
                    nestedLevel++;
                }
                if (nestedLevel == 1 && t == JsonToken.FIELD_NAME
                        && p.getCurrentName().equals(key)) {
                    if (!removeMode) {
                        g.writeStringField(key, newValue);
                    }
                    updated = true;
                    t = p.nextToken(); // Now on value_node
                    t = p.nextToken(); // Now on next_valid_node?
                }
                if (t == JsonToken.END_OBJECT) {
                    if (nestedLevel == 1 && !updated && !removeMode
                            && addIfMissing) {
                        g.writeStringField(key, newValue);
                    }
                    nestedLevel--;
                }
                g.copyCurrentEvent(p);
                t = p.nextToken();
            }
            g.flush();
            g.close();
            this.component = out.toByteArray();
        } else {
            log.error("Failed to update key: {}",key);
        }
        
        return this;
    }

    public AceComponent update(String key, String newValue) throws IOException {
        return this.update(key, newValue, true, false);
    }

    public AceComponent update(String key, String newValue, boolean addIfMissing)
            throws IOException {
        return this.update(key, newValue, addIfMissing, false);
    }

    public AceComponent remove(String key) throws IOException {
        return this.update(key, "", false, true);
    }
}