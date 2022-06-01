package com.example.weatherm;

import android.app.Application;

import com.google.android.gms.maps.model.Polyline;

import java.util.List;

public class userProfile extends Application {

    private String userID;

    public void onCreate(){
        super.onCreate();
    }
    @Override
    public void onTerminate() {

        super.onTerminate();
    }

    public String getUserID(){
        return userID;
    }
    public void setUserID(String id){
        userID = id;
    }


}