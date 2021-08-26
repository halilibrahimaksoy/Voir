package com.halilibrahimaksoy.voir.Model;

import java.io.Serializable;

/**
 * Created by Halil ibrahim AKSOY on 30.12.2015.
 */
public class UserItem implements Serializable {
    private String Id;
    private String UserName;
    private String Name;
    private String Mail;
    private String Description;
    private String UserProfilImageUrl;
    private int UserLikeCounter;
    private String Password;

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getMail() {
        return Mail;
    }

    public void setMail(String mail) {
        Mail = mail;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getUserProfilImageUrl() {
        return UserProfilImageUrl;
    }

    public void setUserProfilImageUrl(String userProfilImageUrl) {
        UserProfilImageUrl = userProfilImageUrl;
    }

    public int getUserLikeCounter() {
        return UserLikeCounter;
    }

    public void setUserLikeCounter(int userLikeCounter) {
        UserLikeCounter = userLikeCounter;
    }
}
