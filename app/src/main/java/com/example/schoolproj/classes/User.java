package com.example.schoolproj.classes;


import com.google.firebase.database.ServerValue;
import java.util.Map;

/**
 * Data model representing a user in the system.
 * Stores user profile information and login activity timestamps.
 */
public class User {
    /** Unique identifier for the user (typically from Firebase Auth). */
    String userID;
    /** The display name or identifier chosen by the user. */
    String username;
    /** Timestamp of the user's most recent login. */
    long lastLogin;
    /** Timestamp of when the user account was created. */
    long creationDate;

    /**
     * Default constructor required for Firebase Realtime Database.
     */
    public User()
    {
    }

    /**
     * Constructor specifically for setting a username.
     * Initializes creation and last login dates to the current system time.
     * @param username The user's chosen display name.
     */
    public User(String username)
    {
        this.username = username;
        // 2. Set current local time in milliseconds
        this.creationDate = System.currentTimeMillis();
        this.lastLogin = System.currentTimeMillis();
    }

    /**
     * Constructor for a full user profile.
     * @param userID Unique user ID.
     * @param username Display name.
     */
    public User(String userID, String username) {
        this.userID = userID;
        this.username = username;
        // 2. Set current local time in milliseconds
        this.creationDate = System.currentTimeMillis();
        this.lastLogin = System.currentTimeMillis();
    }

    /**
     * Returns a special map representing the Firebase server timestamp.
     * Use this when writing to the database to ensure synchronization with server time.
     * @return A map compatible with ServerValue.TIMESTAMP.
     */
    public static Map<String, String> getFirebaseTimestamp()
    {
        return ServerValue.TIMESTAMP;
    }

    /** @param lastLogin The new last login timestamp. */
    public void setLastLogin(long lastLogin)
    {
        this.lastLogin = lastLogin;
    }

    /** @return The user ID. */
    public String getUserID()
    {
        return userID;
    }

    /** @param userID The new user ID. */
    public void setUserID(String userID)
    {
        this.userID = userID;
    }

    /** @param username The new username. */
    public void setUsername(String username)
    {
        this.username = username;
    }

    /** @return The username. */
    public String getUsername()
    {
        return username;
    }

    /** @return The last login timestamp. */
    public long getLastLogin() {
        return lastLogin;
    }

    /** @return The account creation timestamp. */
    public long getCreationDate() {
        return creationDate;
    }

    /** @param creationDate The new creation date timestamp. */
    public void setCreationDate(long creationDate) {
        this.creationDate = creationDate;
    }
}
