package org.agmip.ace;

import static org.junit.Assert.assertNotEquals;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.agmip.ace.io.AceParser;

public class AceDatasetTest {
    private AceDataset setMach;
    private AceDataset setHSC;

    @Before
    public void setup() throws IOException {
        InputStream sourceMach = new GZIPInputStream(this.getClass().getResourceAsStream("/mach_baseline.aceb"));
        InputStream sourceHSC = new GZIPInputStream(this.getClass().getResourceAsStream("/hsc.aceb"));
        setMach = AceParser.parse(sourceMach);
        setHSC  = AceParser.parse(sourceHSC);
        sourceMach.close();
        sourceHSC.close();
    }

    @Test
    public void testRecordSize() throws IOException {
        for(AceWeather w : setMach.getWeathers()) {
            AceRecordCollection coll = w.getDailyWeather();
            // For test.aceb
            Assert.assertEquals("Incorrect number of records", 11323, coll.size());
            // For hsc.aceb
            //Assert.assertEquals("Incorrect number of record", 2454, coll.size());
        }
    }

    @Test
    public void testSoils() throws IOException {
        for(AceSoil s: setMach.getSoils()) {
            System.out.println("SID: "+s.getId()+"\nSoilID: "+s.getValueOr("soil_id", "INVALID"));
        }
    }

    @Test
    public void testExperiments() throws IOException {
        for(AceExperiment e: setMach.getExperiments()) {
            System.out.println("EID: "+e.getId()+"\nExname: "+e.getValueOr("exname", "INVALID"));
            System.out.println("Number of events: "+e.getEvents().size());
        }
    }

    @Test
    public void testFilteredEvents() throws IOException {
        for(AceExperiment e: setHSC.getExperiments()) {
            System.out.println("Number of planting events: "+e.getEvents().filterByEvent(AceEventType.ACE_PLANTING_EVENT).size());
            System.out.println("Number of fertilzer events: "+e.getEvents().filterByEvent(AceEventType.ACE_FERTILIZER_EVENT).size());
            System.out.println("Unordered events: "+e.getEvents());
            System.out.println("Order of events: "+e.getEvents().sort());
            System.out.println("Ordered Irrigation events: "+e.getEvents().filterByEvent(AceEventType.ACE_IRRIGATION_EVENT).sort());
        }
    }

    @Test
    public void testComparable() {
        String a = "20130229";
        String b = "20130301";

        System.out.println("Comparable test "+a.compareTo(b));
        System.out.println("Compare to blank"+a.compareTo(""));
    }
    
    @Test
    public void testUpdateSubcomponents() throws IOException {
        AceExperiment e = setHSC.getExperiments().get(0);
        AceEvent planting = e.getEvents().filterByEvent(AceEventType.ACE_PLANTING_EVENT).asList().get(0);
        String originalName = planting.getValue("crid");
        planting.update("crid", "MAZ");
        String newName = planting.getValue("crid");
        assertNotEquals(originalName, newName);
    }

    @Test
    public void testAssociations() throws IOException {
        AceExperiment e = setHSC.getExperiments().get(0);
        System.out.println("Linked weather ID: "+e.getValue("wid"));
        System.out.println("Linked soil    ID: "+e.getValue("sid"));
    }
}
