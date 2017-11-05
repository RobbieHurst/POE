package com.example.robert.questiontwo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * The type Review activity.
 */
public class review_activity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    /**
     * The Database.
     */
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    /**
     * The My ref.
     */
    DatabaseReference myRef;

    private String message;
    private Double lat;
    private Double lon;
    private Float rating;
    private String Id;
    private boolean edit= true;

    /**
     * The Maps intent.
     */
    Intent mapsIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_activity);

        Intent intent = getIntent();

        EditText editMessage = (EditText) findViewById(R.id.tv_message);
        RatingBar editRating = (RatingBar) findViewById(R.id.ratingBar);
        Button deleteButton = (Button) findViewById(R.id.deleteButton);

        lat = intent.getDoubleExtra("LATITUDE", 0.00);
        lon = intent.getDoubleExtra("LONGITUDE", 0.00);
        Id = intent.getStringExtra("Id");
        message = intent.getStringExtra("MESSAGE");

        if(intent.getStringExtra("RATING") == null){
            rating = 0f;
            deleteButton.setVisibility(View.GONE);
        }else{
            rating = Float.parseFloat(intent.getStringExtra("RATING"));
        }

        mAuth = FirebaseAuth.getInstance();
        myRef = database.getReference("Locations/"+mAuth.getCurrentUser().getUid());

        if(Id == null) {
            edit = false;
            Id = myRef.push().getKey();

        }
        else{

                editMessage.setText(message);
                editRating.setRating(rating);

            }

    }

    /**
     * Save.
     *
     * @param view the view
     */
    public void save(View view){

        EditText editMessage = (EditText) findViewById(R.id.tv_message);
        RatingBar editRating = (RatingBar) findViewById(R.id.ratingBar);

            message = editMessage.getText().toString();
            rating = editRating.getRating();

        Review review = new Review(Id,message,lat,lon, Float.parseFloat(rating+""));


        if(!edit){

            myRef.child(Id).setValue(review);

        }
        else{

            myRef.child(Id).setValue(review);

        }

        mapsIntent = new Intent(this, MapsActivity.class);
        startActivity(mapsIntent);

    }

    /**
     * Delete.
     *
     * @param view the view
     */
    public void delete(View view){

        myRef.child(Id).setValue(null);

        mapsIntent = new Intent(this, MapsActivity.class);
        startActivity(mapsIntent);

    }

    public void onBackPressed(){

        mapsIntent = new Intent(this, MapsActivity.class);
        startActivity(mapsIntent);

    }

}
