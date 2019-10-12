package com.example.driverapp;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.*;


public class MainActivity extends AppCompatActivity {
    int count;
    TextView cv;
    int full;
    String id;
    Location publicLocation = new Location("dummy");
    private FusedLocationProviderClient fusedLocationClient;
    DatabaseReference shuttledb;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cv = findViewById(R.id.counter_view);
        shuttledb = FirebaseDatabase.getInstance().getReference();

        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        sharedPreferences = getPreferences(MODE_PRIVATE);
        updateLocation();
        //shuttleCabs shuttleCab = shuttleCab.get(i);
        final Handler handler = new Handler();
        final int delay = 10000; //milliseconds
        handler.postDelayed(new Runnable(){
            public void run(){
                updateLocation();
                sendLocation();
                handler.postDelayed(this, delay);
            }
        }, delay);
        /*FirebaseDatabase.getInstance().getReference()
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            shuttleCabs shuttleCab = snapshot.getValue(shuttleCabs.class);
                            LatLng shuttle = new LatLng(shuttleCab.getCabx(),shuttleCab.getCaby() );
                            if (shuttleCab.getCabpax()>=13) continue;
                            else cabLocs.add(shuttle);
                        }
                        closestCab = findClosest(cabLocs);
                        map.addMarker(new MarkerOptions().position(closestCab).title("Closest Shuttle"));
                        LatLng publicLatLng = new LatLng(publicLocation.getLatitude(),publicLocation.getLongitude());
                        String url = getDirectionsUrl(publicLatLng,closestCab);
                        new FetchURL(MapActivity.this   ).execute(url, "driving");

                        Location dest = new Location("dummy");
                        dest.setLatitude(closestCab.latitude);
                        dest.setLongitude(closestCab.longitude);
                        String timeleft = "Nearest cab ETA: " + Integer.toString((int)publicLocation.distanceTo(dest)/300)+ " minutes";
                        timeLeft.setText(timeleft);

                        Toast.makeText(MapActivity.this, "Got till here", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                }*/
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        shuttledb.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                for (DataSnapshot cabSnapshot : dataSnapshot.getChildren()){
//                    shuttleCabs shuttleCab = cabSnapshot.getValue(shuttleCabs.class);
//
//                }
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }
    public void swap(){
        final Button changeSpace = (Button) findViewById(R.id.button_changespace);
        if (full==0){
            full = 1;
            changeSpace.setText("FULL");
            changeSpace.setBackgroundColor(Color.RED);
        }
        else{
            full = 0;
            changeSpace.setText("VACANT");
            changeSpace.setBackgroundColor(Color.GREEN);
        }
    }


    public void updateLocation(){
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {

                        }
                        publicLocation=location;
                    }
                });

    }


    public void sendLocation(){
        if (sharedPreferences.getBoolean("firstrun", true)){
            id = shuttledb.push().getKey();
            sharedPreferences.edit().putBoolean("firstrun", false).apply();
            sharedPreferences.edit().putString("myID", id).apply();
        }
        else{
            id = sharedPreferences.getString("myID", "null");
        }
        double x = publicLocation.getLatitude();
        double y = publicLocation.getLongitude();
        shuttleCabs shuttleCab = new shuttleCabs(id,x,y,count, full);
        shuttledb.child(id).setValue(shuttleCab);
        Toast.makeText(this, "Record updated", Toast.LENGTH_SHORT).show();
    }

    public void add(View view){
        count++;
        cv.setText(""+count);
        sendLocation();
    }

    public void reduce(View view){
        if (count > 0){
            count--;
            cv.setText(""+count);
            sendLocation();
        }

    }

    public void changeSpace(View view){
        //Toast.makeText(this, "Works till here!!!", Toast.LENGTH_SHORT).show();
        swap();
    }

    public void update(View view){
        updateLocation();
        sendLocation();
    }
}
