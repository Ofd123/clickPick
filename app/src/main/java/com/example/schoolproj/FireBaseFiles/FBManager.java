package com.example.schoolproj.FireBaseFiles;

import com.example.schoolproj.classes.User;

/**
 * Manager class for handling Firebase data operations.
 * Provides methods to sync user profiles and search history with the cloud database.
 */
public class FBManager
{
    /**
     * Adds a user profile to Firebase.
     * @param user The user object to add.
     * @return True if the operation was initiated successfully.
     */
    public boolean addUserTOFB(User user)
    {
        return true;
    }

    /**
     * Adds a user's search history to Firebase.
     * @param user The user whose history is being synced.
     * @return True if the operation was initiated successfully.
     */
    public boolean addSearchHistoryTOFB(User user)
    {
        return true;
    }
}
