package org.agmip.ace;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.agmip.ace.util.AceFunctions;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.google.common.hash.HashCode;

public class AceSoil extends AceComponent implements IAceBaseComponent {
    private String sid;
    private AceRecordCollection soilLayers = null;

    public AceSoil(byte[] source) throws IOException {
        super(source);
        this.getSoilLayers();
        this.extractSubcomponents();
        this.componentType = AceComponentType.ACE_SOIL;
        this.sid = this.getValue("sid");
        if(this.sid == null) {
            this.getId(true);
        }
    }

    public String getId(boolean forceRegenerate) throws IOException {
        if (forceRegenerate || this.sid == null) {
            this.sid = this.generateId();
            this.update("sid", this.sid, true);
        }
        return this.sid;
    }

    public String getId() throws IOException {
        return this.getId(false);
    }

    public String generateId() throws IOException {
        HashCode currentHash = this.getRawComponentHash();
        for (AceRecord r: this.getSoilLayers()) {
            currentHash = AceFunctions.generateHCId(currentHash.asBytes(), r.getRawComponentHash().asBytes());
        }
        return currentHash.toString();
    }

    public boolean validId() throws IOException {
        if (this.sid == null) return false;
        return this.sid.equals(this.generateId());
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
                    currentName.equals("soilLayer")) {
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
        baseOut = null;
    }


    public AceRecordCollection getSoilLayers() throws IOException {
        if (this.soilLayers == null) {
            this.soilLayers = this.getRecords("soilLayer");
        }
        return this.soilLayers;
    }

    public byte[] rebuildComponent() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        JsonParser p = this.getParser();
        JsonGenerator g = this.getGenerator(bos);
        JsonToken t = p.nextToken();
        while ( t != null) {
            if (t == JsonToken.END_OBJECT) {
                g.writeArrayFieldStart("soilLayer");
                for(AceRecord r: this.getSoilLayers()) {
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
