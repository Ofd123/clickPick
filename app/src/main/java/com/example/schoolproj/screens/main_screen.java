package com.example.schoolproj.screens;

import static android.content.ContentValues.TAG;
import static com.example.schoolproj.GeminiRelevant.Prompts.GET_DATA_FROM_IMAGE;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.example.schoolproj.GeminiRelevant.GeminiCallback;
import com.example.schoolproj.GeminiRelevant.GeminiManager;
import com.example.schoolproj.MasterActivity;
import com.example.schoolproj.R;
import com.example.schoolproj.classes.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Dictionary;

public class main_screen extends MasterActivity
{
    GeminiManager geminiManager;
    JSONObject searchDetails; //https://www.geeksforgeeks.org/java/working-with-json-data-in-java/
    //https://www.w3schools.com/js/js_json_objects.asp
    Intent signinIntent,loginIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        //check if the user singed in
        //if not, signin/login

        Boolean stayConnected = settings.getBoolean("stayConnected", false);
        if(stayConnected)
        {
            connectedUser = new User(settings.getString("userID", ""), settings.getString("username", ""));
            connectedUser.setLastLogin(settings.getLong("lastLogin", 0));
            connectedUser.setCreationDate(settings.getLong("creationDate", 0));
        }
        else
        {
            signinIntent = new Intent(this, signUp_screen.class);
            loginIntent = new Intent(this, login_screen.class);
            startActivityForResult(signinIntent, Codes.SIGN_IN.ordinal());


        }

    }
    // ---------------------------------------------------------------------------------------------
    public void regularSearch(View view)
    {
        Intent intent = new Intent(this, search_screen.class);
        startActivityForResult(intent, Codes.SEARCH_REQUEST_CODE.ordinal());
    }
    // ---------------------------------------------------------------------------------------------
    public void imageSearch(View view)
    {
        try
        {
            String[] options = {"Camera", "Gallery"};

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select Image Source");
            builder.setItems(options, (dialog, which) -> {
                switch (which)
                {
                    case 0:
                        openCamera();
                        dialog.dismiss(); // might switch to cancel
                    case 1:
                        openGallery();
                        dialog.dismiss();
                }
            });

            // handle canceling the dialog
            builder.setOnCancelListener(dialog -> {
                Toast.makeText(main_screen.this, "Image selection cancelled.", Toast.LENGTH_SHORT).show();
                throw new RuntimeException("Image selection cancelled.");
            });
            builder.create().show();


        }
        catch (Exception e)
        {
            Log.i("status:", e.toString());
        }
    }
    // ---------------------------------------------------------------------------------------------
    public void openCamera()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null)
        {
            startActivityForResult(intent, Codes.CAMERA_REQUEST_CODE.ordinal());
        }
        else
        {
            Toast.makeText(this, "No camera app found.", Toast.LENGTH_SHORT).show();
        }
    }
    // ---------------------------------------------------------------------------------------------
    public void openGallery()
    {
        try
        {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, Codes.GALLERY_REQUEST_CODE.ordinal());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.i("Error", e.toString());
        }
    }
    // ---------------------------------------------------------------------------------------------
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        // ------------------------------------------------------------------------
        // check if sign in result
        if(resultCode == RESULT_OK && requestCode == Codes.SIGN_IN.ordinal())
        {
            if (data != null && data.getExtras() != null)
            {
                int state = data.getIntExtra("state", Codes.ERROR.ordinal());
                if (state == Codes.LOG_IN.ordinal())
                {
                    startActivityForResult(loginIntent, Codes.LOG_IN.ordinal());
                }
            }
            return;
        }
        // ------------------------------------------------------------------------
        if(resultCode == RESULT_OK && requestCode == Codes.LOG_IN.ordinal())
        {
            if (data != null && data.getExtras() != null)
            {
                int state = data.getIntExtra("state", Codes.ERROR.ordinal());
                if (state == Codes.SIGN_IN.ordinal())
                {
                    startActivityForResult(signinIntent, Codes.SIGN_IN.ordinal());
                }
            }
            return;
        }
        // ------------------------------------------------------------------------
        Bitmap imageBitmap = null;
        // check camera result
        if (resultCode == RESULT_OK && requestCode == Codes.CAMERA_REQUEST_CODE.ordinal())
        {
            if (data != null && data.getExtras() != null)
            {
                imageBitmap = (Bitmap) data.getExtras().get("data");
            }
            else
            {
                Toast.makeText(this, "No picture was sent.", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Image bitmap is null");
                return;
            }
        }
        // ------------------------------------------------------------------------
        // check gallery result
        // i did not combine it with the camera because i might implement the ability to share several images from the gallery while you can only take 1 picture from the camera at a time
        else if (requestCode == Codes.GALLERY_REQUEST_CODE.ordinal() && resultCode == RESULT_OK && data != null)
        {
            if (data != null && data.getExtras() != null)
            {
                imageBitmap = (Bitmap) data.getExtras().get("data");
            }
            else
            {
                Toast.makeText(this, "No image captured.", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        else
        {
            Toast.makeText(this, "No Image Selected", Toast.LENGTH_SHORT).show();
        }
        // ------------------------------------------------------------------------
        // analyze picture with gemini
        if (imageBitmap != null)
        {
            ProgressDialog pd = new ProgressDialog(this);
            pd.setTitle("Analyzing Image...");
            pd.show();

            String prompt = GET_DATA_FROM_IMAGE;
            geminiManager.sendTextWithPhotoPrompt(prompt, imageBitmap, new GeminiCallback() {
                @Override
                public void onSuccess(String result)
                {
                    pd.dismiss();
                    try
                    {
                        searchDetails = new JSONObject(result);
                        Intent intent = new Intent(main_screen.this, search_screen.class);
                        intent.putExtra("searchDetails", searchDetails.toString());
                        startActivity(intent);
                    }
                    catch (JSONException e)
                    {
                        Toast.makeText(main_screen.this, "JSON Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Throwable error)
                {
                    pd.dismiss();
                    Log.e(TAG, "onActivityResult/Error: " + error.getMessage());
                }
            });
        }
        else
        {
            Toast.makeText(this, "No picture was sent.", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Intent data or extras are null");
        }
    }
    // ---------------------------------------------------------------------------------------------
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == Codes.CAMERA_PERMISSION_CODE.ordinal())
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                openCamera();
            }
            else
            {
                Toast.makeText(this, "Camera permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    // ---------------------------------------------------------------------------------------------

    public void searchHistory(View view) {
    }
    // ---------------------------------------------------------------------------------------------

    public void favorites(View view) {
    }
    // ---------------------------------------------------------------------------------------------

    public void allHistory(View view) {
    }
    // ---------------------------------------------------------------------------------------------

    public void goToSettings(View view) {
    }
}