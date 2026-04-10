package com.example.schoolproj.FireBaseFiles;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Utility class for holding static references to Firebase services and database paths.
 * Centralizes authentication and database reference management.
 */
public class FBRef
{
    /** Singleton reference to Firebase Authentication. */
    public static FirebaseAuth refAuth = FirebaseAuth.getInstance();

    /**
     * Singleton reference to the Firebase Realtime Database.
     * Explicitly initialized with the project's database URL.
     */
    public static FirebaseDatabase FBDB = FirebaseDatabase.getInstance("https://schoolprojbeta-default-rtdb.firebaseio.com");
    
    static {
        try {
            // Disable persistence temporarily to ensure we see real-time server errors
            FBDB.setPersistenceEnabled(false);
        } catch (Exception ignored) {}
    }

    /** Reference to the 'User' node in the database. */
    public static DatabaseReference userRef = FBDB.getReference("User");
    /** Reference to the 'SearchHistory' node in the database. */
    public static DatabaseReference searchHistoryRef = FBDB.getReference("SearchHistory");
    /** Reference to the 'savedItems' node (favorites) in the database. */
    public static DatabaseReference favoritesRef = FBDB.getReference("savedItems");
}
