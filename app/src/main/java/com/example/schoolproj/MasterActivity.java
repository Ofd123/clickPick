package com.example.schoolproj;

import static android.content.ContentValues.TAG;
import static com.example.schoolproj.FireBaseFiles.FBRef.refAuth;

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

public class MasterActivity extends AppCompatActivity
{

    protected enum Codes // when the user opens the app, the enum will take place in several classes depending on the user's logging state
    {
        ERROR, // something went wrong - put it just in case
        SIGN_IN,  // go to sign in screen
        LOG_IN,  // go to log in screen
        REMEMBER_ME, // connected - go to main screen
        LOGGED_OUT, // disconnected - go to sign in -> will help in future updates
        MAIN_SCREEN_REQUEST_CODE,
        SEARCH_REQUEST_CODE,
        GALLERY_REQUEST_CODE,
        CAMERA_REQUEST_CODE,
        CAMERA_PERMISSION_CODE

    }
    protected SharedPreferences settings;
    protected User connected_user = new User();
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
            public void handleOnBackPressed() {
                AlertDialog adb = new AlertDialog.Builder(MasterActivity.this).create();
                adb.setTitle("Are you sure you wish to exit?");
                adb.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", (dialog, which) -> {
                    finish();
                });
                adb.setButton(AlertDialog.BUTTON_NEGATIVE, "No", (dialog, which) -> {
                    dialog.cancel();
                });
                adb.show();
            }
        });


    }

    /**
     * Loads user data from SharedPreferences or Firebase Auth.
     * @return true if user is connected, false otherwise.
     */
    protected boolean loadUserData()
    {
        boolean stayConnected = settings.getBoolean("stayConnected", false);
        if (stayConnected)
        {
            String userID = settings.getString("userID", null);
            if (userID != null && !userID.isEmpty()) {
                String username = settings.getString("username", "User");
                connected_user.setUserID(userID);
                connected_user.setUsername(username);
                connected_user.setLastLogin(settings.getLong("lastLogin", System.currentTimeMillis()));
                connected_user.setCreationDate(settings.getLong("creationDate", 0));
                return true;
            }
        }

        // Fallback: Check if there's an active Firebase session even if "remember me" wasn't checked
        FirebaseUser fbUser = refAuth.getCurrentUser();
        if (fbUser != null) {
            connected_user.setUserID(fbUser.getUid());
            String email = fbUser.getEmail();
            connected_user.setUsername(email != null ? email.split("@")[0] : "User");
            return true;
        }

        return false;
    }
    protected boolean signout()
    {
        try
        {
            refAuth.signOut();
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


    public String jsonToString(JSONObject jsonObject) {
        if (jsonObject == null) return "";
        return jsonObject.toString();
    }

    public JSONObject stringToJsonObject(String jsonString) throws JSONException {
        return new JSONObject(jsonString);
    }
}
