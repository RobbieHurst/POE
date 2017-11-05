package com.example.robert.questiontwo;

import android.app.ActionBar;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

/**
 * The type Main activity.
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Firebase: ";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private boolean loggedOn = false;
    /**
     * The Maps intent.
     */
    Intent mapsIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        mAuth = FirebaseAuth.getInstance();

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
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

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
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
        EditText editPassword = (EditText) findViewById(R.id.Password);

        String email = editEmail.getText() + "";
        String password = editPassword.getText() + "";


        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, R.string.auth_failed,
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
    public void signIn(View view) {

        EditText editEmail = (EditText) findViewById(R.id.Email);
        EditText editPassword = (EditText) findViewById(R.id.Password);

        String email = editEmail.getText() + "";
        String password = editPassword.getText() + "";

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(MainActivity.this, R.string.auth_failed,
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Successful Login",
                                    Toast.LENGTH_SHORT).show();
                            loggedOn = true;
                            displayMap();
                        }
                    }
                });
    }

    /**
     * Display map.
     */
    public void displayMap(){

        if(loggedOn){

            mapsIntent = new Intent(this, MapsActivity.class);
            startActivity(mapsIntent);

        }

    }

    /**
     * Help intent.
     *
     * @param view the view
     */
    public void helpIntent(View view){

        Intent helpIntent = new Intent(this, Help.class);
        startActivity(helpIntent);

    }
}
