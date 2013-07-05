package org.agmip.ace.lookup;

import org.junit.Test;
import static org.junit.Assert.*;

public class LookupCodesTest {
    @Test
    public void simpleLookupTest() {
        String test = "Medicago sativa";

        assertEquals("Invalid lookup results", test, LookupCodes.lookupCode("CRID", "ALF", "ln"));
    }

    @Test
    public void lookupThroughModeTest() {
        String test = "Alfalfa/Lucerne";

        assertEquals("Invalid lookup results", test, LookupCodes.lookupCode("CRID", "AL", "cn", "DSSAT"));
    }
}