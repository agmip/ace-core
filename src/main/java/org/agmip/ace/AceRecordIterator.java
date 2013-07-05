package org.agmip.ace;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import org.agmip.ace.util.JsonFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AceRecordIterator implements Iterator<AceRecord> {
    private static final Logger log = LoggerFactory.getLogger(AceRecordIterator.class);
    private JsonParser p;
    private JsonToken t;

    public AceRecordIterator(byte[] source) throws IOException {
        this.p = JsonFactoryImpl.INSTANCE.getParser(source);
        this.t = this.p.nextToken();
        if (this.t != JsonToken.START_ARRAY) {
            log.error("Pump failed to start, is {}", (t == null ? "NULL" : t.toString()));
        }
    }

    public boolean hasNext() {
        try {
            t = p.nextToken();
            if (t == JsonToken.START_OBJECT) {
                return true;
            } else {
                p.close();
                return false;
            }
        } catch (IOException ex) {
            log.error("Exception caught: {}", ex.getMessage());
            return false;
        }
    }

    public AceRecord next() {
        try {
            JsonGenerator g = JsonFactoryImpl.INSTANCE.getGenerator(new ByteArrayOutputStream());
            g.copyCurrentStructure(p);
            g.flush();
            byte[] out = ((ByteArrayOutputStream)g.getOutputTarget()).toByteArray();
            g.close();
            return new AceRecord(out);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }
}
