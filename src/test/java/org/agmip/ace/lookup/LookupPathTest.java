package org.agmip.ace.lookup;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class LookupPathTest {

    @Test
    public void checkHashFilterSize() {
        assertFalse(LookupPath.INSTANCE.getHashFilter().isEmpty());
    }
    
    @Test
    public void verifyHashFilter() {
        assertTrue(LookupPath.INSTANCE.getHashFilter().contains("institution"));
        assertFalse(LookupPath.INSTANCE.getHashFilter().contains("rotation"));
    }
}
