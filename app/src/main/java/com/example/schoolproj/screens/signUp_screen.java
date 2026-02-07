package com.example.schoolproj.screens;

import static com.example.schoolproj.FireBaseFiles.FBRef.refAuth;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.schoolproj.MasterActivity;
import com.example.schoolproj.R;
import com.example.schoolproj.classes.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

public class signUp_screen extends MasterActivity
{
    EditText nameED, emailED, passwordED;
    String userName, email, password;
    CheckBox rememberMeCB, termsOfSrviceCB;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_screen);
        rememberMeCB = findViewById(R.id.rememberMeChecked);
        termsOfSrviceCB = findViewById(R.id.termsOfSrviceChecked);
        nameED = findViewById(R.id.nameED);
        emailED = findViewById(R.id.emailED);
        passwordED = findViewById(R.id.passwordED);
    }

    public void connect()
    {
        ProgressDialog progressDialog = new ProgressDialog(signUp_screen.this);
        progressDialog.setTitle("creating user");
        progressDialog.setMessage("please wait...");
        progressDialog.show();
        refAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                progressDialog.dismiss();
                if (task.isSuccessful())
                {
                    Log.i("SIGNUP_SUCCESS", "createUserWithEmailAndPassword:success");
                    FirebaseUser user = refAuth.getCurrentUser();
                    connectedUser = new User(user.getUid(), userName);
                    Toast.makeText(signUp_screen.this, "User created successfully.", Toast.LENGTH_SHORT).show();

                    if (rememberMeCB.isChecked())
                    {
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putBoolean("rememberMe", rememberMeCB.isChecked());
                        editor.putString("userID", connectedUser.getUserID());
                        editor.putString("username", connectedUser.getUsername());
                        editor.putLong("lastLogin", connectedUser.getLastLogin());
                        editor.putLong("creationDate", connectedUser.getCreationDate());
                        editor.apply();
                    }

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("state", Codes.REMEMBER_ME.ordinal());
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                }
                else
                {
                    Log.w("SIGNUP_FAILURE", "signIn:failure", task.getException());
                    Exception exp = task.getException();
                    if (exp instanceof FirebaseAuthInvalidUserException)
                    {
                        Toast.makeText(signUp_screen.this, "Invalid email address.", Toast.LENGTH_SHORT).show();
                    }
                    else if (exp instanceof FirebaseAuthWeakPasswordException)
                    {
                        Toast.makeText(signUp_screen.this, "Password is too weak", Toast.LENGTH_SHORT).show();
                    }
                    else if (exp instanceof FirebaseAuthUserCollisionException)
                    {
                        Toast.makeText(signUp_screen.this, "User already exists.", Toast.LENGTH_SHORT).show();
                    }
                    else if (exp instanceof FirebaseAuthInvalidCredentialsException)
                    {
                        Toast.makeText(signUp_screen.this, "General authentication failure.", Toast.LENGTH_SHORT).show();
                    }
                    else if (exp instanceof FirebaseNetworkException)
                    {
                        Toast.makeText(signUp_screen.this, "Network error. Please check your connection.", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(signUp_screen.this, "An error occurred. Please try again later.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    public void signUp(View view)
    {
        if (termsOfSrviceCB.isChecked())
        {
            userName = nameED.getText().toString();
            email = emailED.getText().toString();
            password = passwordED.getText().toString();
            if (userName.isEmpty() || email.isEmpty() || password.isEmpty())
            {
                Toast.makeText(this, "please fill all the fields", Toast.LENGTH_SHORT).show();
                return;
            }
            connect();
        }

    }


    public void logIn(View view)
    {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("state", Codes.LOG_IN.ordinal());
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
}
