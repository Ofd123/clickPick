package com.example.schoolproj.classes;


import com.google.firebase.database.ServerValue;
import java.util.Map;

public class User {
    String userID;
    String username;
    long lastLogin;
    long creationDate;

    public User()
    {
    }
    public User(String username)
    {
        this.username = username;
        // 2. Set current local time in milliseconds
        this.creationDate = System.currentTimeMillis();
        this.lastLogin = System.currentTimeMillis();
    }

    public User(String userID, String username) {
        this.userID = userID;
        this.username = username;
        // 2. Set current local time in milliseconds
        this.creationDate = System.currentTimeMillis();
        this.lastLogin = System.currentTimeMillis();
    }

    // 3. To get the most accurate time from Firebase Server
    // you can use this Map when writing to the database
    public static Map<String, String> getFirebaseTimestamp()
    {
        return ServerValue.TIMESTAMP;
    }

    public void setLastLogin(long lastLogin)
    {
        this.lastLogin = lastLogin;
    }

    public String getUserID()
    {
        return userID;
    }
    public void setUserID(String userID)
    {
        this.userID = userID;
    }
    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getUsername()
    {
        return username;
    }

    public long getLastLogin() {
        return lastLogin;
    }

    public long getCreationDate() {
        return creationDate;
    }
    public void setCreationDate(long creationDate) {
        this.creationDate = creationDate;
    }
}
