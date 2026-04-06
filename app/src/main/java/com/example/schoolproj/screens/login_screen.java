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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class login_screen extends MasterActivity
{

    Intent loginIntent;
    String userName, password;
    EditText emailED, passwordED;
    CheckBox rememberMeCB;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        loginIntent = getIntent();

        emailED = findViewById(R.id.emailED);
        passwordED = findViewById(R.id.passwordED);
        rememberMeCB = findViewById(R.id.rememberMeChecked);

    }

    public void logIn(View view)
    {
        String email = emailED.getText().toString().trim();
        String password = passwordED.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty())
        {
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Logging In");
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        refAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        progressDialog.dismiss(); // Hide the loading dialog
                        if (task.isSuccessful())
                        {
                            // Firebase login was successful
                            Log.d("LOGIN_SUCCESS", "logInWithEmail:success");
                            FirebaseUser fbUser = refAuth.getCurrentUser();

                            if (rememberMeCB.isChecked())
                            {
                                SharedPreferences.Editor editor = settings.edit();
                                editor.putBoolean("stayConnected", true);
                                editor.putString("userID", fbUser.getUid());

                                String username = fbUser.getEmail() != null ? fbUser.getEmail().split("@")[0] : "User";
                                editor.putString("username", username);
                                editor.putLong("lastLogin", System.currentTimeMillis());
                                if (fbUser.getMetadata() != null) {
                                    editor.putLong("creationDate", fbUser.getMetadata().getCreationTimestamp());
                                }
                                editor.apply();
                            }

                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("state", Codes.REMEMBER_ME.ordinal());
                            setResult(Activity.RESULT_OK, resultIntent);
                            finish();
                        }
                        else
                        {
                            // Firebase login failed, show a specific error message
                            Log.w("LOGIN_FAILURE", "logInWithEmail:failure", task.getException());
                            String errorMessage;
                            Exception exp = task.getException();
                            if (exp instanceof FirebaseAuthInvalidUserException)
                            {
                                errorMessage = "No account found with this email.";
                            }
                            else if (exp instanceof FirebaseAuthInvalidCredentialsException)
                            {
                                errorMessage = "Incorrect password. Please try again.";
                            }
                            else if (exp instanceof FirebaseNetworkException)
                            {
                                errorMessage = "Cannot connect to the network. Please check your connection.";
                            }
                            else
                            {
                                errorMessage = "Authentication failed. Please try again later.";
                            }
                            Toast.makeText(login_screen.this, errorMessage, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
    public void switchToSignUp(View view)
    {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("state", Codes.SIGN_IN.ordinal());
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
}
