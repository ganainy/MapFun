package com.example.mapfun;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivityh";
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getPlaces();


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                startActivity(new Intent(getApplicationContext(), MapsActivity.class));
            }
        });
    }

    private void getPlaces() {
        SharedPreferences prefs = getSharedPreferences("MyPrefsFile", MODE_PRIVATE);

        String latitude = prefs.getString("latitude", "latitude");
        String longitude = prefs.getString("longitude", "longitude");
        String adressFromLatLng = prefs.getString("adressFromLatLng", "adressFromLatLng");


        String[] latitudeList = latitude.split("%");
        String[] longitudeList = longitude.split("%");
        String[] adressFromLatLngList = adressFromLatLng.split("%");

        initRecycler(latitudeList,longitudeList,adressFromLatLngList);

        //todo open location of selected place on map ,  each time i get SharedPreferences from map activity save it to another bigger home shared pref
    }


    private void initRecycler(String[] latitudeList, String[] longitudeList, String[] adressFromLatLngList) {
        recyclerView = findViewById(R.id.recyclerView);
        PlacesAdapter placesAdapter=new PlacesAdapter(this,latitudeList,longitudeList,adressFromLatLngList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(placesAdapter);
    }

}
