package org.agmip.ace;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class AceInitialConditions extends AceComponent {
    private AceRecordCollection soilLayers = null;

    public AceInitialConditions(byte[] source) throws IOException {
        super(source);
        this.getSoilLayers();
        this.extractSubcomponents();
        this.componentType = AceComponentType.ACE_INITIALCONDITIONS;
    }

    public AceRecordCollection getSoilLayers() throws IOException {
        if (this.soilLayers == null) {
            this.soilLayers = this.getRecords("soilLayer");
        }
        return this.soilLayers;
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
}
