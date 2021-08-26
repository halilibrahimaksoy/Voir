package com.halilibrahimaksoy.voir.Model;

import java.util.Date;

/**
 * Created by Halil ibrahim AKSOY on 21.12.2015.
 */
public class HeadLineItem {
    private String Id;
    private String UserId;
    private String HeadLineText;
    private Date Date;
    private int FeedCount;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getHeadLineText() {
        return HeadLineText;
    }

    public void setHeadLineText(String headLineText) {
        HeadLineText = headLineText;
    }

    public java.util.Date getDate() {
        return Date;
    }

    public void setDate(java.util.Date date) {
        Date = date;
    }

    public int getFeedCount() {
        return FeedCount;
    }

    public void setFeedCount(int feedCount) {
        FeedCount = feedCount;
    }


}
