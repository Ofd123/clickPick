package com.example.schoolproj;

import static android.content.ContentValues.TAG;
import static com.example.schoolproj.FireBaseFiles.FBRef.refAuth;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.schoolproj.classes.User;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Base Activity class for the application.
 * Centralizes authentication state, session management, and common utility methods.
 * All other screens in the app should extend this activity for consistent behavior.
 */
public class MasterActivity extends AppCompatActivity
{

    /**
     * Enumeration of request and result codes used for activity transitions and state management.
     */
    protected enum Codes // when the user opens the app, the enum will take place in several classes depending on the user's logging state
    {
        /** Generic error state. */
        ERROR,
        /** Request code for the sign-up screen. */
        SIGN_IN,
        /** Request code for the login screen. */
        LOG_IN,
        /** Result code indicating a successful "Remember Me" session. */
        REMEMBER_ME,
        /** State indicating the user has logged out. */
        LOGGED_OUT,
        /** Request code for the main screen. */
        MAIN_SCREEN_REQUEST_CODE,
        /** Request code for the keyword search screen. */
        SEARCH_REQUEST_CODE,
        /** Request code for the system gallery. */
        GALLERY_REQUEST_CODE,
        /** Request code for the system camera. */
        CAMERA_REQUEST_CODE,
        /** Request code for camera permission prompts. */
        CAMERA_PERMISSION_CODE

    }

    /** Persistent storage for application settings and session data. */
    protected SharedPreferences settings;
    /** Flag indicating if the user has authenticated during the current app lifecycle. */
    protected static boolean isLoggedInThisSession = false;
    /** The currently authenticated user's profile data. */
    protected User connected_user = new User();

    /**
     * Called when the activity is starting.
     * Initializes shared preferences, loads user data, and sets up global back button behavior.
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        settings = getSharedPreferences("settings", MODE_PRIVATE);

        loadUserData();

        // Handle the back button press TODO: might change - Master activity will just ignore it
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() 
            {
                AlertDialog adb = new AlertDialog.Builder(MasterActivity.this).create();
                adb.setTitle("Are you sure you wish to exit?");
                adb.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishAffinity();
                    }
                });
                adb.setButton(AlertDialog.BUTTON_NEGATIVE, "No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                adb.show();
            }
        });


    }

    /**
     * Synchronizes the local user state with SharedPreferences and Firebase Auth.
     * @return True if a valid user session exists and data was loaded successfully.
     */
    protected boolean loadUserData()
    {
        boolean stayConnected = settings.getBoolean("stayConnected", false);
        FirebaseUser fbUser = refAuth.getCurrentUser();

        if (fbUser == null)
        {
            isLoggedInThisSession = false;
            return false;
        }

        if (stayConnected || isLoggedInThisSession)
        {
            String userID = settings.getString("userID", fbUser.getUid());

            String username;
            String defaultUsername = "User";
            if (fbUser.getEmail() != null) {
                defaultUsername = fbUser.getEmail().split("@")[0];
            }
            username = settings.getString("username", defaultUsername);

            connected_user.setUserID(userID);
            connected_user.setUsername(username);
            connected_user.setLastLogin(settings.getLong("lastLogin", System.currentTimeMillis()));
            connected_user.setCreationDate(settings.getLong("creationDate", 0));
            return true;
        }

        // If we have a Firebase user but 'Remember Me' wasn't checked and it's a new session
        refAuth.signOut();
        return false;
    }

    /**
     * Signs the user out of Firebase and clears all local session data from SharedPreferences.
     * @return True if sign-out was successful.
     */
    protected boolean signout()
    {
        try
        {
            refAuth.signOut();
            isLoggedInThisSession = false;
            connected_user = new User();
            // Clear SharedPreferences
            SharedPreferences.Editor editor = settings.edit();
            editor.remove("stayConnected");
            editor.remove("userID");
            editor.remove("username");
            editor.remove("lastLogin");
            editor.remove("creationDate");
            editor.apply();
            return true;
        }
        catch (Exception e)
        {
            Log.e(TAG, "signout error: " + e.getMessage());
            return false;
        }
    }


    /**
     * Serializes a JSONObject to its string representation.
     * @param jsonObject The object to serialize.
     * @return A string representation of the JSON, or an empty string if null.
     */
    public String jsonToString(JSONObject jsonObject) {
        if (jsonObject == null) {
            return "";
        }
        return jsonObject.toString();
    }

    /**
     * Parses a JSON string into a JSONObject.
     * @param jsonString The string to parse.
     * @return A JSONObject parsed from the string.
     * @throws JSONException If the string is not valid JSON.
     */
    public JSONObject stringToJsonObject(String jsonString) throws JSONException {
        return new JSONObject(jsonString);
    }
}
