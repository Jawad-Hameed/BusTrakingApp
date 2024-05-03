package com.cuvas.bustrackingapp;

import android.graphics.Color;
import android.os.AsyncTask;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;

import java.util.List;

public class DirectionsManager {

    private GoogleMap mMap;
    private Polyline currentPolyline;

    public DirectionsManager(GoogleMap map) {
        mMap = map;
    }

    public void drawPolylineBetweenPoints(LatLng origin, LatLng destination) {
        new FetchDirectionsTask().execute(origin, destination);
    }

    private class FetchDirectionsTask extends AsyncTask<LatLng, Void, DirectionsResult> {

        @Override
        protected DirectionsResult doInBackground(LatLng... params) {
            LatLng origin = params[0];
            LatLng destination = params[1];

            GeoApiContext context = new GeoApiContext.Builder()
                    .apiKey("AIzaSyAQfzZim28gq7PWy1KT9TD3PA-yJXGQysU")
                    .build();

            try {
                return DirectionsApi.newRequest(context)
                        .mode(TravelMode.DRIVING)
                        .origin(new com.google.maps.model.LatLng(origin.latitude, origin.longitude))
                        .destination(new com.google.maps.model.LatLng(destination.latitude, destination.longitude))
                        .await();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(DirectionsResult result) {
            if (result != null && result.routes != null && result.routes.length > 0) {
                List<com.google.maps.model.LatLng> decodedPath = result.routes[0].overviewPolyline.decodePath();

                if (currentPolyline != null) {
                    currentPolyline.remove();
                }

                PolylineOptions polylineOptions = new PolylineOptions();
                for (com.google.maps.model.LatLng latLng : decodedPath) {
                    polylineOptions.add(new LatLng(latLng.lat, latLng.lng));
                }
                polylineOptions.width(5).color(Color.RED);

                currentPolyline = mMap.addPolyline(polylineOptions);
            }
        }
    }
}

