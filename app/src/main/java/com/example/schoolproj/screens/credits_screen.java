package com.example.schoolproj.screens;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.schoolproj.MasterActivity;
import com.example.schoolproj.R;

/**
 * Activity that displays the credits and attributions for the application.
 * Extends MasterActivity to maintain consistent UI and state management.
 */
public class credits_screen extends MasterActivity {

    /**
     * Called when the activity is starting.
     * Sets the layout and handles the back button dispatcher to close the activity.
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits_screen);
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed()
            {
                finish();
            }
        });
    }

    /**
     * UI callback for the back button to close the activity.
     * @param view The view that was clicked.
     */
    public void back(View view)
    {
        finish();
    }
}