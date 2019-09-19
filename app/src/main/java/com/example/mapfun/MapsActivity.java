package com.example.mapfun;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.internal.IGoogleMapDelegate;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "MapsActivityh";
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    private GoogleMap mMap;
    private LocationListener locationListener;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


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
        getPermission();

/*
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,15f));*/
    }


    private void getPermission() {
        //no need to check if sdk is <23 since minsdk is 23
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat
                    .requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_ASK_PERMISSIONS);
        } else {

           // showLastKnownLocation();
              getLocation();
        }
    }

    private void showLastKnownLocation() {

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if(lastKnownLocation!=null)
        {
            mMap.clear();
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            // Add a marker in Sydney and move the camera
            LatLng userLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
            mMap.addMarker(new MarkerOptions().position(userLocation).title("My location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,20f));
        }else
        {
            Log.i(TAG, "lastKnownLocation=null");
        }



    }

    private void getLocation() {
        Log.i(TAG, "getLocation: ");
         locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
          locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.i(TAG, location.toString());
                showOnMap(location.getLatitude(),location.getLongitude());


                /** to stop listening to location changes after first time*/
                locationManager.removeUpdates(locationListener);
                locationManager = null;

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    1000, // every 1 seconds it will check if location changed
                    0, // minimum distance in meters that will trigger the onLocationChange function
                    locationListener);
        }

    }

    private void showOnMap(double latitude, double longitude) {


        /** show given location with marker and zoom 15/20 */
        Log.i(TAG, "showOnMap: ");
        mMap.clear();
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        // Add a marker in Sydney and move the camera
        LatLng userLocation = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(userLocation).title("My location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,15f));

        /**show toast with address of that location*/
        Geocoder geocoder=new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> fromLocation = geocoder.getFromLocation(latitude, longitude, 1);
            if(fromLocation!=null  && fromLocation.size()>0)
            {
                String subAdminArea = fromLocation.get(0).getSubAdminArea();
                String thoroughfare = fromLocation.get(0).getThoroughfare();
                /** show which exists of area and street or both*/
                String address="";
                if(thoroughfare!=null  )
                  address+=thoroughfare+"\n";

                if (subAdminArea!=null)
                address+=subAdminArea;


                    Toast.makeText(this, address, Toast.LENGTH_SHORT).show();


            }else
            {
                Log.i(TAG, "fromLocation is empty");
            }

        } catch (Exception e) {
            Log.i(TAG, "getFromLocation exception:"+e.getMessage());
            Toast.makeText(this, "getFromLocation exception:"+e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    Toast.makeText(MapsActivity.this, "Permission Granted", Toast.LENGTH_SHORT)
                            .show();
                  getLocation();
                } else {
                    // Permission Denied
                    Toast.makeText(MapsActivity.this, "Permission Denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
