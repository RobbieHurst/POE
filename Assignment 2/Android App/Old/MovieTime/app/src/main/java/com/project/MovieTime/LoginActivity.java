package com.project.MovieTime;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    //Declaring of Variables that are used for Firebase access and creating a new Intent;

    private static final String TAG = "Firebase: ";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private boolean loggedOn = false;

    Intent nowPlayingIntent;

    //Oncreat method.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();         //Getting the intsance of a Firebase Authorization.

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {     //Checking to see if the user is logged in
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

    }
    //Creating a authorization listener.
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {          //Stopping the listener.
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    /**
     * Create account.
     *
     * @param view the view
     */
    public void createAccount(View view) {

        EditText editEmail = (EditText) findViewById(R.id.Email);
        EditText editPassword = (EditText) findViewById(R.id.Password);     //Getting the values for the Login process

        String email = editEmail.getText() + "";
        String password = editPassword.getText() + "";


        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());        //Creating the user

                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, R.string.auth_failed,        //If was not successful
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    /**
     * Sign in.
     *
     * @param view the view
     */
    public void signIn(View view) {             //Sign in method that will take the users details.

        EditText editEmail = (EditText) findViewById(R.id.Email);
        EditText editPassword = (EditText) findViewById(R.id.Password);

        String email = editEmail.getText() + "";
        String password = editPassword.getText() + "";

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());        //Checking for login details.

                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(LoginActivity.this, R.string.auth_failed,
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(LoginActivity.this, "Successful Login",
                                    Toast.LENGTH_SHORT).show();
                            loggedOn = true;
                            menuIntent();
                        }
                    }
                });
    }

    /**
     * menu intent.
     */
    public void menuIntent(){            //Displaying the Now Playing intent.

        if(loggedOn){

            nowPlayingIntent = new Intent(this, MenuActivity.class);
            startActivity(nowPlayingIntent);
        }
    }

    /**
     * Help intent.
     *
     * @param view the view
     */
    public void helpIntent(View view){

        Intent helpIntent = new Intent(this, HelpActivity.class);
        startActivity(helpIntent);

    }

}
