package org.agmip.ace;

import java.io.IOException;

public interface IAceBaseComponent {
    public String getId(boolean forceRegenerate) throws IOException;
    public AceComponentType getComponentType();
    public byte[] getRawComponent() throws IOException;
}
