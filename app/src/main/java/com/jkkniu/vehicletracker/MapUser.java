package com.jkkniu.vehicletracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MapUser extends AppCompatActivity implements OnMapReadyCallback, DataRefs {

    private GoogleMap mMap;
    private MarkerOptions markerOptions;
    Marker marker = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_user);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map2);
        mapFragment.getMapAsync(this);
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

        // Add a marker in Bangladesh and move the camera


        FirebaseDatabase.getInstance().getReference().child(LOCATION_REF).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                LocationSaver saver = snapshot.getValue(LocationSaver.class);
                if (marker != null) {
                    marker.remove();
                }
                if (saver != null) {
                    LatLng latLng = new LatLng(saver.getLatitude(), saver.getLongitude());

                    markerOptions = new MarkerOptions().position(latLng).title("Gari Ekhane!!!");
                    marker = mMap.addMarker(markerOptions);
                    mMap.addPolyline(new PolylineOptions().add(latLng).color(Color.BLUE).width(7));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17.5f));
                } else {
                    Toast.makeText(MapUser.this, "Location pai nai", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}
