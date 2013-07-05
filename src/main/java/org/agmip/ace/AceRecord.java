package org.agmip.ace;

public class AceRecord extends AceComponent {
    public AceRecord() {
        super();
    }
    
    public AceRecord(byte[] source) {
        super(source);
        this.componentType = AceComponentType.ACE_RECORD;
    }
}
