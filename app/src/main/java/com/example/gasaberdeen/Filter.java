package com.example.gasaberdeen;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


public class Filter extends AppCompatActivity {
    private static final String TAG = "FilterActivity";

    private static final String KEY_NAME = "Name";
    private static final String KEY_DIESEL = "Diesel";
    private static final String KEY_PETROL = "Petrol";

    private EditText editTextName;
    private EditText editTextDiesel;
    private EditText editTextPetrol;
    private EditText editTextMin;
    private EditText editTextMax;
    private EditText editTextMaxDist;
    private TextView textViewData;

    private FusedLocationProviderClient client;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference stationRef = db.collection("FuelStations");
    //    private  geoFirestore geoFire = GeoFirestore(collectionRef);
    private DocumentReference noteRef = db.document("FuelStations/hSCFiccIVGyG1E2RPN9i");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);


        requestPermission();
        client = LocationServices.getFusedLocationProviderClient(this);

        //try putting data here
        editTextMin = findViewById(R.id.edit_text_Min);
        editTextMax = findViewById(R.id.edit_text_Max);
        editTextMaxDist = findViewById(R.id.edit_text_MaxDist);
        textViewData = findViewById(R.id.text_view_data);

        //try putting the data here

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);


        //we will set home as selected

        bottomNavigationView.setSelectedItemId(R.id.filter);

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
                    case R.id.filter:
                        return true;

                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(),
                                MainActivity.class));
                        overridePendingTransition(0, 0);
                        return true;


                    case R.id.about:
                        startActivity(new Intent(getApplicationContext(),
                                About.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });
    }

    // requests user location tracking permission
    private void requestPermission(){
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
    }

    @Override
    protected void onStart() {
        super.onStart();
        stationRef.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }

                String data = "";

                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Note note = documentSnapshot.toObject(Note.class);
                    note.setDocumentId(documentSnapshot.getId());

//                    String documentId = note.getDocumentId();
                    String name = note.getName();
                    float diesel = note.getDiesel();
                    float petrol = note.getPetrol();

                    data += "\nName: " + name + "\nPetrol: " + petrol + "\nDiesel: " + diesel + "\n\n";
                }

                textViewData.setText(data);
            }
        });
    }

    public void loadNotes(View v) {

        if (ActivityCompat.checkSelfPermission(Filter.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            textViewData.setText("Please allow location permissions");
            return;
        }

        client.getLastLocation().addOnSuccessListener(Filter.this, new OnSuccessListener<Location>() {

            @Override
            public void onSuccess(final Location location) {

                if (location != null) {

                    // query firebase and display data
                    stationRef.orderBy("name", Query.Direction.ASCENDING)
                            .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                            String data = "";
                            // get user current location
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
//                            data += "Current Location" + "\nLatitude: " + latitude + "\nLongitude: " + longitude + "\n"; // debugging
                            // iterate through each data entry
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                Note note = documentSnapshot.toObject(Note.class);
                                note.setDocumentId(documentSnapshot.getId());

                                String documentId = note.getDocumentId();
                                String Name = note.getName();
                                float Diesel = note.getDiesel();
                                float Petrol = note.getPetrol();

                                // get station location
                                double stationLat = note.getLatitude();
                                double stationLong = note.getLongitude();
                                double dist = distance(latitude, stationLat, longitude, stationLong);

                                dist = Math.round(dist * 100.0)/100.0;
                                data += "\nName: " + Name + "\nPetrol: " + Petrol + "\nDiesel: " + Diesel + "\nDistance: " + dist + "km\n";

                            }
                            // display data
                            data += "\n";
                            textViewData.setText(data);

                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, e.toString());
                                }
                            });
                }
            }
        });


        // sort data entries by name initially
