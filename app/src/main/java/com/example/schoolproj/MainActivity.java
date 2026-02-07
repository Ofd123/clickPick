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

public class MainActivity extends MasterActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
}