package org.agmip.ace.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import org.agmip.ace.AceDataset;
import org.agmip.ace.IAceBaseComponent;
import org.agmip.ace.util.JsonFactoryImpl;

import com.fasterxml.jackson.core.JsonGenerator;

/**
 * This class is responsible for generating the JSON (or GZIPPED JSON)
 * {@link OutputStream} from an {@link AceDataset}.
 */
public class AceGenerator {
  private AceGenerator() {}

  /**
   * Write a dataset to a file (uncompressed).
   * <p>
   * This utility method uses {@link #generate} to write the uncompressed
   * JSON to a file. This method automatically closes the {@link OutputStream}
   * used to create the file.
   *
   * @param dest Destination {@link File}
   * @param set  the {@link AceDataset} to write.
   * @throws IOException if there is an I/O error
   */
  public static void generate(File dest, AceDataset set) throws IOException {
    FileOutputStream fos = new FileOutputStream(dest);
    generate(fos, set);
  }

  /**
   * Write a dataset to an {@link OutputStream} and automatically close
   * the stream (uncompressed). 
   * <p>
   * This utility method uses {@link #generate} to write the uncompressed
   * JSON to an OutputStream. This method automatically closes the 
   * OutputStream.
   *
   * @param dest Destination {@link OutputStream}.
   * @param set  the {@link AceDataset} to write.
   * @throws IOException if there is an I/O error
   */
  public static void generate(OutputStream dest, AceDataset set)
    throws IOException {
    generate(dest, set, true);
  }

  /**
   * Write a dataset to an {@link OutputStream} (uncompressed).
   * <p>
   * This method writes the uncompressed JSON to an OutputStream.
   *
   * @param dest Destination {@link OutputStream}
   * @param set  {@link AceDataset} to write.
   * @param closeStream close stream after writing.
   * @throws IOException if there is an I/O error
   */
  public static void generate(OutputStream dest, AceDataset set,
      boolean closeStream) throws IOException {
    JsonGenerator g = JsonFactoryImpl.INSTANCE.getGenerator(dest);
    g.writeStartObject();
    g.writeArrayFieldStart("weathers");
    g.flush();
    Iterator<IAceBaseComponent> i;
    List<IAceBaseComponent> c = set.getWeatherComponents();
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

  /**
   * Write a dataset to a file (compressed).
   * <p>
   * This utility method uses {@link #generate} to write the GZIP compressed
   * JSON to a file. This method automatically closes the OutputStream used
   * to create the file.
   *
   * @param dest Destination {@link File}
   * @param set the {@link AceDataset} to compress and write.
   * @throws IOException if there is an I/O error
   */
  public static void generateACEB(File dest, AceDataset set)
    throws IOException {
    FileOutputStream fos = new FileOutputStream(dest);
    GZIPOutputStream gos = new GZIPOutputStream(fos);
    generate(gos, set, false);
    gos.close();
    fos.close();
  }

  /**
   * Write a GZIP compressed string to a file.
   * <p>
   * This method GZIP compresses a string and writes it to a file. This method
   * automatically closes the OutputStream used to create the file.
   * 
   * @param dest Destination {@link File}
   * @param json String to GZIP compress and write.
   * @throws IOException if there is an I/O error
   */
  public static void generateACEB(File dest, String json) throws IOException {
    FileOutputStream fos = new FileOutputStream(dest);
    GZIPOutputStream gos = new GZIPOutputStream(fos);
    gos.write(json.getBytes("UTF-8"));
    gos.close();
    fos.close();

  }

  /**
   * Write the current {@link IAceBaseComponent} to an {@link OutputStream}.
   * <p>
   * This method automatically calls {@link IAceBaseComponent#rebuildComponent}
   * to retrieve all subcomponents.
   *
   * @param dest Destination {@link OutputStream}
   * @param i    Iterator of all items of a specific {@link IAceBaseComponent}.
   * @throws IOException if there is an I/O error
   */
  private static void writeBlock(OutputStream dest, Iterator<IAceBaseComponent> i)
    throws IOException {
    while (i.hasNext()) {
      dest.write(i.next().rebuildComponent());
      if (i.hasNext()) {
        dest.write(",".getBytes("UTF-8"));
      }
    }
  }
}
