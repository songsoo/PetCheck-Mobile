package com.example.weatherm.Model;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;

//회원 정보 저장
public class MemberInfo implements Serializable {

    private String uid;
    public String date;
    public String photoUrl;
    public String nickname;
    public ArrayList<String> walkingList;
    public ArrayList<String> bookmarkRouteList;
    public ArrayList<String> routeNameList;
    public ArrayList<String> friendList;
    public String gender;
    public String myProfile;
    public LatLng address;

    public MemberInfo(String uid, String date, String photoUrl, String nickname,
                      ArrayList<String> walkingList, ArrayList<String> bookmarkRouteList,
                      ArrayList<String> routeNameList, ArrayList<String> friendList, String gender, String myProfile, LatLng address) {
        this.uid = uid;
        this.date = date;
        this.photoUrl = photoUrl;
        this.nickname = nickname;
        this.walkingList = walkingList;
        this.bookmarkRouteList = bookmarkRouteList;
        this.routeNameList = routeNameList;
        this.friendList = friendList;
        this.gender = gender;
        this.myProfile = myProfile;
        this.address = address;
    }

    public String getMyProfile() {
        return myProfile;
    }

    public void setMyProfile(String myProfile) {
        this.myProfile = myProfile;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public ArrayList<String> getFriendList() {
        return friendList;
    }

    public void setFriendList(ArrayList<String> friendList) {
        this.friendList = friendList;
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


    public String getDate(){
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPhotoUrl(){
        return photoUrl;
    }

    public String getNickname(){
        return nickname;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }


}
