package org.agmip.ace;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.agmip.ace.AceDataset;
import org.agmip.ace.AceRecord;
import org.agmip.ace.AceRecordCollection;
import org.agmip.ace.AceWeather;
import org.agmip.ace.io.AceParser;
import org.junit.Test;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AceRecordCollectionTest {
    private static final Logger log = LoggerFactory.getLogger(AceRecordCollectionTest.class);

    @Test
    public void testBlankCollection() throws IOException {
        log.debug("==== Blank Collection Test ===");
        byte[] test = new byte[0];
        AceRecordCollection coll = new AceRecordCollection(test);
        Assert.assertEquals("Incorrect number of records", 0, coll.size());
        log.debug("=== END TEST ===");
    }

    @Test
    public void testBlankIterable() throws IOException {
        log.debug("=== Blank Iterable Test ===");
        byte[] test = new byte[0];
        AceRecordCollection coll = new AceRecordCollection(test);
        Iterator<AceRecord> i = coll.iterator();
        Assert.assertFalse("iterator should not iterate", i.hasNext());
        log.debug("=== END TEST ===");
    }

    @Test
    public void testValidCollection() throws IOException {
        AceDataset set = loadTestFile();
        for(AceWeather wth : set.getWeathers()) {
            int testCount = wth.getDailyWeather().size();
            int ia=0;
            int ib=0;
            Iterator<AceRecord> iter = wth.getDailyWeather().iterator();
            while(iter.hasNext()) {
                iter.next();
                ia++;
            }
            for(AceRecord c : wth.getDailyWeather()) {
                c.getValue("w_date");
                ib++;
            }
            Assert.assertEquals("Iterator returned a different number of records", testCount, ia);
            Assert.assertEquals("Iterator returned a different number of records", testCount, ib);
        }
    }

    @Test
    public void testCollectionGetByIndex() throws Exception {
      AceDataset set = loadTestFile();
      log.debug("=== Start getByIndex() test ===");
      for(AceWeather wth : set.getWeathers()) {
        AceRecord r = wth.getDailyWeather().getByIndex(2);
        log.debug("Found: {}", r.getValueOr("w_date", "00000000"));
      }
      log.debug("=== END TEST ===");
    }

    @Test
    public void testRecordYears() throws IOException {
        AceDataset set = loadTestFile();
        List<String> testYears = new ArrayList<String>();
        for (int i=1980; i < 2011; i++) {
            testYears.add(i+"");
        }
        for(AceWeather wth : set.getWeathers()) {
            List<String> years = wth.getRecordYears();
            Assert.assertEquals("Years do not match", testYears, years);
        }
    }

    @Test
    public void testMissingDates() throws IOException {
        AceDataset set = loadTestFile();
        for(AceWeather wth : set.getWeathers()) {
            List<String> missing = wth.getMissingDates();
            Assert.assertEquals("Incorrect missing dates", 0, missing.size());
        }
    }

    private static AceDataset loadTestFile() throws IOException {
        InputStream source = new GZIPInputStream(AceRecordCollectionTest.class.getResourceAsStream("/test.aceb"));
        AceDataset set = AceParser.parse(source);
        return set;
    }
}
