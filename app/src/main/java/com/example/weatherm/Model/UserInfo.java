package com.example.weatherm.Model;

public class UserInfo {

    public UserInfo(){}


    public static String userId;
    private String location;
    public static String nickname;
    public static String profileImg;


    public UserInfo(String userId, String location,String nickname,String openTime,String profileImg,String BakeryName){
        this.userId=userId;
        this.location=location;
        this.nickname=nickname;
        this.profileImg=profileImg;

    }

    public String getUserId() { return this.userId; }
    public String getNickname() { return this.nickname; }
    public String getProfileImg() { return this.profileImg; }
    public String getLocation() { return this.location; }



    public void setUserId(String userId) { this.userId = userId; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public void setProfileImg(String profileImg) { this.profileImg = profileImg; }

}
