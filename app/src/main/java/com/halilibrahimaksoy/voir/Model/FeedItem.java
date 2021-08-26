package com.halilibrahimaksoy.voir.Model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Halil ibrahim AKSOY on 11.11.2015.
 */
public class FeedItem {
    private String UserName;
    private String Title;
    private String Description;
    private Date Date;
    private String FeedImageUrl;
    private String FeedVideoUrl;
    private String Categori;
    private String Id;
    private String Location;
    private String Like = "";
    private String Dislike = "";
    private String UserProfilImageUrl;
    private String UserId;

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getUserProfilImageUrl() {
        return UserProfilImageUrl;
    }

    public void setUserProfilImageUrl(String userProfilImageUrl) {
        UserProfilImageUrl = userProfilImageUrl;
    }

    public String getLike() {
        return Like;
    }

    public void setLike(String like) {
        Like = like;
    }

    public String getDislike() {
        return Dislike;
    }

    public void setDislike(String dislike) {
        Dislike = dislike;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String sender) {
        UserName = sender;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public java.util.Date getDate() {
        return Date;
    }

    public void setDate(java.util.Date date) {
        Date = date;
    }

    public String getFeedImageUrl() {
        return FeedImageUrl;
    }

    public void setFeedImageUrl(String feedImageUrl) {
        FeedImageUrl = feedImageUrl;
    }

    public String getFeedVideoUrl() {
        return FeedVideoUrl;
    }

    public void setFeedVideoUrl(String feedVideoUrl) {
        FeedVideoUrl = feedVideoUrl;
    }

    public String getCategori() {
        return Categori;
    }

    public void setCategori(String categori) {
        Categori = categori;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }


    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }
}
