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
    
    @Override
    public boolean add(AceRecord e) {
        try {
            JsonParser p = JsonFactoryImpl.INSTANCE.getParser(collection);
            JsonToken t = p.nextToken();
            if (t != JsonToken.START_ARRAY) {
                log.error("Not starting with START_ARRAY, is {}", (t == null ? "NULL" : t.asString()));
                //DEBUG
                log.debug("Value is: {}",new String(collection, "UTF-8"));
                return false;
            }
            p.close();
            StringBuilder sb = new StringBuilder(new String(collection));
            if (sb.toString().equals("[]")) {
                sb = new StringBuilder();
                sb.append("[");
            } else {
                sb.deleteCharAt(sb.length() - 1);
                sb.append(",");
            }
            sb.append(new String(e.getRawComponent()));
            sb.append("]");
            collection = sb.toString().getBytes("UTF-8");

        } catch (IOException ex) {
            log.error("Fail to insert record caused by: {}", ex.getMessage());
            return false;
        }
        return true;
    }

    public AceRecord getByIndex(int index) throws Exception {
      if(index < 0 || index >= this.size())
        throw new IndexOutOfBoundsException();

      int i=0;
      Iterator<AceRecord> iter = this.iterator();
      while(iter.hasNext()) {
        if(index == i) {
          return iter.next();
        } else {
          i++;
          iter.next();
        }
      }
      return null;
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
