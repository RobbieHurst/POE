package com.example.robert.questiontwo;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * The type Maps activity.
 */
public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, LocationListener, GoogleMap.OnInfoWindowClickListener {

    /**
     * The constant MY_PERMISSIONS_REQUEST_LOCATION.
     */
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    /**
     * The M google map.
     */
    GoogleMap mGoogleMap;
    /**
     * The Map frag.
     */
    SupportMapFragment mapFrag;
    /**
     * The M location request.
     */
    LocationRequest mLocationRequest;
    /**
     * The M google api client.
     */
    GoogleApiClient mGoogleApiClient;
    /**
     * The M last location.
     */
    Location mLastLocation;
    /**
     * The M curr location marker.
     */
    Marker mCurrLocationMarker;
    /**
     * The Geocoder.
     */
    Geocoder geocoder;
    /**
     * The Addresses.
     */
    List<Address> addresses;
    /**
     * The Reviews.
     */
    ArrayList<Review> reviews;
    /**
     * The Review intent.
     */
    Intent reviewIntent;
    /**
     * The Database.
     */
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    /**
     * The My ref.
     */
    DatabaseReference myRef;

    /**
     * The Login intent.
     */
    Intent loginIntent;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(this);

        mAuth = FirebaseAuth.getInstance();
        myRef = database.getReference("Locations/"+mAuth.getCurrentUser().getUid());

    }

    @Override
    public void onPause() {
        super.onPause();

        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

            mGoogleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json));


        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                buildGoogleApiClient();
                mGoogleMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        } else {
            buildGoogleApiClient();
            mGoogleMap.setMyLocationEnabled(true);
        }

        mGoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                View v = getLayoutInflater().inflate(R.layout.custom_window, null);

                TextView tvTitle = (TextView) v.findViewById(R.id.tv_title);

                RatingBar ratingBar = (RatingBar) v.findViewById(R.id.RatingBar);

                tvTitle.setText("Message: "+ marker.getSnippet());

                ratingBar.setRating(Float.parseFloat(marker.getTitle().toString()));

                return v;
            }
        });

        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                return false;
            }
        });

        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {
                // TODO Auto-generated method stub

                newReviewIntent(point);

            }
        });

        mGoogleMap.setOnInfoWindowClickListener(this);

        getPoints();

    }

    /**
     * Build google api client.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onLocationChanged(Location location) {

        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        double lat = location.getLatitude();
        double lon = location.getLongitude();

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());



    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MapsActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mGoogleMap.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    /**
     * Get points.
     */
    public void getPoints(){

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Locations/"+mAuth.getCurrentUser().getUid());
        ref.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        reviews  = new ArrayList<>();

                        for (DataSnapshot dsp: dataSnapshot.getChildren()) {

                                Double latitude = (Double) dsp.child("latitude").getValue();
                                Double longitude = (Double) dsp.child("longitude").getValue();
                                String message = (String) dsp.child("message").getValue();
                                Float rating = Float.parseFloat(dsp.child("rating").getValue().toString());
                                String Id = dsp.getKey();

                                Review review = new Review(Id,message,latitude,longitude,rating);

                                reviews.add(review);

                        }
                        addPoints();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });

    }

    /**
     * Add points.
     */
    public void addPoints(){

        LatLng lastPoint;

        for(Review review : reviews){

            Double lat = review.getLatitude();
            Double lon = review.getLongitude();

            LatLng latLng = new LatLng(lat,lon);

           Marker marker = mGoogleMap.addMarker(new MarkerOptions().position(latLng).title(review.getRating()+"").snippet(review.getMessage()));
            marker.setTag(review.getID());


        }

        try{

            lastPoint = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());

            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastPoint, 11));

        }
        catch (Exception e){
            Toast.makeText(MapsActivity.this, R.string.auth_failed,
                    Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        getReviewIntent(marker);
    }

    /**
     * Get review intent.
     *
     * @param marker the marker
     */
    public void getReviewIntent(Marker marker){

        LatLng latLng = marker.getPosition();

        reviewIntent = new Intent(this, review_activity.class);
        reviewIntent.putExtra("LATITUDE",latLng.latitude);
        reviewIntent.putExtra("LONGITUDE",latLng.longitude);
        reviewIntent.putExtra("MESSAGE",marker.getSnippet());
        reviewIntent.putExtra("RATING",marker.getTitle());
        reviewIntent.putExtra("Id", marker.getTag().toString());
        startActivity(reviewIntent);
    }

    /**
     * New review intent.
     *
     * @param latLng the lat lng
     */
    public void newReviewIntent(LatLng latLng){

        reviewIntent = new Intent(this, review_activity.class);

        reviewIntent.putExtra("LATITUDE",latLng.latitude);
        reviewIntent.putExtra("LONGITUDE",latLng.longitude);
        startActivity(reviewIntent);

    }

    /**
     * Add current location.
     *
     * @param view the view
     */
    public void addCurrentLocation(View view){

        LatLng latLng = new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude());
        newReviewIntent(latLng);

    }

    public void onBackPressed(){

        loginIntent = new Intent(this, MainActivity.class);
        startActivity(loginIntent);

    }
}