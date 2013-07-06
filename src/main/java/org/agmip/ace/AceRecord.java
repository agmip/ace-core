package org.agmip.ace;

import java.io.IOException;

public class AceRecord extends AceComponent {
    public AceRecord() throws IOException {
        super();
    }
    
    public AceRecord(byte[] source) throws IOException {
        super(source);
        this.componentType = AceComponentType.ACE_RECORD;
    }
}
