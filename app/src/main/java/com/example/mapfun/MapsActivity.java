package com.example.mapfun;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.example.mapfun.Database.DatabaseHelper;
import com.example.mapfun.Database.PlaceModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "MapsActivityh";
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    private GoogleMap mMap;
    private LocationListener oneTimeLocationListener;
    private LocationListener trackingLocationListener;
    private LocationManager locationManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);





        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private boolean handleAdapterIntent() {
        if(getIntent().hasExtra("lat")&&getIntent().hasExtra("long")&&getIntent().hasExtra("address"))
        {
            String lat = getIntent().getStringExtra("lat");
            String longitude = getIntent().getStringExtra("long");
            String address = getIntent().getStringExtra("address");
            showOnMap(Double.valueOf(lat),Double.valueOf(longitude),address);
            return true;
        }
         return false;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;


        /** check if activity was opened from the adapter intent*/
        if(handleAdapterIntent())
        {
            return;
        }

        getPermission();

        addMarkerOnLongClick();

    }


    private void getPermission() {
        //no need to check if sdk is <23 since minsdk is 23
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat
                    .requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_ASK_PERMISSIONS);
        } else {

           showLastKnownLocation();
            /** this method will execute only once */
              getUserLocation();
            /** this method will execute every 15 secs to check if user moved and show it on map*/
            trackUserLocation();
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
            Log.i(TAG, "showLastKnownLocation: success");
            showOnMap(lastKnownLocation.getLatitude(),lastKnownLocation.getLongitude());
        }else
        {
            Log.i(TAG, "lastKnownLocation=null");

        }



    }

    private void getUserLocation() {



        Log.i(TAG, "getUserLocation once: ");
        setupOneTimeLocationListener();


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    0,
                    0, // minimum distance in meters that will trigger the onLocationChange function
                    oneTimeLocationListener);
        }

    }

    private void trackUserLocation() {

        /** this method will execute every 15 sec */

        Log.i(TAG, "trackUserLocation: each 15 sec ");
        setupTrackingLocationListener();


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    15, // every 15 seconds it will check if location changed
                    50, // minimum distance in meters that will trigger the onLocationChange function
                    trackingLocationListener);
        }

    }

    private void setupTrackingLocationListener() {
        trackingLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.i(TAG, location.toString());
                showOnMap(location.getLatitude(),location.getLongitude());

                Log.i(TAG, "TrackingonLocationChanged: ");
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
                /*deprecated */
            }

            @Override
            public void onProviderEnabled(String s) {
                Log.i(TAG, "onProviderEnabled: ");
            }

            @Override
            public void onProviderDisabled(String s) {
                Log.i(TAG, "onProviderDisabled: ");
                turnOnGPS();
            }
        };
    }

    private void setupOneTimeLocationListener() {
        oneTimeLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.i(TAG, location.toString());
                showOnMap(location.getLatitude(),location.getLongitude());


                Log.i(TAG, "1 -setupOneTimeLocationListener");
                /** to stop listening to location changes after first time*/
                if(locationManager!=null)
                {
                    Log.i(TAG, "2 -stoped listening ");
                    locationManager.removeUpdates(oneTimeLocationListener);
                    locationManager = null;
                }else
                {
                    Log.i(TAG, "2 -locationManager==null ");
                }


            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
                /*deprecated */
            }

            @Override
            public void onProviderEnabled(String s) {
                Log.i(TAG, "onProviderEnabled: ");
            }

            @Override
            public void onProviderDisabled(String s) {
                Log.i(TAG, "onProviderDisabled: ");
                turnOnGPS();
            }
        };
    }


    private void addMarkerOnLongClick() {
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                String adressFromLatLng = getAdressFromLatLng(latLng.latitude, latLng.longitude);
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(latLng.latitude, latLng.longitude))
                        .title( adressFromLatLng));

                savePlace(latLng.latitude,latLng.longitude,adressFromLatLng);
            }
        });
    }

    private long savePlace(double latitude, double longitude, String adressFromLatLng) {

            // get writable database as we want to write data
            SQLiteDatabase db = new DatabaseHelper(MapsActivity.this).getWritableDatabase();

            ContentValues values = new ContentValues();

            values.put(PlaceModel.COLUMN_ADDRESS, adressFromLatLng);
            values.put(PlaceModel.COLUMN_LATITUDE, latitude+"");
            values.put(PlaceModel.COLUMN_LONGITUDE, longitude+"");

            // insert row
            long id = db.insert(PlaceModel.TABLE_NAME, null, values);

            // close db connection
            db.close();

            // return newly inserted row id
        Log.i(TAG, "savePlace: "+id);

        return id;

    }

    private void turnOnGPS() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("GPS must be turned on to get your location")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
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
        String adressFromLatLng = getAdressFromLatLng(latitude, longitude);
        if(!adressFromLatLng.equals(""))
        Toast.makeText(this, adressFromLatLng, Toast.LENGTH_SHORT).show();

    }

    private void showOnMap(double latitude, double longitude,String adressFromLatLng) {


        /** show given location with marker and zoom 15/20 */
        Log.i(TAG, "showOnMap: ");
        mMap.clear();


        LatLng userLocation = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(userLocation).title(adressFromLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.like)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,20f));


        if(!adressFromLatLng.equals(""))
        Toast.makeText(this, adressFromLatLng, Toast.LENGTH_SHORT).show();

    }

    private String getAdressFromLatLng(double latitude, double longitude) {
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


                    return address;


            }else
            {
                Log.i(TAG, "fromLocation is empty");
                return"";
            }

        } catch (Exception e) {
            Log.i(TAG, "getFromLocation exception:"+e.getMessage());
            Toast.makeText(this, "getFromLocation exception:"+e.getMessage(), Toast.LENGTH_LONG).show();
            return"";
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
                  getUserLocation();
                } else {
                    // Permission Denied
                    Toast.makeText(MapsActivity.this, "Permission Denied \n Can't show your location", Toast.LENGTH_LONG)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }



}
