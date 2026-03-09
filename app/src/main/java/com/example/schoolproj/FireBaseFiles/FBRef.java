package com.example.schoolproj.FireBaseFiles;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

    public class FBRef
    {
        public static FirebaseAuth refAuth = FirebaseAuth.getInstance();

        public static FirebaseDatabase FBDB = FirebaseDatabase.getInstance();
        public static DatabaseReference userRef = FBDB.getReference("User");
        public static DatabaseReference searchHistoryRef = FBDB.getReference("SearchHistory");
        public static DatabaseReference favoritesRef = FBDB.getReference("savedItems");
    }
