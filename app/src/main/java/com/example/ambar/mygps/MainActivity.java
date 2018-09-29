package com.example.ambar.mygps;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.List;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener , View.OnClickListener, LocationListener{

    public static final String TAG="TAG";

    private static final int REQUEST_CODE=1000;
    GoogleApiClient googleApiClient;
    //Location location;
    //TextView TextLocation;

    TextView txtDistance;
    TextView txtTime;
    EditText edtAdress;
    EditText edtMilePerHour;
    EditText edtMeterPerMile;
    Button   BTNgetTheData;
    private String DestinationLocationAddress="";
    private TaxiManager taxiManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtDistance=(TextView) findViewById(R.id.TXTdistance);
        txtTime=(TextView) findViewById(R.id.TXTtime);
        edtAdress=(EditText) findViewById(R.id.EDTadress);
        edtMilePerHour=(EditText) findViewById(R.id.EDTmilePerHour);
        edtMeterPerMile=(EditText) findViewById(R.id.EDTmeterPerMile);
        BTNgetTheData=(Button)findViewById(R.id.BTNresult);

        BTNgetTheData.setOnClickListener(MainActivity.this);
        taxiManager= new TaxiManager();

        //TextLocation=findViewById(R.id.GPSTEXT);

        googleApiClient= new GoogleApiClient.Builder(MainActivity.this)
                .addOnConnectionFailedListener(MainActivity.this)
                .addConnectionCallbacks(MainActivity.this)
                .addApi(LocationServices.API).build();
    }

    @Override
    public void onLocationChanged(Location location) {
      onClick(null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        FusedLocationProviderApi fusedLocationProviderApi =LocationServices.FusedLocationApi;
        fusedLocationProviderApi.removeLocationUpdates(googleApiClient, MainActivity.this);
    }

    @Override
    public void onClick(View v) {
        String AddressValue=edtAdress.getText().toString();
        boolean isGoeCoding= true;
        if (!AddressValue.equals(DestinationLocationAddress)){
           DestinationLocationAddress= AddressValue;
            Geocoder geocoder= new Geocoder(getApplicationContext());
            try{
                List<Address> MyAddresses=geocoder.getFromLocationName(DestinationLocationAddress,4);
                if (MyAddresses!=null){
                 double Latitude = MyAddresses.get(0).getLatitude();
                 double Longtitude= MyAddresses.get(0).getLongitude();
                    Location LocationAdress= new Location("MyDestination");
                    LocationAdress.setLatitude(Latitude);
                    LocationAdress.setLongitude(Longtitude);

                    taxiManager.SetDestinationLocation(LocationAdress);


                }
            }catch(Exception e){
                isGoeCoding=false;
                e.printStackTrace();
            }

        }
        int PermissionCheck= ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION);
        if(PermissionCheck== PackageManager.PERMISSION_GRANTED){
            FusedLocationProviderApi fusedLocationProviderApi = LocationServices.FusedLocationApi;
            Location UserCurrentLocation = fusedLocationProviderApi.getLastLocation(googleApiClient);
            if (UserCurrentLocation!=null && isGoeCoding){
                txtDistance.setText(taxiManager.ReturnTheMileBetweenCurrentToDestination(UserCurrentLocation, Integer.parseInt(edtMeterPerMile.getText().toString())));
                txtTime.setText(taxiManager.TimeToGetToDestinatio(UserCurrentLocation, Float.parseFloat(edtMilePerHour.getText().toString()), Integer.parseInt(edtMeterPerMile.getText().toString())));
            }


        }
        else{
            txtDistance.setText("This application is NOT allowed to access the location");
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},1);

        }

    }




   // @SuppressLint("MissingPermission")
    @SuppressLint("MissingPermission")
    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "The connection is suspened.");
        FusedLocationProviderApi fusedLocationProviderApi= LocationServices.FusedLocationApi;
        @SuppressLint("RestrictedApi") LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        locationRequest.setSmallestDisplacement(5);
        if (googleApiClient.isConnected()){
            fusedLocationProviderApi.requestLocationUpdates(googleApiClient, locationRequest,MainActivity.this );
        }
        else{
          googleApiClient.connect();
        }




    }



    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "The connection failed.");
        if( connectionResult.hasResolution()){
            try{
            connectionResult.startResolutionForResult(MainActivity.this, REQUEST_CODE);
            }catch (Exception e){
              Log.d(TAG,e.getStackTrace().toString());

            }

        }
        else{
            Toast.makeText(MainActivity.this, "Google-Play Services Are NOT working", Toast.LENGTH_LONG).show();
            finish();

        }

    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "we are onnected t user location!:)");
       // ShowTheLOcation();



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode== REQUEST_CODE && resultCode== RESULT_OK){
            googleApiClient.connect();

        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(googleApiClient!= null){
            googleApiClient.connect();
        }

    }

    //custom method
   /* private void ShowTheLOcation() {
        int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);

        if (permissionCheck== PackageManager.PERMISSION_GRANTED){

            FusedLocationProviderApi fusedLOcationProviderApi= LocationServices.FusedLocationApi;
            location= fusedLOcationProviderApi.getLastLocation(googleApiClient);
            if (location!= null){
                Double Latitude= location.getLatitude();
                Double Longitude= location.getLongitude();
                TextLocation.setText(Latitude+ " , " + Longitude);
            }
            else{

                TextLocation.setText("The Location is not accessable right now"+ " try again later :)? ");
            }
        }
        else{

            TextLocation.setText("The app is now alloweded to access the location!");
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},1);
        }
    }*/

}
