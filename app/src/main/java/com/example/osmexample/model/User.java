package com.example.osmexample.model;

import com.example.osmexample.presenter.Marker;

import java.util.HashSet;
import java.util.Set;

public class User {
    private String nickname;
    private String email;
    private String password;
    private Set<MarkerInfo> markers;
    private Set<RouteInfo> routes;
    private Set<RouteInfo> tracks;

    public User(String nickname, String email, String password) {
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.markers = new HashSet<>();
        this.markers = new HashSet<>();
        this.markers = new HashSet<>();
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<MarkerInfo> getMarkers() {
        return markers;
    }

    public void setMarkers(Set<MarkerInfo> markers) {
        this.markers = markers;
    }

    public Set<RouteInfo> getRoutes() {
        return routes;
    }

    public void setRoutes(Set<RouteInfo> routes) {
        this.routes = routes;
    }

    public Set<RouteInfo> getTracks() {
        return tracks;
    }

    public void setTracks(Set<RouteInfo> tracks) {
        this.tracks = tracks;
    }
}
