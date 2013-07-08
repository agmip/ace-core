package org.agmip.ace.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class MetadataFilterTest {
    @Before
    public void setup() {
        MetadataFilter.INSTANCE.initialize();
    }
    
    @Test
    public void checkExport() {
        assertTrue(MetadataFilter.INSTANCE.getExportMetadata().contains("crid"));
        assertFalse(MetadataFilter.INSTANCE.getRestrictedNoExportMetadata().contains("invalid"));
    }
}
