package com.cuvas.bustrackingapp.model;

import com.google.type.LatLng;

public class PointModel {
    String id;
    String route;

    public PointModel() {
    }

    public PointModel(String id, String route) {
        this.id = id;
        this.route = route;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }
}