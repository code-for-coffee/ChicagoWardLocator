package org.chiwardlocator.codeforcoffee.chicagowardlocator;

import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.location.Location;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.jar.Manifest;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private GoogleMap mMap;                     // google map object
    private Location location;                  // location object
    private LocationRequest mLocationRequest;   // locationrequest object
    private GoogleApiClient mGoogleApiClient;   // googleapi client object
    protected Double latitude, longitude;       // doubles for lat/long
    final Double CHICAGO_LAT = 41.8500300;
    final Double CHICAGO_LONG = -87.6500500;

    // sdk 23+ permissions (support for Android 6.0+ request
    int coarsePermissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION);
    int finePermissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);

    protected static final int REQUEST_CHECK_SETTINGS = 100;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    public static final String TAG = MapsActivity.class.getSimpleName();

    // on Connected to Location Services
    @Override
    public void onConnected(Bundle connectionHint) throws SecurityException {
        Log.i(TAG, "Location Services connected!");
        location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } else {
            handleLocationChange(location);
        }
    }

    // what to do is our location changes
    private void handleLocationChange(Location location) {
        Log.d(TAG, location.toString());
        Double lat = location.getLatitude(),
                lon = location.getLongitude();
        updateLatLong(lat, lon);
        updateMapMarkerAndLocation();
    }

    // when our activity is resumed...
    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect(); // use dat memory
    }

    // when our activity is paused...
    @Override
    protected void onPause(){
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect(); // save dat memory
        }
    }

    // when our Location service is suspended
    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Location Services suspended! You should reconnect.");
    }

    // when our connection fails...
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    protected void startLocationUpdates() throws SecurityException {
        // Define a listener that responds to location updates
        LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    }

    private void updateLatLong(Double lat, Double lon) {
        this.latitude = lat;
        this.longitude = lon;
    }

    @Override
    public void onLocationChanged(Location location) {
        handleLocationChange(location);
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);       // milliseconds
        mLocationRequest.setFastestInterval(5000); // milliseconds
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); // IMPORTANT
    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Create an instance of GoogleAPIClient.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        createLocationRequest();

//        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
//                .addLocationRequest(mLocationRequest);
//
//        PendingResult<LocationSettingsResult> result =
//                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
//                        builder.build());
//
//        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
//            @Override
//            public void onResult(LocationSettingsResult res) {
//                final Status STATUS = res.getStatus();
//                //final LocationSettingsStates LOCATION_SETTING_STATES = res.getLocationSettingsStates();
//                switch (STATUS.getStatusCode()) {
//                    case LocationSettingsStatusCodes.SUCCESS:
//                        // all is well... we can make requests here
//                        startLocationUpdates();
//                        break;
//                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
//                        // Location settings are not satisfied, but this can be fixed
//                        // by showing the user a dialog.
//                        try {
//                            // Show the dialog by calling startResolutionForResult(),
//                            // and check the result in onActivityResult().
//                            STATUS.startResolutionForResult(
//                                    MapsActivity.this,
//                                    REQUEST_CHECK_SETTINGS);
//                        } catch (IntentSender.SendIntentException e) {
//                            // Ignore the error.
//                        }
//                        break;
//                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
//                        // settings unavailable and cannot be changed :(
//                        break;
//                }
//            }
//        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void updateMapMarkerAndLocation() {
        LatLng currentLocation = new LatLng(this.longitude,this.latitude);
        mMap.addMarker(new MarkerOptions().position(currentLocation).title("Your location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
    }

//    private void updateLocation() throws SecurityException {
//
//        Criteria criteria  = new Criteria();
//        criteria.setAccuracy(Criteria.ACCURACY_FINE);
//        criteria.setAltitudeRequired(false);
//        criteria.setBearingRequired(false);
//        criteria.setCostAllowed(true);
//        criteria.setPowerRequirement(Criteria.POWER_LOW);
//
//        String bestProvider = locationManager.getBestProvider(criteria, true);
//        locationManager.requestLocationUpdates(bestProvider, 5000, 0, locationListener);
//    }


    // Manipulates the map once available.
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(CHICAGO_LAT, CHICAGO_LONG)));
    }


}
