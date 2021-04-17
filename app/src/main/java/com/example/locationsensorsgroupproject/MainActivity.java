package com.example.locationsensorsgroupproject;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void submitForm(View V){
        TextView locationRequested = findViewById(R.id.locationRequested);
        String location =  locationRequested.getText().toString();
        TextView results = findViewById(R.id.results);
        results.setText("Results: "+getLatLongFromString(location));
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
            double lat2 = 38.8976763;
            double lon2 = -77.0365298;
            return "Distance to the White House is "+distance(address.getLongitude(),  address.getLatitude(), lon2, lat2)+" miles";

        }catch(Exception e){
            return "Could not locate";
        }

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

}