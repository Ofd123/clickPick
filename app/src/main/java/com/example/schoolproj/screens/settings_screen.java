package com.example.schoolproj.screens;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
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

public class settings_screen extends MasterActivity
{
    TextView tvSettingsDisplayName;
    TextView tvSettingsWhenCreated;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        tvSettingsDisplayName = findViewById(R.id.tvSettingsDisplayName);
        tvSettingsWhenCreated = findViewById(R.id.tvSettingsWhenCreated);

        tvSettingsDisplayName.setText(connected_user.getUsername());
        
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
        String dateString = sdf.format(new Date(connected_user.getCreationDate()));
        tvSettingsWhenCreated.setText("Member since: " + dateString);


    }

    public void back(View view)
    {
        finish();
    }



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