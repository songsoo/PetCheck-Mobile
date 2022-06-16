package com.example.weatherm.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class CommentInfo implements Serializable {
    private String commentId;
    private String publisherId;
    private String publisher;
    private String content;
    private ArrayList<String> imageList;
    private Date createdTime;
    private String profileImage;
    private String postId;

    public CommentInfo(String commentId, String publisherId, String publisher, String content, Date createdTime, String profileImage, String postId) {
        this.commentId = commentId;
        this.publisherId = publisherId;
        this.publisher = publisher;
        this.content = content;
        this.createdTime = createdTime;
        this.profileImage = profileImage;
        this.postId = postId;
        this.imageList=null;
    }

    public CommentInfo(String commentId, String publisherId, String publisher, String content, ArrayList<String> imageList, Date createdTime, String profileImage, String postId) {
        this.commentId = commentId;
        this.publisherId = publisherId;
        this.publisher = publisher;
        this.content = content;
        this.imageList = imageList;
        this.createdTime = createdTime;
        this.profileImage = profileImage;
        this.postId = postId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public String getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(String publisherId) {
        this.publisherId = publisherId;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ArrayList<String> getImageList() {
        return imageList;
    }

    public void setImageList(ArrayList<String> imageList) {
        this.imageList = imageList;
    }
}
