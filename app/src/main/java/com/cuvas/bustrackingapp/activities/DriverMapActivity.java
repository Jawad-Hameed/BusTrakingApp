package com.cuvas.bustrackingapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.cuvas.bustrackingapp.R;
import com.cuvas.bustrackingapp.model.LocationModel;
import com.cuvas.bustrackingapp.model.PointModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;

public class DriverMapActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    private static final int PERMISSION_REQUEST_CODE = 100;
    private LocationManager locationManager;
    private double latitude, longitude;

    FirebaseFirestore db;
    Dialog dialog;
    String routeID;
    String id;
    FloatingActionButton startLocation;
    Boolean isSharing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_driver_map);

        startLocation = findViewById(R.id.startLocation);

        db = FirebaseFirestore.getInstance();

         routeID = db.collection("Points").document().getId();
        id = db.collection("Points").document().collection("Location").document().getId();

         createDialog();

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        startLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSharing){
                    stopLocationUpdates();
                    isSharing = false;
                    startLocation.setImageDrawable(getDrawable(R.drawable.play));

                }else {
                    if (ContextCompat.checkSelfPermission(DriverMapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(DriverMapActivity.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                PERMISSION_REQUEST_CODE);
                    } else {
                        startLocationUpdates();
                    }
                    isSharing = true;
                    startLocation.setImageDrawable(getDrawable(R.drawable.stop));
                }

            }
        });

    }
    public void createDialog(){
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.route_dialog);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        EditText editText = dialog.findViewById(R.id.editText);
        AppCompatButton button = dialog.findViewById(R.id.submitBtn);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String route = editText.getText().toString();
                if (!route.isEmpty()){
                    PointModel pointModel = new PointModel(routeID,route);
                    db.collection("Points").document(routeID).set(pointModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(DriverMapActivity.this, "Route Added", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    });
                }
            }
        });
    }

    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    0, 0, this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
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

    private void stopLocationUpdates() {
        locationManager.removeUpdates(this);
        deletePoint();
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        LocationModel locationModel = new LocationModel(id, String.valueOf(latitude), String.valueOf(longitude));
        db.collection("Points").document(routeID).collection("Location").document(id).set(locationModel);
        // Add marker to the map
        if (mMap != null) {
            LatLng latLng = new LatLng(latitude, longitude);
            mMap.clear(); // Clear existing markers
            mMap.addMarker(new MarkerOptions().position(latLng).title("Driver Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f));
        }
    }



    public void deletePoint(){
        db.collection("Points").document(routeID).collection("Location").document(id).delete();
    }
}
