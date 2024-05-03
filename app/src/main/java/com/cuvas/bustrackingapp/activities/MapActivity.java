package com.cuvas.bustrackingapp.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.cuvas.bustrackingapp.DirectionsManager;
import com.cuvas.bustrackingapp.R;
import com.cuvas.bustrackingapp.model.LocationModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private LocationManager locationManager;

    String id;

    FirebaseFirestore db;
    ArrayList<LocationModel> locations;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_map);

        db = FirebaseFirestore.getInstance();

        id = getIntent().getStringExtra("id");


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_CODE);
        } else {
            startLocationUpdates();
        }

        db.collection("Points").document(id).collection("Location").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                locations = new ArrayList<>();
                for (DocumentSnapshot document: value.getDocuments()){
                     LocationModel locationModel = document.toObject(LocationModel.class);
                    locations.add(locationModel);
                }
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

    }

    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    0, 0, this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            } else {
                // Handle permission denied
            }
        }
    }


    @Override
    public void onLocationChanged(@NonNull Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        if (mMap != null) {
            LatLng userLatLng = new LatLng(latitude, longitude);
            LatLng driverLatLng = new LatLng(Double.parseDouble(locations.get(0).getLatitude()), Double.parseDouble(locations.get(0).getLongitude()));

            // Add markers for user and driver
            mMap.clear(); // Clear existing markers
            mMap.addMarker(new MarkerOptions().position(userLatLng).title("Your Location")); // Set custom marker icon
            mMap.addMarker(new MarkerOptions().position(driverLatLng).title("Driver Location").icon(BitmapDescriptorFactory.fromResource(R.drawable.bus)));

            // Create a bounds that encompasses both markers
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(userLatLng);
            builder.include(driverLatLng);
            LatLngBounds bounds = builder.build();

            // Calculate padding to ensure markers are not placed at the edge of the map
            int padding = 100; // in pixels

            // Move and animate the camera to show the entire bounds
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));

            DirectionsManager directionsManager = new DirectionsManager(mMap);
            directionsManager.drawPolylineBetweenPoints(userLatLng, driverLatLng);
        }

    }
}