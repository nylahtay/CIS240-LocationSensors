package com.example.locationsensorsgroupproject;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final int ACCESS_FINE_LOCATION = 44;
    //Initialize variables
    TextView TextViewCurrentLocation, TextViewDestinationLocation, TextViewDistance;
    FusedLocationProviderClient fusedLocationProviderClient;
    Button btLocation;
    Double lat1, lon1, lat2, lon2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Assign the variables

        TextViewCurrentLocation = findViewById(R.id.txtLocation);
        TextViewDestinationLocation = findViewById(R.id.txtDestination);
        TextViewDistance = findViewById(R.id.txtResults);
        btLocation = findViewById(R.id.submitBtn);


        //Initialize fusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


        //Check to see if you have permission, if not request it. This will ask the user for permission
        //if checkForLocationPermission is true, then permission is already granted, otherwise the method will ask the user for permission
        if (checkForLocationPermission()) {
            getLocation();
        }
    }




    public void submitForm(View V){
        TextView locationRequested = findViewById(R.id.locationRequested);
        String location =  locationRequested.getText().toString();
        String destCoord = getLatLongFromString(location);
        TextViewDestinationLocation.setText(destCoord);
        if(destCoord == "Could Not Find Location"){
            TextViewDistance.setText("Could Not Find Location");

        }else{
            TextViewDistance.setText(String.format("%.1f",distance(lon1, lat1, lon2, lat2))+" Miles Away");
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
        //double r = 6371;
        double r = 3956;
        // calculate the result
        double distance = c * r;

        return Math.floor(distance);
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
            lat1 = address.getLatitude();
            lon1 = address.getLongitude();
            return String.format("%.4f",lat1)+", "+String.format("%.4f",lon1);

        }catch(Exception e){
            Toast.makeText(this, "Could not find location", Toast.LENGTH_SHORT).show();
            return "Could Not Find Location";
        }

    }



    //check for location permission
    //if checkForLocationPermission is true, then permission is already granted, otherwise the method will ask the user for permission
    private boolean checkForLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // calling ActivityCompat#requestPermissions here to request the missing permissions
            // to handle the case where the user grants the permission.
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
            return false;
        }
        else {
            return true;
        }
    }

    //This method is called after the user accepts or declines the permission request for location.
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case ACCESS_FINE_LOCATION:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                }  else {
                    // Explain to the user that the feature is unavailable because
                    // the features requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                    TextViewCurrentLocation.setText("Location permission is required to use this app.");

                }
                return;
        }
        // Other 'case' lines to check for other
        // permissions this app might request.
    }


    private void getLocation() {

        //Check to see if you have permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // calling ActivityCompat#requestPermissions here to request the missing permissions

            String errorString = "Permission Denied";
            Toast.makeText(MainActivity.this, errorString, Toast.LENGTH_LONG).show();
            TextViewCurrentLocation.setText(errorString);
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
                        TextViewCurrentLocation.setText(String.format("%.4f", addresses.get(0).getLatitude()) + "," + String.format("%.4f", addresses.get(0).getLongitude()));


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
                    TextViewCurrentLocation.setText(errorString);

                }
            }
        });
    }




    //This is the inflator for adding the share button
    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        getMenuInflater().inflate(R.menu.main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    //this method will run when a menu item is selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(checkForLocationPermission()) {
            switch (item.getItemId()) {
                case R.id.mShare:
                    Intent i = new Intent(
                            Intent.ACTION_SEND
                    );
                    i.setType("text/plain");
                    i.putExtra(
                            android.content.Intent.EXTRA_TEXT, String.format("%.4f", lat2) + ", " + String.format("%.4f", lon2) + "\n https://google.com/maps/search/" + String.format("%.4f", lat2) + "," + String.format("%.4f", lon2)
                    );
                    startActivity(Intent.createChooser(
                            i,
                            "Share Via"
                    ));
                    break;
            }
        }

        Toast.makeText(getApplicationContext(), "You clicked on menu share", Toast.LENGTH_SHORT).show();
        return super.onOptionsItemSelected(item);

    }
}