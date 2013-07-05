package org.agmip.ace.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import org.agmip.ace.AceComponent;
import org.agmip.ace.AceDataset;
import org.agmip.ace.util.JsonFactoryImpl;

import com.fasterxml.jackson.core.JsonGenerator;

public class AceGenerator {
    /*private static int  ACEB_VERSION = ((AceVersion.ACEB_MAJOR_VERSION & 0x7f) << 24)
            | ((AceVersion.ACEB_MINOR_VERSION & 0x7f) << 16)
            | ((AceVersion.ACEB_REVISION_VERSION & 0x7f) << 8) | 0; */

    
    
    private AceGenerator() {
    }

    public static void generate(File dest, AceDataset set) throws IOException {
        FileOutputStream fos = new FileOutputStream(dest);
        generate(fos, set);
    }

    public static void generate(OutputStream dest, AceDataset set)
            throws IOException {
        generate(dest, set, true);
    }

    public static void generate(OutputStream dest, AceDataset set,
            boolean closeStream) throws IOException {
        JsonGenerator g = JsonFactoryImpl.INSTANCE.getGenerator(dest);
        g.writeStartObject();
        g.writeArrayFieldStart("weathers");
        g.flush();
        Iterator<AceComponent> i;
        List<AceComponent> c = set.getWeatherComponents();
        i = c.iterator();
        writeBlock(dest, i);
        g.writeEndArray();
        g.writeArrayFieldStart("soils");
        g.flush();
        c = set.getSoilComponents();
        i = c.iterator();
        writeBlock(dest, i);
        g.writeEndArray();
        g.writeArrayFieldStart("experiments");
        g.flush();
        c = set.getExperimentComponents();
        i = c.iterator();
        writeBlock(dest, i);
        g.writeEndArray();
        g.writeEndObject();
        g.close();
        if (closeStream) {
            dest.close();
        }
    }

    public static void generateACEB(File dest, AceDataset set)
            throws IOException {
        FileOutputStream fos = new FileOutputStream(dest);
        GZIPOutputStream gos = new GZIPOutputStream(fos);
        generate(gos, set, false);
        gos.close();
        fos.close();
    }
    
    public static void generateACEB(File dest, String json) throws IOException {
        FileOutputStream fos = new FileOutputStream(dest);
        GZIPOutputStream gos = new GZIPOutputStream(fos);
        gos.write(json.getBytes("UTF-8"));
        gos.close();
        fos.close();
        
    }

    private static void writeBlock(OutputStream dest, Iterator<AceComponent> i)
            throws IOException {
        while (i.hasNext()) {
            dest.write(i.next().getRawComponent());
            if (i.hasNext()) {
                dest.write(",".getBytes("UTF-8"));
            }
        }
    }
}
