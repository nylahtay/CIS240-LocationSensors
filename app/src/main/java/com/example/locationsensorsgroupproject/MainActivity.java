package com.example.locationsensorsgroupproject;

import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
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
            return address.getLongitude() + " "+ address.getLatitude();
        }catch(Exception e){
            return "Could not locate";
        }

    }

}