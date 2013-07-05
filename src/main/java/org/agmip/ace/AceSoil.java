package org.agmip.ace;

import java.io.IOException;
import java.util.List;

import org.agmip.ace.util.AceFunctions;

import com.google.common.collect.Lists;

public class AceSoil extends AceComponent implements IAceBaseComponent {
    private String sid;
    private AceRecordCollection soilLayers = null;

    public AceSoil(byte[] source) throws IOException {
        super(source);
        this.componentType = AceComponentType.ACE_SOIL;
        this.sid = this.getValue("sid");
        if(this.sid == null) {
            this.update("sid", AceFunctions.generateId(source), true);
        }
    }

    public String getId() {
        return this.sid;
    }
    
    public AceComponentType getComponentType() {
        return this.componentType;
    }
    
   
    public AceRecordCollection getSoilLayers() throws IOException {
        if (this.soilLayers == null) {
            this.soilLayers = this.getRecords("soilLayers");
        }
        return this.soilLayers;
    }
    
    @Override
    public AceSoil update(String key, String newValue, boolean addIfMissing) throws IOException {
        super.update(key, newValue, addIfMissing);
        if (key == "sid") {
            this.sid = newValue;
        } else {
            List<String> id = Lists.newArrayList("sid");
            this.component = AceFunctions.removeKeys(this.component, id);
            this.update("sid", AceFunctions.generateId(this.component), true);
            this.hasUpdate = true;
        }
        return this;
    }
}
