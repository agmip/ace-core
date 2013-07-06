package org.agmip.ace.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import org.agmip.ace.AceDataset;
import org.agmip.ace.AceEvent;
import org.agmip.ace.AceEventType;
import org.agmip.ace.AceExperiment;
import org.agmip.ace.AceWeather;
import org.agmip.ace.io.AceParser;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import java.util.HashMap;


public class AceFunctionsTest {
    private AceDataset setMach;
    private AceDataset setHSC;
    private static final Logger LOG = LoggerFactory.getLogger(AceFunctionsTest.class);

    @Before
    public void setup() throws IOException {
        LOG.info("Processed input files");
        InputStream sourceMach = new GZIPInputStream(this.getClass().getResourceAsStream("/test.aceb"));
        InputStream sourceHSC = new GZIPInputStream(this.getClass().getResourceAsStream("/hsc.aceb"));
        setMach = AceParser.parse(sourceMach);
        setHSC  = AceParser.parse(sourceHSC);
        sourceMach.close();
        sourceHSC.close();
        setHSC.linkDataset();
        setMach.linkDataset();
    }
    
    @Test
    public void testValidation() throws IOException {
        for(AceExperiment e : setMach.getExperiments()) {
            // Very intensive workout
            if (! AceFunctions.verifyId(e)) {
                System.out.println("Found:    "+e.getId()+"\nExpected: "+AceFunctions.generateId(e.getRawComponent())+"\n\n");
            }
        }
    }
    
    @Test
    public void testGeneration() throws IOException {
        for(AceExperiment e : setHSC.getExperiments()) {
            System.out.println("Generated new id: "+AceFunctions.generateId(e));
        }
    }
    
    @Test
    public void testHasherIgnoresFields() throws IOException {
        AceExperiment e = setHSC.getExperiments().get(0);
        String originalName = e.getValue("exname");
        String originalHash = AceFunctions.generateId(e.getRawComponent());
        e.update("exname", "Changed");
        String newName = e.getValue("exname");
        String newHash = AceFunctions.generateId(e.getRawComponent());
        assertEquals(originalHash, newHash);
        assertNotEquals(originalName, newName);
    }
    
    @Test
    public void testHasherUpdatesFields() throws IOException {
        AceExperiment e = setHSC.getExperiments().get(0);
        String originalName = e.getValueOr("rotation", "N");
        String originalHash = AceFunctions.generateId(e.getRawComponent());
        e.update("rotation", "Y");
        String newName = e.getValueOr("rotation", "N");
        String newHash = AceFunctions.generateId(e.getRawComponent());
        assertNotEquals(originalHash, newHash);
        assertNotEquals(originalName, newName);
    }
    
     
    
    
    @Test
    public void testHasherUpdateDeepFields() throws IOException {
        AceExperiment e = setHSC.getExperiments().get(0);
        String originalExhash = e.getId(true);
        AceEvent planting = e.getEvents().filterByEvent(AceEventType.ACE_PLANTING_EVENT).asList().get(0);
        String originalCrop = planting.getValue("crid");
        String originalHash = planting.getRawComponentHash().toString();
        planting.update("crid", "MAZ");
        String newCrop = planting.getValue("crid");
        String newHash = planting.getRawComponentHash().toString();
        String newExhash = e.getId(true);
        assertNotEquals(originalCrop, newCrop);
        assertNotEquals(originalHash, newHash);
        assertNotEquals(originalExhash, newExhash);
        LOG.info("Old ExHash: {} New ExHash: {}", originalExhash, newExhash);
    }
    
    @Test 
    public void testHasherUpdateDeepIgnoreFields() throws IOException {
        AceExperiment e = setHSC.getExperiments().get(0);
        String originalExhash = e.getId(true);
        AceEvent planting = e.getEvents().filterByEvent(AceEventType.ACE_PLANTING_EVENT).asList().get(0);
        String originalValue = new String(planting.getRawComponent(), "UTF-8");
        planting.update("pldoe", "blahblah", true);
        String newExhash = e.getId(true);
        String newValue  = new String(planting.getRawComponent(), "UTF-8");
        assertNotEquals(originalValue, newValue);
        assertEquals(originalExhash, newExhash);
    }
    
    @Test
    public void testWeatherOnHash() throws IOException {
        AceWeather w = setHSC.getWeathers().get(0);
        assertNotNull(w);
        assertNotEquals(w.getId(), AceFunctions.generateId(w.getRawComponent()));
        LOG.info("New Hash Is: {}",w.getId(true));
    }
    
    @Test
    public void generateAllIds() throws IOException {
        AceExperiment e = setMach.getExperiments().get(0);
        LOG.info("Experiment Hash: {}", e.getId(true));
        LOG.info("Weather hash: {}", e.getWeather().getId(true));
        LOG.info("Soil Hash: {}", e.getSoil().getId(true));
        LOG.info("Weather meta: {}", new String(e.getWeather().getRawComponent(), "UTF-8"));
        LOG.info("New Hashes:\n\tExperiment: {}\n\tWeather: {}\n\tSoil: {}", e.getId(), e.getWeather().getId(), e.getSoil().getId());
    }
}
