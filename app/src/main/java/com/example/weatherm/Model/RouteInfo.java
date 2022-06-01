package com.example.weatherm.Model;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class RouteInfo implements Serializable {


    private ArrayList<LatLng> ListStartLatLng;
    private ArrayList<LatLng> ListEndLatLng;
    private ArrayList<LatLng> ListTrashLatLng;
    private ArrayList<LatLng> ListWarningLatLng;
    private double totalDistance;
    private long totalTime;
    private Date walkingDate;
    private String walkingContent;


    public RouteInfo(ArrayList<LatLng> listStartLatLng, ArrayList<LatLng> listEndLatLng, double totalDistance, long totalTime, Date walkingDate, String walkingContent,ArrayList<LatLng> ListTrashLatLng,
          ArrayList<LatLng> ListWarningLatLng) {
        ListStartLatLng = listStartLatLng;
        ListEndLatLng = listEndLatLng;
        this.totalDistance = totalDistance;
        this.totalTime = totalTime;
        this.walkingDate = walkingDate;
        this.walkingContent = walkingContent;
        this.ListTrashLatLng=ListTrashLatLng;
        this.ListWarningLatLng=ListWarningLatLng;
    }


    public ArrayList<LatLng> getListTrashLatLng() {
        return ListTrashLatLng;
    }

    public void setListTrashLatLng(ArrayList<LatLng> listTrashLatLng) {
        ListTrashLatLng = listTrashLatLng;
    }

    public ArrayList<LatLng> getListWarningLatLng() {
        return ListWarningLatLng;
    }

    public void setListWarningLatLng(ArrayList<LatLng> listWarningLatLng) {
        ListWarningLatLng = listWarningLatLng;
    }

    public ArrayList<LatLng> getListStartLatLng() {
        return this.ListStartLatLng;
    }

    public void setListStartLatLng2(ArrayList<LatLng> listStartLatLng2) {
        this.ListStartLatLng = listStartLatLng2;
    }

    public ArrayList<LatLng> getListEndLatLng() {
        return this.ListEndLatLng;
    }

    public void setListEndLatLng(ArrayList<LatLng> listEndLatLng) {
        this.ListEndLatLng = listEndLatLng;
    }

    public double getTotalDistance() {
        return this.totalDistance;
    }

    public void setTotalDistance(double totalDistance) {
        this.totalDistance = totalDistance;
    }

    public long getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(long totalTime) {
        this.totalTime = totalTime;
    }

    public Date getWalkingDate() {
        return this.walkingDate;
    }

    public void setWalkingDate(Date walkingDate) {
        this.walkingDate = walkingDate;
    }

    public String getWalkingContent() {
        return this.walkingContent;
    }

    public void setWalkingContent(String walkingContent) {
        this.walkingContent = walkingContent;
    }
}
