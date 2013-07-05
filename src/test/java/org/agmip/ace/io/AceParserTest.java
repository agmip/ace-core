package org.agmip.ace.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.agmip.ace.AceDataset;
import org.agmip.ace.AceWeather;
import org.agmip.ace.io.AceParser;

public class AceParserTest {
    private AceDataset set;

    @Before
    public void setup() throws IOException {
        InputStream source = new GZIPInputStream(this.getClass().getResourceAsStream("/test.aceb"));
        set = AceParser.parse(source);
        source.close();
    }

    @Test
    public void getValueOrTest() throws IOException {
        System.out.println(set.getWeathers().size());
        for (AceWeather w : set.getWeathers()) {
            System.out.println(w.getValueOr("wst_id", "INVALID"));
            String lat = w.getValueOr("wst_lat", "-999.99");
            String lon = w.getValueOr("wst_long", "-99.99");
            System.out.println("LL: "+lat+", "+lon);
        }
        Assert.assertTrue(true);
    }
}
