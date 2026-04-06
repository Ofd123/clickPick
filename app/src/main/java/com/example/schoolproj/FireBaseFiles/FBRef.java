package com.example.schoolproj.FireBaseFiles;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

    public class FBRef
    {
        public static FirebaseAuth refAuth = FirebaseAuth.getInstance();

        // Explicitly set the database URL to match your google-services.json
        public static FirebaseDatabase FBDB = FirebaseDatabase.getInstance("https://schoolprojbeta-default-rtdb.firebaseio.com");
        
        static {
            try {
                // Disable persistence temporarily to ensure we see real-time server errors
                FBDB.setPersistenceEnabled(false);
            } catch (Exception ignored) {}
        }

        public static DatabaseReference userRef = FBDB.getReference("User");
        public static DatabaseReference searchHistoryRef = FBDB.getReference("SearchHistory");
        public static DatabaseReference favoritesRef = FBDB.getReference("savedItems");
    }
