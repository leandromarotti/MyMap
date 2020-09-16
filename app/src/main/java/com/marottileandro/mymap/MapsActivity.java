package com.marottileandro.mymap;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import androidx.fragment.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // retrieve city name from original intent
        Intent originalIntent = getIntent();
        city = originalIntent.getStringExtra(Cities.CITY_KEY);
        if (city == null)
            city = Cities.DEFAULT_CITY;

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
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

        // default latitude and longitude for city before geocoding city
        double latitude = Cities.DEFAULT_LATITUDE;
        double longitude = Cities.DEFAULT_LONGITUDE;

        // retrieve attraction for city
        Cities cities = MainActivity.cities;
        String attraction = cities.getAttraction(city);

        // retrieve latitude and longitude of city/attraction
        Geocoder coder = new Geocoder(this);
        try {
            //geocode city
            String address = attraction + ", " + city;
            List<Address> addresses = coder.getFromLocationName(address, 6);
            if (addresses != null) {
                latitude = addresses.get(0).getLatitude();
                longitude = addresses.get(0).getLongitude();
            } else // geocoding failed; use default values
                city = Cities.DEFAULT_CITY;
        } catch (IOException ioe) {
            // geocoding failed; use default city, latitude and longitude
            city = Cities.DEFAULT_CITY;
        }

        // update the map
        LatLng cityLocation = new LatLng(latitude, longitude);

        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(cityLocation, 15.5f);
        mMap.moveCamera(update);

        MarkerOptions options = new MarkerOptions();
        options.position(cityLocation);
        options.title(attraction);
        options.snippet(Cities.MESSAGE);
        mMap.addMarker(options);

        CircleOptions circleOptions = new CircleOptions().center(cityLocation).radius(500)
                .strokeWidth(10.0f).strokeColor(0xFFFF0000);
        mMap.addCircle(circleOptions);
    }
}