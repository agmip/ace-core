package org.agmip.ace;

import java.io.IOException;
import java.util.AbstractCollection;
import java.util.Iterator;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import org.agmip.ace.util.JsonFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AceRecordCollection extends AbstractCollection<AceRecord> {
    private static final Logger log = LoggerFactory.getLogger(AceRecordCollection.class);
    private byte[] collection;
    private int length;

    public AceRecordCollection(byte[] source) throws IOException {
        super();
        this.collection = source;
        this.length = this.countRecords();
    }

    @Override
    public int size() {
        return this.length;
    }

    @Override
    public Iterator<AceRecord> iterator() {
        try {
            return new AceRecordIterator(collection);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private int countRecords() throws IOException {
        JsonParser p = JsonFactoryImpl.INSTANCE.getParser(collection);
        JsonToken t = p.nextToken();
        int i = 0;
        if (t != JsonToken.START_ARRAY) {
            log.error("Not starting with START_ARRAY, is {}", (t == null ? "NULL" : t.asString()));
            //DEBUG
            log.debug("Value is: {}",new String(collection, "UTF-8"));
            return i;
        }
        while(t != null) {
            if (t == JsonToken.START_OBJECT) {
                i++;
                p.skipChildren();
            }
            t = p.nextToken();
        }
        p.close();
        return i;
    }
}
