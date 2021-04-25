package com.jkkniu.vehicletracker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.List;
import java.util.Random;

public class MapDriver extends AppCompatActivity implements OnMapReadyCallback, DataRefs {

    private GoogleMap mMap;

    SupportMapFragment supportMapFragment;
   // FusedLocationProviderClient fusedLocationProviderClient;
    Marker marker = null;

/*    private LocationRequest locationRequest = new LocationRequest()
            .setInterval(2000)
            .setFastestInterval(1000)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);*/

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_driver);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);

        // get GPS permission
/*
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest.create());
        builder.setAlwaysShow(true);


        LocationServices.getSettingsClient(getApplicationContext())
                .checkLocationSettings(builder.build())
                .addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                        try {
                            LocationSettingsResponse response = task.getResult(ApiException.class);
                        } catch (ApiException e) {
                            switch (e.getStatusCode()) {
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    try {
                                        ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                        resolvableApiException.startResolutionForResult(MapDriver.this, 1001);
                                    } catch (IntentSender.SendIntentException ex) {
                                        ex.printStackTrace();

                                    }
                                    break;
                            }
                        }
                    }
                });
*/


        //for runtime permission
        String[] permission = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
        };

        Dexter.withContext(getApplicationContext())
                .withPermissions(permission)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {

                        if (multiplePermissionsReport.areAllPermissionsGranted()) {

                            //todo change "bus_1"
                            Intent startIntent = new Intent(MapDriver.this, ForegroundNotificationService.class);
                            startIntent.setAction("START_TRACKING");
                            startIntent.putExtra("bus", "bus_1");
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                startForegroundService(startIntent);
                            } else {
                                startService(startIntent);
                            }

                            Toast.makeText(MapDriver.this, "Permission Granted!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MapDriver.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {

                    }
                }).check();

//todo change "bus_1"
        FirebaseDatabase.getInstance().getReference().child(DataRefs.LOCATION_REF).child("bus_1")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        LocationSaver locationSaver = snapshot.getValue(LocationSaver.class);
                        if (mMap != null && locationSaver != null) {
                            addMarkerToMap(locationSaver);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Log.d("OnMapReady", "Permission paisi");
        // getCurrentLocation();
        Log.d("OnMapReady", "Location paisi");
/*
        LatLng latLng = new LatLng(23.0, 90.0);
        addMarkerToMap(latLng);*/

    }

 /*   private void addMarkerToMap(LatLng latLng) {

        if (marker != null) {
            marker.remove();
        }
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title("ami ekhane").position(latLng);
        marker = mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f));
    }*/

    private void addMarkerToMap(LocationSaver locationSaver) {

        if (marker != null) {
            marker.remove();
        }
        LatLng latLng = new LatLng(locationSaver.getLatitude(), locationSaver.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title("ami ekhane").position(latLng);
        marker = mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f));
    }


/*    private LocationCallback locationCallback = new LocationCallback() {

        @Override
        public void onLocationAvailability(LocationAvailability locationAvailability) {
            super.onLocationAvailability(locationAvailability);
        }

        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if (locationResult != null) {
                Location location = locationResult.getLastLocation();
                Log.e("LOCATION paisi>>", location.toString());


                //start notification service
                Intent serviceIntent = new Intent(MapDriver.this, ForegroundNotificationService.class);
                serviceIntent.putExtra("input", location.toString());
                ContextCompat.startForegroundService(MapDriver.this, serviceIntent);
                //


                LatLng l = new LatLng(location.getLatitude(), location.getLongitude());
                addMarkerToMap(l);
            }


        }
    };


    public void getCurrentLocation() {

        fusedLocationProviderClient = new FusedLocationProviderClient(MapDriver.this);
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    public void getLocation(Context context) {
        final long time = 4900;
        fusedLocationProviderClient = new FusedLocationProviderClient(context);
        fusedLocationProviderClient
                .requestLocationUpdates(
                        new LocationRequest()
                                .setFastestInterval(time)
                                .setInterval(time)
                                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY),
                        new LocationCallback() {
                            @Override
                            public void onLocationResult(LocationResult locationResult) {
                                if (locationResult != null) {
                                    Location location = locationResult.getLastLocation();
                                    Log.e("LOCATION Updtx", location.toString());
                                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                                    saveLocationToServer(location);

                                } else {
                                    Log.e("LOCATION Updtx", "no updates");
                                }

                            }

                            @Override
                            public void onLocationAvailability(LocationAvailability locationAvailability) {
                                super.onLocationAvailability(locationAvailability);
                            }
                        }, Looper.getMainLooper()
                );
    }


    public void stopLocationUpdates() {
        if (fusedLocationProviderClient != null) {
            try {
                final Task<Void> voidTask = LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(locationCallback);
                if (voidTask.isSuccessful()) {
                    Log.d("stopLocationUpdates", "StopLocation updates successful! ");
                } else {
                    Log.d("stopLocationUpdates", "StopLocation updates unsuccessful! " + voidTask.toString());
                }
            } catch (SecurityException exp) {
                Log.d("stopLocationUpdates", " Security exception while removeLocationUpdates");
            }
        }
    }*/

    public void logout(View view) {
/*        stopLocationUpdates();
        FirebaseAuth.getInstance().signOut(); //logout

        //stop foreground notification service
        Intent serviceIntent = new Intent(MapDriver.this, ForegroundNotificationService.class);
        stopService(serviceIntent);
        //*/

        Intent startIntent = new Intent(MapDriver.this, ForegroundNotificationService.class);
        startIntent.setAction("STOP_TRACKING");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(startIntent);
        } else {
            startService(startIntent);
        }

        startActivity(new Intent(getApplicationContext(), Login.class));
        finish();
    }

/*    public void saveLocationToServer(Location location) {
        FirebaseDatabase.getInstance()
                .getReference()
                .child(LOCATION_REF)
                .setValue(
                        new LocationSaver()
                                .setLatitude(location.getLatitude())
                                .setLongitude(location.getLongitude())
                )
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Firebase", "database e data paisi");
                        Toast.makeText(
                                MapDriver.this,
                                "database e data paisi",
                                Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        Toast.makeText(MapDriver.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }*/
}
