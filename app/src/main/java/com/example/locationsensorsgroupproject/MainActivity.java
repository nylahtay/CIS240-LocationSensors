package com.example.locationsensorsgroupproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    //Initialize variables
    TextView textView1, textView2, textView3, textView4, textView5;
    FusedLocationProviderClient fusedLocationProviderClient;
    double lat1, lat2, lon1,lon2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Assign the variables
        textView1 = findViewById(R.id.textView_location);



        //Initialize fusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        //this will populate the lat2 and lon2 with the phone's location onCreate;
        getLocation();


    }




    public void submitForm(View V){
        TextView locationRequested = findViewById(R.id.locationRequested);
        String location =  locationRequested.getText().toString();
        TextView results = findViewById(R.id.results);
        results.setText("Results: "+getLatLongFromString(location));
    }

    private double distance(double longitude1,double latitude1, double longitude2,double latitude2){
        double lon1 = Math.toRadians(longitude1);
        double lon2 = Math.toRadians(longitude2);
        double  lat1 = Math.toRadians(latitude1);
        double  lat2 = Math.toRadians(latitude2);

        double dlon = lon2 - lon1;
        double dlat = lat2 - lat1;
        double a = Math.pow(Math.sin(dlat / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.pow(Math.sin(dlon / 2),2);

        double c = 2 * Math.asin(Math.sqrt(a));

        // Radius of earth in kilometers. Use 3956
        // for miles
        double r = 6371;

        // calculate the result
        double distance = c * r;

        return Math.floor(distance*.621371);
    }


    public String getLatLongFromString(String location){
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocationName(location, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try{
            Address address = addresses.get(0);


            // output the gps as lat/lon
            //return "Lat/Lon: "+address.getLatitude() +" "+address.getLongitude();

            // output distance
            // create a second location
            // this is the white house in washington dc



            //using getLocation to update the lat2 and lon2
            getLocation();


            return "Distance to the White House is "+distance(address.getLongitude(),  address.getLatitude(), lon2, lat2)+" miles";

        }catch(Exception e){
            return "Could not locate";
        }

    }



    private void getLocation() {

        //Check to see if you have permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // calling ActivityCompat#requestPermissions here to request the missing permissions

            String errorString = "Permission Denied";
            Toast.makeText(MainActivity.this, errorString, Toast.LENGTH_LONG).show();
            textView1.setText(errorString);

            //this will ask the user for permission
            // to handle the case where the user grants the permission.
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);

            return;
        }

        //This will check the phones location
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                //Initialize location
                Location location = task.getResult();
                if (location != null) {
                    //Location isn't null
                    try {
                        //initialize the geoCoder
                        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());

                        //Initialize address list
                        List<Address> addresses = geocoder.getFromLocation(
                                location.getLatitude(), location.getLongitude(), 1
                        );

                        //Set latitude on TextView
                        textView1.setText(Html.fromHtml(
                                "<font color='#6200EE'><b>Country Name:</b><br></font>" + addresses.get(0).getCountryName()
                                        + " Address:" + addresses.get(0).getAddressLine(0) + " " + addresses.get(0).getAddressLine(1)
                                        + " Longitude:" + addresses.get(0).getLongitude() + " Latitude:" + addresses.get(0).getLatitude()

                        ));
                        String coordinates = addresses.get(0).getLatitude() + "," + addresses.get(0).getLongitude();

                        //sending the location to the interface GPSCallback
                        lat2 = addresses.get(0).getLatitude();
                        lon2 = addresses.get(0).getLongitude();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }
                else {
                    //location returned null from task.getResult()
                    String errorString = "Location is set to NULL on this device";
                    Toast.makeText(MainActivity.this, errorString, Toast.LENGTH_LONG).show();
                    textView1.setText(errorString);
                }
            }
        });
    }

}