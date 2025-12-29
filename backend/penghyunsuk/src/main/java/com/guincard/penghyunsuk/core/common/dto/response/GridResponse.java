package com.guincard.penghyunsuk.common.dto.response;

public class GridResponse {
    double lat;
    double lon;
    int nx;
    int ny;

    public GridResponse(double lat, double lon, int x, int y) {
        this.lat = lat;
        this.lon = lon;
        this.nx = x;
        this.ny = y;
    }
}