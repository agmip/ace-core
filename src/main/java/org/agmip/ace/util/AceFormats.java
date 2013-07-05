package org.agmip.ace.util;

import java.text.DecimalFormat;

public enum AceFormats {
    INSTANCE;
    
    private DecimalFormat coordinates = new DecimalFormat("#.00");
    
    public DecimalFormat getCoordinateFormat() {
        return this.coordinates;
    }
}
