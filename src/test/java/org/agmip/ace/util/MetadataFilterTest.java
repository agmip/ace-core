package org.agmip.ace.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class MetadataFilterTest {
    @Before
    public void setup() {
        //MetadataFilter.INSTANCE.initialize();
    }
    
    @Test
    public void checkExport() {
        assertTrue(MetadataFilter.INSTANCE.getExportMetadata().contains("crid"));
        assertFalse(MetadataFilter.INSTANCE.getRestrictedNoExportMetadata().contains("invalid"));
    }
    
    @Test
    public void checkLabels() {
        assertEquals(MetadataFilter.INSTANCE.getLabels().get("fl_loc_1"), "Field Country");
    }
    
    @Test
    public void checkLabelFor() {
        assertEquals(MetadataFilter.INSTANCE.getLabelFor("fl_loc_1"), "Field Country");
    }
}
