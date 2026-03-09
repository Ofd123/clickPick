package com.example.schoolproj;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.schoolproj.classes.User;

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
    protected User connected_user;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        settings = getSharedPreferences("settings", MODE_PRIVATE);

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
    public String jsonToString(JSONObject jsonObject) {
        if (jsonObject == null) return "";
        return jsonObject.toString();
    }

    public JSONObject stringToJsonObject(String jsonString) throws JSONException {
        return new JSONObject(jsonString);
    }
}
