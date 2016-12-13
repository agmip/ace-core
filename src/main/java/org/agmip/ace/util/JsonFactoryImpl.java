package org.agmip.ace.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;

public enum JsonFactoryImpl {
    INSTANCE;
    
    public static final JsonFactory jsonFactory = new JsonFactory();
    
    public JsonParser getParser(byte[] source) throws IOException {
        return jsonFactory.createParser(source);
    }
    
    public JsonParser getParser(InputStream source) throws IOException {
        return jsonFactory.createParser(source);
    }
    
    public JsonGenerator getGenerator(OutputStream dest) throws IOException {
        return jsonFactory.createGenerator(dest);
    }
}
