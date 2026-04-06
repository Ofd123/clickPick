package com.example.schoolproj.screens;

import static android.content.ContentValues.TAG;
import static com.example.schoolproj.GeminiRelevant.Prompts.GET_DATA_FROM_IMAGE;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class main_screen extends MasterActivity
{
    GeminiManager geminiManager;
    JSONObject searchDetails;
    Intent signinIntent, loginIntent;
    private boolean isAuthRedirecting = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        // Initialize GeminiManager
        geminiManager = GeminiManager.getInstance();

        // Pre-initialize auth intents
        signinIntent = new Intent(this, signUp_screen.class);
        loginIntent = new Intent(this, login_screen.class);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        // Check if user is still logged in every time we return to this screen,
        // unless we are currently in the middle of a signup/login switch.
        if (!isAuthRedirecting)
        {
            loadData();
        }
        isAuthRedirecting = false;
    }

    void loadData()
    {
        if (!loadUserData())
        {
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
    public void showAlert()
    {
        AlertDialog.Builder secondChance = new AlertDialog.Builder(this);
        secondChance.setTitle("Please make sure");
        secondChance.setMessage("providing a corrupted image or an image where it is hard to understand what is being searched for may result in poor results, please send the most detailed picture that you can");
        secondChance.setPositiveButton("continue", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
                selectImageSource();
            }
        });
        secondChance.setNegativeButton("back", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });
        secondChance.show();
    }
    // ---------------------------------------------------------------------------------------------
    public void imageSearch(View view)
    {
        showAlert();
    }

    private void selectImageSource()
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
                        dialog.dismiss();
                        break;
                    case 1:
                        openGallery();
                        dialog.dismiss();
                        break;
                }
            });

            builder.setOnCancelListener(dialog -> {
                Toast.makeText(main_screen.this, "Image selection cancelled.", Toast.LENGTH_SHORT).show();
            });
            builder.create().show();
        }
        catch (Exception e)
        {
            Log.e(TAG, "selectImageSource error: " + e.getMessage());
        }
    }
    // ---------------------------------------------------------------------------------------------
    public void openCamera()
    {
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, Codes.CAMERA_PERMISSION_CODE.ordinal());
        }
        else
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
            Log.e("Error", e.toString());
        }
    }
    // ---------------------------------------------------------------------------------------------
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK)
        {
            // Handle Login/Signup flow
            if (requestCode == Codes.SIGN_IN.ordinal() || requestCode == Codes.LOG_IN.ordinal())
            {
                int state = data != null ? data.getIntExtra("state", Codes.ERROR.ordinal()) : Codes.ERROR.ordinal();

                if (state == Codes.LOG_IN.ordinal())
                {
                    isAuthRedirecting = true;
                    startActivityForResult(loginIntent, Codes.LOG_IN.ordinal());
                }
                else if (state == Codes.SIGN_IN.ordinal())
                {
                    isAuthRedirecting = true;
                    startActivityForResult(signinIntent, Codes.SIGN_IN.ordinal());
                }
                else if (state == Codes.REMEMBER_ME.ordinal())
                {
                    loadUserData(); // Refresh user from SharedPreferences
                    Toast.makeText(this, "Welcome " + (connected_user != null ? connected_user.getUsername() : ""), Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // Handle Camera/Gallery results
            Bitmap imageBitmap = null;
            if (requestCode == Codes.CAMERA_REQUEST_CODE.ordinal())
            {
                if (data != null && data.getExtras() != null)
                {
                    imageBitmap = (Bitmap) data.getExtras().get("data");
                }
            }
            else if (requestCode == Codes.GALLERY_REQUEST_CODE.ordinal())
            {
                if (data != null && data.getData() != null)
                {
                    Uri imageUri = data.getData();
                    try {
                        imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    } catch (IOException e) {
                        Log.e(TAG, "Error loading gallery image: " + e.getMessage());
                    }
                }
            }

            if (imageBitmap != null)
            {
                analyzeImage(imageBitmap);
            }
        }
    }

    private void analyzeImage(Bitmap imageBitmap)
    {
        ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Analyzing Image...");
        pd.setCancelable(false);
        pd.show();

        String prompt = GET_DATA_FROM_IMAGE;
        if (geminiManager != null) {
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
                    Log.e(TAG, "analyzeImage failure: " + error.getMessage());
                }
            });
        } else {
            pd.dismiss();
            Toast.makeText(this, "Gemini Manager not initialized", Toast.LENGTH_SHORT).show();
        }
    }

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

    public void searchHistory(View view)
    {
        Intent intent = new Intent(this, search_history_screen.class);
        startActivity(intent);
    }

    public void favorites(View view)
    {
        Intent intent = new Intent(this,saved_items_screen.class);
        startActivity(intent);
    }
    public void allHistory(View view)
    {
        Intent intent = new Intent(this,show_all_history_screen.class);
        startActivity(intent);
    }
    public void goToSettings(View view)
    {
        Intent intent = new Intent(this, settings_screen.class);
        startActivity(intent);
    }

    public void credits(View view)
    {
        Intent intent = new Intent(this, credits_screen.class);
        startActivity(intent);
    }

    public void howToSearch(View view)
    {
        Intent intent = new Intent(this, how_to_search_screen.class);
        startActivity(intent);
    }
}
