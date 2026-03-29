package com.example.schoolproj.screens;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.schoolproj.MasterActivity;
import com.example.schoolproj.R;

public class settings_screen extends MasterActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

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