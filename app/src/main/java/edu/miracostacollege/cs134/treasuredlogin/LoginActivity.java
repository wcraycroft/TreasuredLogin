package edu.miracostacollege.cs134.treasuredlogin;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "TreasuredLogin";

    private EditText mEmailEditText;
    private EditText mPasswordEditText;

    //TODO (1): Add Firebase member variables (auth and user)
    FirebaseAuth mAuth;
    FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Views
        mEmailEditText = (EditText) findViewById(R.id.emailEditText);
        mPasswordEditText = (EditText) findViewById(R.id.passwordEditText);


        //TODO (2): Initialize Firebase authentication
        mAuth = FirebaseAuth.getInstance();


        // TODO (3): Get the current user.  If not null (already signed in), go directly to TreasureActivity
        mUser = mAuth.getCurrentUser();
        if (mUser != null)
        {
            // Already signed in, take them to TreasureActivity
            Intent intent = new Intent(this, TreasureActivity.class);
            startActivity(intent);
            finish();
        }
    }

    // DONE (4): Create a private void goToTreasure() method that finishes this activity
    // DONE (4): then creates a new Intent to the TreasureActivity.class and starts the intent.


    // TODO (5): Create a private boolean isValidInput() method that checks to see whether
    // TODO (5): the email address or password is empty.  Return false if either is empty, true otherwise.
    private boolean isValidInput()
    {
        boolean valid = true;
        String email = mEmailEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();

        if (TextUtils.isEmpty(email))
        {
            valid = false;
            mEmailEditText.setError("Email field is empty.");
        }
        else if (TextUtils.isEmpty(password))
        {
            valid = false;
            mPasswordEditText.setError("Password field is empty.");
        }

        return valid;
    }

    // TODO (6): Create a private void createAccount(String email, String password) method
    // TODO (6): that checks for valid input, then uses Firebase authentication to create the user with email and password.
    private void createAccount(String email, String password)
    {
        if (!isValidInput())
            return;
        // Use authentication service to create a user with an email and password
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // task is successful is username is unique (new) on Firebase
                        if (task.isSuccessful())
                        {
                            // Send a verification email
                            mUser = mAuth.getCurrentUser();
                            mUser.sendEmailVerification();
                            Toast.makeText(LoginActivity.this,
                                    "A verification email has been sent to " + mUser.getEmail() + ".",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(LoginActivity.this,
                                    "Error creating the account.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // TODO (7): Create a private void signIn(String email, String password) method
    // TODO (7): that checks for valid input, then uses Firebase authentication to sign in user with email and password entered.
    private void signIn(String email, String password)
    {
        if (!isValidInput())
            return;
        // Use authentication service to create a user with an email and password
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // task is successful is username is unique (new) on Firebase
                        if (task.isSuccessful())
                        {
                            mUser = mAuth.getCurrentUser();
                            if (mUser.isEmailVerified()) {
                                Intent intent = new Intent(LoginActivity.this, TreasureActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else
                            {
                                Toast.makeText(LoginActivity.this,
                                        "Account not verified. Please check for verification email sent to "
                                                + mUser.getEmail() + ", then sign in.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        {
                            Toast.makeText(LoginActivity.this,
                                    "Invalid email or password.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // TODO (8): Create a public void handleLoginButtons(View v) that checks the id of the button clicked.
    // TODO (8): If the button is createAccountButton, call the createAccount() method, else if it's signInButton, call the signIn() method.
    public void handleLoginButtons(View v)
    {
        if (v.getId() == R.id.createAccountButton)
        {
            createAccount(mEmailEditText.getText().toString(), mPasswordEditText.getText().toString());
        }
        else if (v.getId() == R.id.signInButton)
        {
            signIn(mEmailEditText.getText().toString(), mPasswordEditText.getText().toString());
        }
    }
}
