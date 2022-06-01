package com.example.weatherm.Model;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;

//회원 정보 저장
public class MemberInfo implements Serializable {

    private String name;
    private String phoneNumber;
    private String Date;
    private String adress;
    private String photoUrl;
    private String nickname;
    private ArrayList<String> walkingList;
    private ArrayList<String> bookmarkRouteList;
    private ArrayList<String> routeNameList;
    private LatLng address;



    public MemberInfo(String name, String phoneNumber, String adress, String Date, String photoUrl, String nickname, ArrayList<String> bookmarkRouteList,ArrayList<String> walkingList, ArrayList<String> routeNameList,LatLng address)
    {
        this.name=name;
        this.phoneNumber=phoneNumber;
        this.Date=Date;
        this.adress=adress;
        this.photoUrl = photoUrl;
        this.nickname=nickname;
        this.bookmarkRouteList = bookmarkRouteList;
        this.walkingList=walkingList;
        this.routeNameList=routeNameList;
        this.adress=adress;
    }

    public LatLng getAddress() {
        return address;
    }

    public void setAddress(LatLng address) {
        this.address = address;
    }

    public ArrayList<String> getWalkingList() {
        return walkingList;
    }

    public void setWalkingList(ArrayList<String> walkingList) {
        this.walkingList = walkingList;
    }

    public String getName(){
        return name;
    }

    public String getPhoneNumber(){
        return phoneNumber;
    }

    public String getDate(){
        return Date;
    }

    public String getAdress(){
        return adress;
    }

    public String getPhotoUrl(){
        return photoUrl;
    }

    public String getNickname(){
        return nickname;
    }



}
