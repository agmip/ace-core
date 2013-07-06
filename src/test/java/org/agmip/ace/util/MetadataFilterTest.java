package org.agmip.ace.util;

import org.junit.Test;
import static org.junit.Assert.*;

public class MetadataFilterTest {
    @Test
    public void checkExport() {
        assertTrue(MetadataFilter.INSTANCE.getExportMetadata().contains("crid"));
        assertFalse(MetadataFilter.INSTANCE.getRestrictedNoExportMetadata().contains("nothign"));
    }
}
