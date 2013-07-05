package org.agmip.ace;

public interface IAceBaseComponent {
    public String getId();
    public AceComponentType getComponentType();
    public byte[] getRawComponent();
}
