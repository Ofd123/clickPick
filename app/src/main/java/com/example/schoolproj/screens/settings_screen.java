package com.example.schoolproj.screens;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.schoolproj.MasterActivity;
import com.example.schoolproj.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Activity for managing user settings and profile overview.
 * Displays the user's display name and account creation date.
 * Provides functionality to sign out of the application.
 */
public class settings_screen extends MasterActivity
{
    /** TextView to display the user's username. */
    TextView tvSettingsDisplayName;
    /** TextView to display the account creation timestamp. */
    TextView tvSettingsWhenCreated;

    /**
     * Called when the activity is starting.
     * Initializes UI components and populates user profile data from the session.
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle contains the data it most
     *                           recently supplied in onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed()
            {
                finish();
            }
        });

        tvSettingsDisplayName = findViewById(R.id.tvSettingsDisplayName);
        tvSettingsWhenCreated = findViewById(R.id.tvSettingsWhenCreated);

        tvSettingsDisplayName.setText(connected_user.getUsername());
        
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
        String dateString = sdf.format(new Date(connected_user.getCreationDate()));
        tvSettingsWhenCreated.setText("Member since: " + dateString);


    }

    /**
     * UI callback for the back button to close the activity.
     * @param view The view that was clicked.
     */
    public void back(View view)
    {
        finish();
    }

    /**
     * Initiates the sign-out process.
     * Displays a confirmation dialog before clearing the session and closing the activity.
     * @param view The view that was clicked (Sign Out button).
     */
    public void signOut(View view)
    {
        AlertDialog.Builder secondChance = new AlertDialog.Builder(this);
        secondChance.setTitle("are you sure you want to sign out?");
        secondChance.setMessage("you would have to sign in again if you choose to sign out");
//        secondChance.setIcon("")
        secondChance.setPositiveButton("yes", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                if (signout())
                {
                    finish();
                }
                else
                {
                    Toast.makeText(settings_screen.this, "something went wrong", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
        });
        secondChance.setNegativeButton("no", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });
        secondChance.show();
    }
}