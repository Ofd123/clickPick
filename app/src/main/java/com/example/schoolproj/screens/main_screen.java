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

/**
 * The main dashboard Activity of the application.
 * Provides entry points for regular search, image search, history, favorites, and settings.
 * Handles image capture/selection and initiates AI-powered image analysis via Gemini.
 */
public class main_screen extends MasterActivity
{
    /** Manager for interacting with the Gemini AI model. */
    GeminiManager geminiManager;
    /** JSON object to store extracted search details from an image. */
    JSONObject searchDetails;
    /** Pre-initialized intents for the authentication flow. */
    Intent signinIntent, loginIntent;
    /** Flag to prevent redundant data loading during auth redirects. */
    private boolean isAuthRedirecting = false;

    /**
     * Called when the activity is starting.
     * Initializes the Gemini manager and authentication intents.
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in onSaveInstanceState(Bundle).
     */
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

    /**
     * Called when the activity will start interacting with the user.
     * Triggers data loading and authentication checks.
     */
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

    /**
     * Loads user data and redirects to sign-in if no user is authenticated.
     */
    void loadData()
    {
        if (!loadUserData())
        {
            startActivityForResult(signinIntent, Codes.SIGN_IN.ordinal());
        }
    }

    /**
     * UI callback to start a regular text-based search.
     * @param view The view that was clicked.
     */
    public void regularSearch(View view)
    {
        Intent intent = new Intent(this, search_screen.class);
        startActivityForResult(intent, Codes.SEARCH_REQUEST_CODE.ordinal());
    }

    /**
     * Displays an alert warning the user about image quality for searches.
     */
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

    /**
     * UI callback to start an image-based search.
     * @param view The view that was clicked.
     */
    public void imageSearch(View view)
    {
        showAlert();
    }

    /**
     * Opens a dialog for the user to select between Camera or Gallery.
     */
    private void selectImageSource()
    {
        try
        {
            String[] options = {"Camera", "Gallery"};

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select Image Source");
            builder.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
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
                }
            });

            builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    Toast.makeText(main_screen.this, "Image selection cancelled.", Toast.LENGTH_SHORT).show();
                }
            });
            builder.create().show();
        }
        catch (Exception e)
        {
            Log.e(TAG, "selectImageSource error: " + e.getMessage());
        }
    }

    /**
     * Requests camera permission if needed and opens the camera application.
     */
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

    /**
     * Opens the device gallery to pick an image.
     */
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

    /**
     * Processes results from various activities (Auth, Camera, Gallery).
     * @param requestCode The integer request code originally supplied to startActivityForResult().
     * @param resultCode The integer result code returned by the child activity through its setResult().
     * @param data An Intent, which can return result data to the caller.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK)
        {
            // Handle Login/Signup flow
            if (requestCode == Codes.SIGN_IN.ordinal() || requestCode == Codes.LOG_IN.ordinal())
            {
                int state;
                if (data != null) {
                    state = data.getIntExtra("state", Codes.ERROR.ordinal());
                } else {
                    state = Codes.ERROR.ordinal();
                }

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
                    String welcomeMsg = "Welcome ";
                    if (connected_user != null) {
                        String username = connected_user.getUsername();
                        if (username != null) {
                            welcomeMsg += username;
                        }
                    }
                    Toast.makeText(this, welcomeMsg, Toast.LENGTH_SHORT).show();
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

    /**
     * Sends the captured/selected bitmap to Gemini for product extraction.
     * Transitions to the search screen upon successful analysis.
     * @param imageBitmap The bitmap to analyze.
     */
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

    /**
     * Handles the results of permission requests (e.g., Camera).
     * @param requestCode The request code passed in requestPermissions().
     * @param permissions The requested permissions.
     * @param grantResults The grant results for the corresponding permissions.
     */
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

    /**
     * UI callback to view search history.
     * @param view The view that was clicked.
     */
    public void searchHistory(View view)
    {
        Intent intent = new Intent(this, search_history_screen.class);
        startActivity(intent);
    }

    /**
     * UI callback to view favorite items.
     * @param view The view that was clicked.
     */
    public void favorites(View view)
    {
        Intent intent = new Intent(this,saved_items_screen.class);
        startActivity(intent);
    }

    /**
     * UI callback to view all history records.
     * @param view The view that was clicked.
     */
    public void allHistory(View view)
    {
        Intent intent = new Intent(this,show_all_history_screen.class);
        startActivity(intent);
    }

    /**
     * UI callback to navigate to the settings screen.
     * @param view The view that was clicked.
     */
    public void goToSettings(View view)
    {
        Intent intent = new Intent(this, settings_screen.class);
        startActivity(intent);
    }

    /**
     * UI callback to view the credits screen.
     * @param view The view that was clicked.
     */
    public void credits(View view)
    {
        Intent intent = new Intent(this, credits_screen.class);
        startActivity(intent);
    }

    /**
     * UI callback to view the "How to Search" screen.
     * @param view The view that was clicked.
     */
    public void howToSearch(View view)
    {
        Intent intent = new Intent(this, how_to_search_screen.class);
        startActivity(intent);
    }
}
