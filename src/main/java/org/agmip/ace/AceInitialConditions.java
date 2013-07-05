package org.agmip.ace;

import java.io.IOException;

public class AceInitialConditions extends AceComponent {
    private AceRecordCollection soilLayers = null;

    public AceInitialConditions(byte[] source) throws IOException {
        super(source);
        this.componentType = AceComponentType.ACE_INITIALCONDITIONS;
    }

    public AceRecordCollection getSoilLayers() throws IOException {
        if (this.soilLayers == null) {
            this.soilLayers = this.getRecords("soilLayers");
        }
        return this.soilLayers;
    }
}
