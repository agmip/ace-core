package org.agmip.ace.util;

import java.io.IOException;
import java.io.InputStream;
//import java.util.HashMap;
import java.util.zip.GZIPInputStream;

import org.agmip.ace.AceDataset;
import org.agmip.ace.AceExperiment;
import org.agmip.ace.io.AceParser;
import org.junit.Before;
import org.junit.Test;


public class AceFunctionsTest {
    private AceDataset setMach;
    private AceDataset setHSC;

    @Before
    public void setup() throws IOException {
        InputStream sourceMach = new GZIPInputStream(this.getClass().getResourceAsStream("/test.aceb"));
        InputStream sourceHSC = new GZIPInputStream(this.getClass().getResourceAsStream("/hsc.aceb"));
        setMach = AceParser.parse(sourceMach);
        setHSC  = AceParser.parse(sourceHSC);
        sourceMach.close();
        sourceHSC.close();
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
}
