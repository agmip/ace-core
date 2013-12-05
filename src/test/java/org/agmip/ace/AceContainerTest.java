package org.agmip.ace;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import org.agmip.ace.io.AceParser;
import org.junit.Before;
import org.junit.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AceContainerTest {
    private static final Logger LOG = LoggerFactory.getLogger(AceContainerTest.class);
    private AceWeather w;
    private AceSoil    s;
    private AceExperiment e;

    @Before
    public void setup() throws IOException {
        InputStream sourceHSC = new GZIPInputStream(this.getClass().getResourceAsStream("/hsc.aceb"));
        AceDataset setHSC  = AceParser.parse(sourceHSC);
        sourceHSC.close();
        w = setHSC.getWeathers().get(0);
        s = setHSC.getSoils().get(0);
        e = setHSC.getExperiments().get(0);
    }

    @Test
    public void testWeatherValidation() throws IOException {
        String originalId = w.getId();
        assertNotNull("NULL WeatherID found", originalId);
        assertTrue("Failed validId()", w.validId());
        assertEquals("IDs should match", originalId, w.generateId());
    }

    @Test
    public void testInvalidWeatherValidation() throws IOException {
        String originalId = w.getId();
        w.update("wst_lat", "-99.99");
        assertFalse("IDs should not match", originalId.equals(w.generateId()));
        assertFalse("Passed validId()", w.validId());
    }
    
    @Test
    public void testSoilValidation() throws IOException {
        String originalId = s.getId();
        assertTrue("Failed validId()", s.validId());
        assertEquals("IDs should match", originalId, s.generateId());

    }

    @Test
    public void testInvalidSoilValidation() throws IOException {
        String originalId = s.getId();
        s.update("sldp", "17");
        assertFalse("IDs should not match", originalId.equals(s.generateId()));
        assertFalse("Passed validId()", s.validId());
    }
    
    @Test
    public void testExperimentValidation() throws IOException {
        String originalId = e.getId();
        LOG.debug("Original ID from tEV: {}", originalId);
        assertTrue("Failed validId()", e.validId());
        assertEquals("IDs should match", originalId, e.generateId());
    }

    @Test
    public void testInvalidExperimentValidation() throws IOException {
        String originalId = e.getId();
        e.update("rotation", "1");
        assertFalse("IDs should not match", originalId.equals(e.generateId()));
        assertFalse("Passed validId()", e.validId());
    }
    
}
