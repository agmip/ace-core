package org.agmip.ace.util;

import ch.hsr.geohash.GeoHash;
import ch.hsr.geohash.WGS84Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GeoPoint {
    private static final Logger LOG = LoggerFactory.getLogger(GeoPoint.class);
    private String geohash;
    private String lat;
    private String lng;
    
    public GeoPoint(String geohash) {
        this.geohash = geohash;
        GeoHash g = GeoHash.fromGeohashString(geohash);
        WGS84Point point = g.getPoint();
        this.lat = getCoordinateComponent(point.getLatitude());
        this.lng = getCoordinateComponent(point.getLongitude());
    }
    
    public GeoPoint(String lat, String lng) {
        this.lat = lat;
        this.lng = lng;
        this.geohash = GeoPoint.calculateGeoHash(lat, lng);
    }
    
    public GeoPoint() {
        this.lat = null;
        this.lng = null;
        this.geohash = null;
    }

    public void setGeoHash(String geohash) {
        this.geohash = geohash;
    }
    
    public void setLatitude(String lat) {
        this.lat = lat;
    }
    
    public void setLongitude(String lng) {
        this.lng = lng;
    }
    
    public void setLat(String lat) {
        this.lat = lat;
    }
    
    public void setLng(String lng) {
        this.lng = lng;
    }
    
    public String getGeoHash() {
        return this.geohash;
    }
    
    public String getLatitude() {
        return this.lat;
    }
    
    public Double getLatitudeAsDouble() {
        return getWithinRangeAsDouble(this.lat, -90.0, 90.0);
    }
    
    public String getLat() {
        return this.lat;
    }
    
    public String getLongitude() {
        return this.lng;
    }
    
    public Double getLongitudeAsDouble() {
        return getWithinRangeAsDouble(this.lng, -180.0, 180.0);
    }
    
    public String getLng() {
        return this.lng;
    }

    private String getCoordinateComponent(double component) {
        try {
            return AceFormats.INSTANCE.getCoordinateFormat().format(component);
        } catch (NumberFormatException ex) {
            return null;
        }
    }
    
    
    public static String calculateGeoHash(String lat, String lng) {
        LOG.info("Calculating hash for: {},{}", lat, lng);
        Double latitude = getWithinRangeAsDouble(lat, -90.0, 90.0); 
        Double longitude = getWithinRangeAsDouble(lng, -180.0, 180.0);
        
        if (latitude != null && longitude != null) {
            GeoHash geohash = GeoHash.withCharacterPrecision(latitude, longitude, 7);
            return geohash.toBase32();
        } else {
            return null;
        }
    }
    
    public static Double getWithinRangeAsDouble(String s, double min, double max) {
        try {
            double d = Double.parseDouble(s);
            if (d < min || d > max) {
                return null;
            } else {
                return Double.valueOf(d);
            }
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
