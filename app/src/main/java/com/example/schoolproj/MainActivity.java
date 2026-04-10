package com.example.schoolproj;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.schoolproj.classes.User;
import com.example.schoolproj.screens.login_screen;
import com.example.schoolproj.screens.main_screen;
import com.example.schoolproj.screens.signUp_screen;

/**
 * The initial entry point of the application.
 * Extends MasterActivity to inherit session management and UI consistency.
 */
public class MainActivity extends MasterActivity
{
    /**
     * Called when the activity is starting.
     * Sets the content view for the main entry layout.
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
}