package com.example.mapfun;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.example.mapfun.Database.DatabaseHelper;
import com.example.mapfun.Database.PlaceModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivityh";
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);




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
        // get readable database as we are not inserting anything
        SQLiteDatabase db = new DatabaseHelper(this).getReadableDatabase();
        List<PlaceModel> placeModelList = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + PlaceModel.TABLE_NAME + " ORDER BY " +
                PlaceModel.COLUMN_ID + " DESC";

        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                PlaceModel placeModel = new PlaceModel();
                placeModel.setId(cursor.getInt(cursor.getColumnIndex(placeModel.COLUMN_ID)));
                placeModel.setLatitude(cursor.getString(cursor.getColumnIndex(placeModel.COLUMN_LATITUDE)));
                placeModel.setLongitude(cursor.getString(cursor.getColumnIndex(placeModel.COLUMN_LONGITUDE)));
                placeModel.setAddress(cursor.getString(cursor.getColumnIndex(placeModel.COLUMN_ADDRESS)));

                placeModelList.add(placeModel);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        initRecycler(placeModelList);

    }

    private void initRecycler(List<PlaceModel> placeModelList) {
        recyclerView = findViewById(R.id.recyclerView);
        PlacesAdapter placesAdapter=new PlacesAdapter(this,placeModelList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(placesAdapter);
    }


    @Override
    protected void onStart() {
        super.onStart();

        getPlaces();
    }
}
