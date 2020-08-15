package com.example.gasaberdeen;
import android.Manifest;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.SearchView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.gms.location.LocationListener;

import static com.example.gasaberdeen.Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import org.w3c.dom.ls.LSException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class About extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    //for our application we will need to implement
    //a map activity using the Google Maps API
    //a location on said map
    //and a permission request for using GPS services
    //that must be accepted
    //otherwise return null map (the world map)

    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    double currentLatitude, currentLongitude;
    Location myLocation;
    private boolean mLocationPermissionGranted = false;

    private final static int REQUEST_CHECK_SETTINGS_GPS = 0x1;
    private final static int REQUEST_ID_MULTIPLE_PERMISSIONS = 0x2;

    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;


    SupportMapFragment mapFragment;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        //after getting permissions
        //we will fetch location from
        //the google map fragment


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fetchLastLocation();

        searchView = findViewById(R.id.sv_location);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.google_map);
        
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            //we have also implemented a search options
            //where everyone can query a location
            //but for this version of the application
            //only querying Aberdeen, United Kingdowm
            //will return gas station data


            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = searchView.getQuery().toString();
                List<Address> addressList = null;

                if (location != null || !location.equals("")) {
                    Geocoder geocoder = new Geocoder(About.this);
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Address address = addressList.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(),address.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(latLng).title(location));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });



        mapFragment.getMapAsync(this);
        setUPGCLient();




        //here we will
        //initialize and assign variables

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);


        //we will set home as selected

        bottomNavigationView.setSelectedItemId(R.id.about);

        //we will perform ItemSelectedListener

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.dashboard:
                        startActivity(new Intent(getApplicationContext(),
                                Dashboard.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(),
                                MainActivity.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.filter:
                        startActivity(new Intent(getApplicationContext(),
                                Filter.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.about:
                        return true;
                }
                return false;
            }
        });

    }

    private void setUPGCLient() {

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0, this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();

    }


    //unfinished function
    private void fetchLastLocation() {
      //  Task<Location> task = fusedLocationProviderClient.getLastLocation();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        googleMap.setMapStyle( MapStyleOptions.loadRawResourceStyle( this, R.raw.gasaberdeenstyles));

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    //our permission check is tested
    //anytime a user has their GPS on
    //and runs the application


    @Override
    public void onConnected(@Nullable Bundle bundle) {

        checkPermission();

    }


    //our Permission Check consists of
    //accessing Manifest permissions
    //i.e Location Data


    private void checkPermission() {

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED)
        {mLocationPermissionGranted = true;
        getMyLocation();
        }
        else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        int permissionLocation = ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionLocation == PackageManager.PERMISSION_GRANTED) {

            getMyLocation();




        }

        else {
            checkPermission();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    //we need an algorithm
    //that will display all gas stations
    //around the city of Aberdeen
    //using realtime location
    //latitude and longitude
    //and will keep those stations when location changes


    @Override
    public void onLocationChanged(Location location) {

        myLocation = location;

        if (myLocation != null) {
            currentLatitude = location.getLatitude();
            currentLongitude = location.getLongitude();


            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.navigation);
            BitmapDescriptor stationicon = BitmapDescriptorFactory.fromResource(R.drawable.asd);
            BitmapDescriptor ev_charger = BitmapDescriptorFactory.fromResource(R.drawable.evfg);

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLatitude, currentLongitude), 13));

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(new LatLng(currentLatitude, currentLongitude));
            markerOptions.title("You are here.");
            markerOptions.icon(icon);
            mMap.addMarker(markerOptions);

            MarkerOptions morrisons = new MarkerOptions();
            morrisons.position(new LatLng(57.153414, -2.098051));
            morrisons.title("Morissons, Check Filter for Information");
            morrisons.icon(stationicon);
            mMap.addMarker(morrisons);

            MarkerOptions esso = new MarkerOptions();
            esso.position(new LatLng(57.162009, -2.096277));
            esso.title("Esso Petrol Station, Check Filter for Information");
            esso.icon(stationicon);
            mMap.addMarker(esso);

            MarkerOptions shelldon = new MarkerOptions();
            shelldon.position(new LatLng(57.173985, -2.091349));
            shelldon.title("Shell, Check Filter for Information, Offers Premium Fuel");
            shelldon.icon(stationicon);
            mMap.addMarker(shelldon);

            MarkerOptions asdacheapest = new MarkerOptions();
            asdacheapest.position(new LatLng(57.122572, -2.124924));
            asdacheapest.title("Asda Bridge of Dee, Check Filter for Information");
            asdacheapest.icon(stationicon);
            mMap.addMarker(asdacheapest);

            MarkerOptions tescoextra = new MarkerOptions();
            tescoextra.position(new LatLng(57.179422, -2.149954));
            tescoextra.title("Tesco Extra, Check Filter for Information, Offers Premium Fuel");
            tescoextra.icon(stationicon);
            mMap.addMarker(tescoextra);


            MarkerOptions BP = new MarkerOptions();
            BP.position(new LatLng(57.137267, -2.096653));
            BP.title("BP, Check Filter for Information, Offers Premium Fuel");
            BP.icon(stationicon);
            mMap.addMarker(BP);

            MarkerOptions coopgas = new MarkerOptions();
            coopgas.position(new LatLng(57.137380, -2.153966));
            coopgas.title("CO-OP, Check Filter for Information");
            coopgas.icon(stationicon);
            mMap.addMarker(coopgas);

            MarkerOptions gulfgas = new MarkerOptions();
            gulfgas.position(new LatLng(57.196699, -2.208978));
            gulfgas.title("Gulf Service Station, Check Filter for Information");
            gulfgas.icon(stationicon);
            mMap.addMarker(gulfgas);

            //call EV stations and places

            MarkerOptions ev_option_1 = new MarkerOptions();
            ev_option_1.position(new LatLng(57.167498, -2.098729));
            ev_option_1.title("Chargeplace Scotland Charging Station");
            ev_option_1.icon(ev_charger);
            mMap.addMarker(ev_option_1);

            MarkerOptions ev_option_2 = new MarkerOptions();
            ev_option_2.position(new LatLng(57.150212, -2.098645));
            ev_option_2.title("PodPoint Charging Station");
            ev_option_2.icon(ev_charger);
            mMap.addMarker(ev_option_2);

            MarkerOptions ev_option_3 = new MarkerOptions();
            ev_option_3.position(new LatLng(57.144180, -2.093776));
            ev_option_3.title("Franklin Energy");
            ev_option_3.icon(ev_charger);
            mMap.addMarker(ev_option_3);

            MarkerOptions ev_option_4 = new MarkerOptions();
            ev_option_4.position(new LatLng(57.180374, -2.150676));
            ev_option_4.title("ChargePoint Charging Station");
            ev_option_4.icon(ev_charger);
            mMap.addMarker(ev_option_4);

            MarkerOptions tesla1 = new MarkerOptions();
            tesla1.position(new LatLng(57.105216, -2.267963));
            tesla1.title("Tesla Destination Charger");
            tesla1.icon(ev_charger);
            mMap.addMarker(tesla1);

            MarkerOptions tesla2 = new MarkerOptions();
            tesla2.position(new LatLng(57.179575, -2.176281));
            tesla2.title("EV Charger Point");
            tesla2.icon(ev_charger);
            mMap.addMarker(tesla2);

            MarkerOptions tesla3 = new MarkerOptions();
            tesla3.position(new LatLng(57.360823, -2.313512));
            tesla3.title("EV Charger Point");
            tesla3.icon(ev_charger);
            mMap.addMarker(tesla3);









            getNearbyGAS();

        }




    }

    private void getNearbyGAS() {

        StringBuilder stringBuilder =
                new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        stringBuilder.append("location="+String.valueOf(currentLatitude)+","+String.valueOf(currentLongitude));
        stringBuilder.append("&radius=1000");
        stringBuilder.append("&type=gasstation");
        stringBuilder.append("&key="+getResources().getString(R.string.map_key));

        String url = stringBuilder.toString();

        Object dataTransfer[] = new Object[2];
        dataTransfer[0] = mMap;
        dataTransfer[1] = url;

        GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();



    }


    //cast an algorithm that uses
    //high accuracy when implementing
    //realtime location

    private void getMyLocation(){
        if(mGoogleApiClient!=null) {
            if (mGoogleApiClient.isConnected()) {
                int permissionLocation = ContextCompat.checkSelfPermission(About.this,
                        Manifest.permission.ACCESS_FINE_LOCATION);
                if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                    myLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                    LocationRequest locationRequest = new LocationRequest();
//                    locationRequest.setInterval(3000);
//                    locationRequest.setFastestInterval(3000);
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                            .addLocationRequest(locationRequest);
                    builder.setAlwaysShow(true);
                    LocationServices.FusedLocationApi
                            .requestLocationUpdates(mGoogleApiClient, locationRequest, this);
                    PendingResult<LocationSettingsResult> result =
                            LocationServices.SettingsApi
                                    .checkLocationSettings(mGoogleApiClient, builder.build());
                    result.setResultCallback(new ResultCallback<LocationSettingsResult>() {

                        @Override
                        public void onResult(LocationSettingsResult result) {
                            final Status status = result.getStatus();
                            switch (status.getStatusCode()) {
                                case LocationSettingsStatusCodes.SUCCESS:
                                    // All location settings are satisfied.
                                    // You can initialize location requests here.
                                    int permissionLocation = ContextCompat
                                            .checkSelfPermission(About.this,
                                                    Manifest.permission.ACCESS_FINE_LOCATION);
                                    if (permissionLocation == PackageManager.PERMISSION_GRANTED) {


                                        myLocation = LocationServices.FusedLocationApi
                                                .getLastLocation(mGoogleApiClient);


                                    }
                                    break;
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    // Location settings are not satisfied.
                                    // But could be fixed by showing the user a dialog.
                                    try {
                                        // Show the dialog by calling startResolutionForResult(),
                                        // and check the result in onActivityResult().
                                        // Ask to turn on GPS automatically
                                        status.startResolutionForResult(About.this,
                                                REQUEST_CHECK_SETTINGS_GPS);


                                    } catch (IntentSender.SendIntentException e) {
                                        // Ignore the error.
                                    }


                                    break;
                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                    // Location settings are not satisfied.
                                    // However, we have no way
                                    // to fix the
                                    // settings so we won't show the dialog.
                                    // finish();
                                    break;
                            }
                        }
                    });

                }
            }
        }
    }

}
