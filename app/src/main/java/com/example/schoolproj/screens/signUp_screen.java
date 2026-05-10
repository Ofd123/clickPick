package com.example.schoolproj.screens;

import static com.example.schoolproj.FireBaseFiles.FBRef.refAuth;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

/**
 * Activity for new user registration (Sign Up).
 * Handles user creation using Firebase Authentication, manages "Remember Me"
 * preferences,
 * and enforces acceptance of the Terms of Service.
 */
public class signUp_screen extends MasterActivity {
    /** Editable fields for user name, email, and password. */
    EditText emailED, passwordED;
    /** Current values of registration fields. */
    String userName, email, password;
    /** Checkboxes for persisting the session and accepting terms. */
    CheckBox rememberMeCB, cbTermsOfService;
    /** Link to open the Terms of Service dialog. */
    TextView tvTermsOfServiceLink;

    /**
     * Called when the activity is starting.
     * Initializes UI components and sets up listeners for the Terms of Service
     * link.
     * 
     * @param savedInstanceState If the activity is being re-initialized after
     *                           previously being shut down then this Bundle
     *                           contains the data it most
     *                           recently supplied in onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_screen);

        rememberMeCB = findViewById(R.id.rememberMeChecked);
        cbTermsOfService = findViewById(R.id.cbTermsOfService);
        tvTermsOfServiceLink = findViewById(R.id.tvTermsOfServiceLink);
//        nameED = findViewById(R.id.nameED);
        emailED = findViewById(R.id.emailED);
        passwordED = findViewById(R.id.passwordED);

        tvTermsOfServiceLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTermsOfServiceDialog();
            }
        });

        cbTermsOfService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {showTermsOfServiceDialog();}
        });
    }

    /**
     * Displays an AlertDialog containing the app's Terms of Service.
     * Provides 'Accept' and 'Decline' options that update the Terms checkbox.
     */
    private void showTermsOfServiceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.terms_of_service_dialog, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        View btnAccept = dialogView.findViewById(R.id.btnAccept);
        View btnDecline = dialogView.findViewById(R.id.btnDecline);

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cbTermsOfService.setChecked(true);
                dialog.dismiss();
            }
        });

        btnDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cbTermsOfService.setChecked(false);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    /**
     * Performs the actual user creation in Firebase Authentication.
     * Displays a progress dialog, manages session state, and persists user info if
     * "Remember Me" is checked.
     */
    public void connect() {
        ProgressDialog progressDialog = new ProgressDialog(signUp_screen.this);
        progressDialog.setTitle("creating user");
        progressDialog.setMessage("please wait...");
        progressDialog.show();
        refAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            Log.i("SIGNUP_SUCCESS", "createUserWithEmailAndPassword:success");
                            FirebaseUser user = refAuth.getCurrentUser();
                            isLoggedInThisSession = true;
                            connected_user = new User(user.getUid(), userName);
                            Toast.makeText(signUp_screen.this, "User created successfully.", Toast.LENGTH_SHORT).show();

                            if (rememberMeCB.isChecked()) {
                                SharedPreferences.Editor editor = settings.edit();
                                editor.putBoolean("stayConnected", true);
                                editor.putString("userID", connected_user.getUserID());
                                editor.putString("username", connected_user.getUsername());
                                editor.putLong("lastLogin", connected_user.getLastLogin());
                                editor.putLong("creationDate", connected_user.getCreationDate());
                                editor.apply();
                            }

                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("state", Codes.REMEMBER_ME.ordinal());
                            setResult(Activity.RESULT_OK, resultIntent);
                            finish();
                        } else {
                            Log.w("SIGNUP_FAILURE", "signIn:failure", task.getException());
                            Exception exp = task.getException();
                            if (exp instanceof FirebaseAuthInvalidUserException) {
                                Toast.makeText(signUp_screen.this, "Invalid email address.", Toast.LENGTH_SHORT).show();
                            } else if (exp instanceof FirebaseAuthWeakPasswordException) {
                                Toast.makeText(signUp_screen.this, "Password is too weak", Toast.LENGTH_SHORT).show();
                            } else if (exp instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(signUp_screen.this, "User already exists.", Toast.LENGTH_SHORT).show();
                            } else if (exp instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(signUp_screen.this, "General authentication failure.",
                                        Toast.LENGTH_SHORT).show();
                            } else if (exp instanceof FirebaseNetworkException) {
                                Toast.makeText(signUp_screen.this, "Network error. Please check your connection.",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(signUp_screen.this, "An error occurred. Please try again later.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

    }

    /**
     * UI callback to start the sign-up process.
     * Validates that all fields are filled and the Terms of Service are accepted.
     * 
     * @param view The view that was clicked (Sign Up button).
     */
    public void signUp(View view) {
//        userName = nameED.getText().toString().trim();
        email = emailED.getText().toString().trim();
        password = passwordED.getText().toString().trim();

        boolean isValid = true;

//        if (userName.isEmpty() || userName.length() < 3) {
//            nameED.setError("Name cannot be empty");
//            isValid = false;
//        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailED.setError("Please enter a valid email address");
            isValid = false;
        }

        if (password.isEmpty() || password.length() < 4) {
            passwordED.setError("Password cannot be empty");
            isValid = false;
        }
        userName = email.split("@")[0]; //get the username from the email


        if (!isValid) {
            return;
        }

        if (!cbTermsOfService.isChecked())
        {
            Toast.makeText(this, "Please read and accept the Terms of Service", Toast.LENGTH_SHORT).show();
            return;
        }

        connect();
    }

    /**
     * Switches the user state to Log In and returns to the calling activity.
     * 
     * @param view The view that was clicked (Log In link/button).
     */
    public void logIn(View view) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("state", Codes.LOG_IN.ordinal());
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
}
