package org.agmip.ace.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opencsv.CSVReader;

public enum MetadataFilter {
    INSTANCE;

    private final Logger LOG = LoggerFactory.getLogger("org.agmip.ace.util.MetadataFilter");
    private final Set<String> metadata              = new CopyOnWriteArraySet<>();
    private final Set<String> required              = new CopyOnWriteArraySet<>();
    private final Set<String> indexed               = new CopyOnWriteArraySet<>();
    private final Set<String> restricted            = new CopyOnWriteArraySet<>();
    private final Set<String> suggested             = new CopyOnWriteArraySet<>();
    private final Set<String> export                = new CopyOnWriteArraySet<>();
    private final Set<String> restrictedexport      = new CopyOnWriteArraySet<>();
    private final Set<String> norestrictedexport    = new CopyOnWriteArraySet<>();
    private final Map<String, String>  descriptions = new ConcurrentHashMap<>();
    private final Map<String, Integer> weights      = new ConcurrentHashMap<>();
    private final Map<String, String>  labels       = new ConcurrentHashMap<>();

    MetadataFilter() {
    	InputStream filter = getClass().getClassLoader().getResourceAsStream("metadata_filter.csv");
        loadFromEmbeddedCSV(filter);
    }

    /**public void initialize() {
        InputStream filter = getClass().getClassLoader().getResourceAsStream("metadata_filter.csv");
        loadFromEmbeddedCSV(filter);
    }**/

    public Set<String> getMetadata() {
        return metadata;
    }

    public Set<String> getRequiredMetadata() {
        return required;
    }

    public Set<String> getIndexedMetadata() {
        return indexed;
    }

    public Map<String, Integer> getWeights() {
        return weights;
    }

    public Set<String> getRestrictedMetadata() {
        return restricted;
    }

    public Set<String> getSuggestedMetadata() {
        return suggested;
    }

    public Set<String> getExportMetadata() {
        return export;
    }

    public Set<String> getRestrictedExportMetadata() {
        return restrictedexport;
    }

    public Set<String> getRestrictedNoExportMetadata() {
        return norestrictedexport;
    }

    public Map<String, String> getDescriptions() {
        return descriptions;
    }
    
    public Map<String, String> getLabels() {
        return labels;
    }
    
    public String getLabelFor(String var) {
        String label = labels.get(var);
        if (label == null) {
            return "";
        } else {
            return label;
        }
    }

    public void addMetadata(String item) {
        metadata.add(item);
    }

    public void addIndexedMetadata(String item) {
        indexed.add(item);
    }

    public void addRequiredMetadata(String item) {
        required.add(item);
    }

    public void addRestrictedMetadata(String item) {
        restricted.add(item);
    }

    public void addSuggestedMetadata(String item) {
        suggested.add(item);
    }

    public void addWeight(String item, int value) {
        weights.put(item, value);
    }
    
    public void addLabel(String item, String value) {
        labels.put(item, value);
    }

    public void addExportMetadata(String item) {
        export.add(item);
    }

    public void addRestrictedExportMetadata(String item) {
        restrictedexport.add(item);
    }

    public void addRestrictedNoExportMetadata(String item) {
        norestrictedexport.add(item);
    }

    public void removeMetadata(String item) {
        metadata.remove(item);
        required.remove(item);
        indexed.remove(item);
    }

    public void removeIndexedMetadata(String item) {
        indexed.remove(item);
    }

    public void removeRequiredMetadata(String item) {
        required.remove(item);
    }

    private void loadFromEmbeddedCSV(InputStream res) {
        try {
            if( res != null ) {
                CSVReader reader = new CSVReader(new InputStreamReader(res));
                String[] nextLine;
                reader.readNext(); // Skip the first line
                while(( nextLine = reader.readNext()) != null) {
                    String var = nextLine[0].toLowerCase();
                    if (! var.startsWith("!")) {
                        // Comments start with !
                        if(! nextLine[1].equals("")) {
                            addIndexedMetadata(var);
                        }
                        if(! nextLine[2].equals("")) {
                            char type = nextLine[2].toUpperCase().charAt(0);
                            if (type == 'S') {
                                addSuggestedMetadata(var);
                            }
                            if (type == 'X') {
                                addRestrictedMetadata(var);
                            }
                            if (type == 'R') {
                                addRequiredMetadata(var);
                            }
                        }
                        if(! nextLine[3].equals("")) {
                            addWeight(var, Integer.parseInt(nextLine[3]));
                        }
                        if(! nextLine[4].equals("")) {
                            char type = nextLine[4].toUpperCase().charAt(0);
                            if( type == 'E') {
                                addExportMetadata(var);
                            }
                            if (type == 'X') {
                                addRestrictedExportMetadata(var);
                            }
                            if (type == 'N') {
                                addRestrictedNoExportMetadata(var);
                            }
                        }
                        addMetadata(var);
                        descriptions.put(var, nextLine[5]);
                        if(! nextLine[7].equals("")) {
                            addLabel(var, nextLine[7]);
                        }
                    }
                }
                reader.close();
            } else {
                LOG.error("Missing embedded CSV file for configuration. MetadataFilter will be blank");
            }
        } catch(IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
