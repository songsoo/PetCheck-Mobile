package com.example.weatherm.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

//자유게시글 정보 를 저장하기 위한 유형
//제목, 내용, 작성자, 유저이름, 작성일자, 추천수, 댓글, 작성글 id, 추천유저 id값을 저장.
public class FreePostInfo implements Serializable, Comparable<FreePostInfo> {

    private String content;
    private String publisherId;
    private String publisherName;
    private ArrayList<String> imageList;
    private Date createdAt;
    private long recom;
    private ArrayList<String> comment;
    private String postId;
    private ArrayList<String> recomUserId;
    private String category;
    private String nickname;
    private String routeInfoId;

    public String getRouteInfoId() {
        return routeInfoId;
    }

    public void setRouteInfoId(String routeInfoId) {
        this.routeInfoId = routeInfoId;
    }

    public FreePostInfo(String content, String publisherId, String publisherName, Date createdAt,
                        long recom, ArrayList<String> comment, String postId, ArrayList<String> recomUserId, ArrayList<String> imageList, String category, String nickname,String routeInfoId){

        this.content = content;
        this.publisherId = publisherId;
        this.publisherName = publisherName;
        this.createdAt = createdAt;
        this.recom = recom;
        this.comment = comment;
        this.postId = postId;
        this.recomUserId = recomUserId;
        this.imageList=imageList;
        this.category=category;
        this.nickname=nickname;
        this.routeInfoId=routeInfoId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public FreePostInfo(String content, String publisherId, String publisherName, Date createdAt, long recom, ArrayList<String> comment, String postId, ArrayList<String> recomUserId, String category, String nickname,String routeInfoId) {

        this.content = content;
        this.publisherId = publisherId;
        this.publisherName = publisherName;
        this.createdAt = createdAt;
        this.recom = recom;
        this.comment = comment;
        this.postId = postId;
        this.recomUserId = recomUserId;
        this.category = category;
        this.nickname=nickname;
        this.routeInfoId=routeInfoId;
    }



    public ArrayList<String> getImageList() {
        return imageList;
    }

    public void setImageList(ArrayList<String> imageList) {
        this.imageList = imageList;
    }

    public String getContent(){ return  this.content;}
    public void setContent(String content){this.content = content;}
    public String getPublisherName(){return this.publisherName;}
    public void setPublisherName(String publisher){this.publisherName = publisher;}

    public String getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(String publisherId) {
        this.publisherId = publisherId;
    }

    public Date getCreatedAt(){return this.createdAt;}
    public void setCreatedAt(Date publisher){this.createdAt = createdAt;}
    public long getRecom(){return this.recom;}
    public void setRecom(long recom){this.recom = recom;}
    public ArrayList<String> getComment(){return this.comment;}
    public void setComment(ArrayList<String> comment){this.comment = comment;}
    public String getPostId(){ return  this.postId;}
    public void setPostId(String postId){this.postId = postId;}
    public String getCategory(){ return  this.category;}
    public void setCategory(String category){this.category = category;}
    public ArrayList<String> getRecomUserId(){return this.recomUserId;}
    public void setRecomUserId(ArrayList<String> recomUserId){this.recomUserId = recomUserId;}

    @Override
    public int compareTo(FreePostInfo otherPost) {
        if (this.getCreatedAt().compareTo(otherPost.getCreatedAt())>0) {
            return -1;
        }else if(this.getCreatedAt().compareTo(otherPost.getCreatedAt())==0){
            return 0;

        }else{
            return 1;
        }


    }

}