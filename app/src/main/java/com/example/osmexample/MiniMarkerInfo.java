package com.example.osmexample;

import java.time.LocalDateTime;

public class MiniMarkerInfo {
    private final double latitude;
    private final double longitude;
    private final String name;
    private final String description;
    private final MarkerType markerType;
    private final LocalDateTime dateTime;


    public MiniMarkerInfo(double latitude, double longitude, String name, String description, MarkerType markerType, LocalDateTime dateTime) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.description = description;
        this.markerType = markerType;
        this.dateTime = dateTime;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public MarkerType getMarkerType() {
        return markerType;
    }

    public String getName() {
        return name;
    }


    public String getDescription() {
        return description;
    }


    public LocalDateTime getDateTime() {
        return dateTime;
    }
}