//        stationRef.orderBy("name", Query.Direction.ASCENDING)
//                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//            @Override
//            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                String data = "";
//
//                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
//                    Note note = documentSnapshot.toObject(Note.class);
//                    note.setDocumentId(documentSnapshot.getId());
//
////                    String documentId = note.getDocumentId();   // debugging
//                    String Name = note.getName();
//                    float Diesel = note.getDiesel();
//                    float Petrol = note.getPetrol();
//
//                    data += "\nName: " + Name + "\nPetrol: " + Petrol + "\nDiesel: " + Diesel + "\n\n";
//                }
//                // display data on app
//                textViewData.setText(data);
//
//            }
//        });
    }

    // sort and filter petrol
    @SuppressLint("SetTextI18n")
    public void filterp(View v) {
        // if no user input for min/max, default to 0 and 999 respectively
        if (editTextMin.length() == 0) {
            editTextMin.setText("0");
        }
        if (editTextMax.length() == 0) {
            editTextMax.setText("999");
        }

        if (editTextMaxDist.length() == 0) {
            editTextMaxDist.setText("99999999999999");
        }

        if (ActivityCompat.checkSelfPermission(Filter.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            textViewData.setText("Please allow location permissions");
            return;
        }

        client.getLastLocation().addOnSuccessListener(Filter.this, new OnSuccessListener<Location>() {

            @Override
            public void onSuccess(final Location location) {

                if (location != null) {

                    // query firebase and display data
                    stationRef
                            .whereGreaterThanOrEqualTo("petrol", Float.parseFloat(editTextMin.getText().toString()))
                            .whereLessThanOrEqualTo("petrol", Float.parseFloat(editTextMax.getText().toString()))
                            .orderBy("petrol", Query.Direction.ASCENDING)
                            .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                            String data = "";
                            // get user current location
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
//                            data += "Current Location" + "\nLatitude: " + latitude + "\nLongitude: " + longitude + "\n"; //debugging
                            // iterate through each data entry
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                Note note = documentSnapshot.toObject(Note.class);
                                note.setDocumentId(documentSnapshot.getId());

                                String documentId = note.getDocumentId();
                                String Name = note.getName();
                                float Diesel = note.getDiesel();
                                float Petrol = note.getPetrol();

                                // get station location
                                double stationLat = note.getLatitude();
                                double stationLong = note.getLongitude();
                                double dist = distance(latitude, stationLat, longitude, stationLong);

                                // filter distance
                                if (dist <= Double.parseDouble(editTextMaxDist.getText().toString())) {
                                    dist = Math.round(dist * 100.0)/100.0; // round to two decimal places
                                    data += "\nName: " + Name + "\nPetrol: " + Petrol + "\nDiesel: " + Diesel + "\nDistance: " + dist + "km\n";
                                }
                            }
                            // display data
                            data += "\n";
                            textViewData.setText(data);

                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, e.toString());
                                }
                            });
                }
            }
        });
    }

    // filter and sort diesel
    @SuppressLint("SetTextI18n")
    public void filterd(View v) {
        // if no user input for min/max, default to 0 and 999 respectively
        if (editTextMin.length() == 0) {
            editTextMin.setText("0");
        }
        if (editTextMax.length() == 0) {
            editTextMax.setText("999");
        }

        if (editTextMaxDist.length() == 0) {
            editTextMaxDist.setText("99999999999999");
        }

        if (ActivityCompat.checkSelfPermission(Filter.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            textViewData.setText("Please allow location permissions");
            return;
        }

        client.getLastLocation().addOnSuccessListener(Filter.this, new OnSuccessListener<Location>() {

            @Override
            public void onSuccess(final Location location) {

                if (location != null) {

                    // query firebase and display data
                    stationRef
                            .whereGreaterThanOrEqualTo("diesel", Float.parseFloat(editTextMin.getText().toString()))
                            .whereLessThanOrEqualTo("diesel", Float.parseFloat(editTextMax.getText().toString()))
                            .orderBy("diesel", Query.Direction.ASCENDING)
                            .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                            String data = "";
                            // get user current location
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
//                            data += "Current Location" + "\nLatitude: " + latitude + "\nLongitude: " + longitude + "\n"; //debugging
                            // iterate through each data entry
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                Note note = documentSnapshot.toObject(Note.class);
                                note.setDocumentId(documentSnapshot.getId());

                                String documentId = note.getDocumentId();
                                String Name = note.getName();
                                float Diesel = note.getDiesel();
                                float Petrol = note.getPetrol();

                                // get station location
                                double stationLat = note.getLatitude();
                                double stationLong = note.getLongitude();
                                double dist = distance(latitude, stationLat, longitude, stationLong);

                                // filter distance
                                if (dist <= Double.parseDouble(editTextMaxDist.getText().toString())) {
                                    dist = Math.round(dist * 100.0)/100.0;
                                    data += "\nName: " + Name + "\nPetrol: " + Petrol + "\nDiesel: " + Diesel + "\nDistance: " + dist + "km\n";
                                }
                            }
                            // display data
                            data += "\n";
                            textViewData.setText(data);

                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, e.toString());
                                }
                            });
                }
            }
        });
    }

    @SuppressLint("SetTextI18n")
    public void sortDist(View v) {

        if (ActivityCompat.checkSelfPermission(Filter.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            textViewData.setText("Please allow location permissions");
            return;
        }

        client.getLastLocation().addOnSuccessListener(Filter.this, new OnSuccessListener<Location>() {
            //            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(Location location) {

                if(location!=null){
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    String data = "";
                    data += "Latitude: " + latitude + "\nLongitude: " + longitude;
                    if (editTextMaxDist.length() == 0) {
                        editTextMaxDist.setText("99999999999999");
                    }
                    textViewData.setText(data);
                }
            }
        });
    }

    // method referenced from https://www.geeksforgeeks.org/program-distance-two-points-earth/
    public double distance(double lat1, double lat2, double lon1, double lon2){
        // Convert degrees to radians
        lon1 = Math.toRadians(lon1);
        lon2 = Math.toRadians(lon2);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        // Haversine formula
        double dlon = lon2 - lon1;
        double dlat = lat2 - lat1;
        double a = Math.pow(Math.sin(dlat / 2), 2) + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(dlon / 2),2);

        double c = 2 * Math.asin(Math.sqrt(a));

        // Earth radius in km
        double r = 6371;

        // calculate the result
        return(c * r);
    }
}
