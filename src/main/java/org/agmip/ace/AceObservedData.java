package org.agmip.ace;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

public class AceObservedData extends AceComponent {
  private AceRecordCollection timeseries;

  public AceObservedData(byte[] source) throws IOException {
    super(source);
    this.getTimeseries();
    this.extractSubcomponents();
    this.componentType = AceComponentType.ACE_OBSERVED;

  }

  public AceRecordCollection getTimeseries() throws IOException {
    if (this.timeseries == null) {
      this.timeseries = this.getRecords("timeSeries");
    }
    return this.timeseries;
  }

  private void extractSubcomponents() throws IOException {
    ByteArrayOutputStream baseOut = new ByteArrayOutputStream();
    JsonParser p = this.getParser();
    JsonGenerator g = this.getGenerator(baseOut);
    JsonToken t;

    t = p.nextToken();

    while (t != null) {
      String currentName = p.getCurrentName();
      if(currentName != null && t == JsonToken.FIELD_NAME &&
          currentName.equals("timeSeries")) {
        p.nextToken();
        p.skipChildren();
      } else {
        g.copyCurrentEvent(p);
      }
      t = p.nextToken();
    }
    p.close();
    g.flush();
    g.close();
    this.component = baseOut.toByteArray();
  }
}
