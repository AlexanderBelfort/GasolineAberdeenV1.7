package com.example.gasaberdeen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Dashboard extends AppCompatActivity {

    //we will be using a recyclerView to implement
    //data for our gas stations
    //implemented as a list

    RecyclerView recyclerView;

    List<GasStations> GasList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        recyclerView = findViewById(R.id.recyclerView);


        //we will intialize the data we have
        //and display of our recycler view

        initData();
        initRecyclerView();

        //and we will also need buttons
        //that we will use as a
        //way to get directions using external apps
        //which in our case will be Google Maps

        Button morrissons_button;
        Button shell_button;
        Button tesco_button;
        Button asda_button;
        Button tesco_extra_gas;
        Button bp_gas;
        Button coop_gas;
        Button gulf_gas;

        morrissons_button = (Button)findViewById(R.id.morrissons_button);
        shell_button = (Button)findViewById(R.id.shell_button);
        tesco_button = (Button)findViewById(R.id.tesco_button);
        asda_button = (Button)findViewById(R.id.asda_button);
        tesco_extra_gas = (Button)findViewById(R.id.tesco_extra_gas);
        bp_gas = (Button)findViewById(R.id.bp_gas);
        coop_gas = (Button)findViewById(R.id.coop_gas);
        gulf_gas = (Button)findViewById(R.id.gulf_gas);


        morrissons_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent i=new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("google.navigation:q=57.153414,-2.098051&mode=d"));

                startActivity(i);

            }
        });

        shell_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent i=new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("google.navigation:q=57.173985,-2.091349&mode=d"));

                startActivity(i);

            }
        });

        tesco_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent i=new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("google.navigation:q=57.162009,-2.096277&mode=d"));

                startActivity(i);

            }
        });

        asda_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent i=new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("google.navigation:q=57.122572,-2.124924&mode=d"));

                startActivity(i);

            }
        });

        tesco_extra_gas.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent i=new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("google.navigation:q=57.179422,-2.149954&mode=d"));

                startActivity(i);

            }
        });


        bp_gas.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent i=new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("google.navigation:q=57.137267,-2.096653&mode=d"));

                startActivity(i);

            }
        });


        coop_gas.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent i=new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("google.navigation:q=57.137380,-2.153966&mode=d"));

                startActivity(i);

            }
        });


        gulf_gas.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent i=new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("google.navigation:q=57.196699,-2.208978&mode=d"));

                startActivity(i);

            }
        });

        //here we will
        //initialize and assign variables

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);


        //we will set home as selected

        bottomNavigationView.setSelectedItemId(R.id.dashboard);

        //we will perform ItemSelectedListener

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.dashboard:
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
                        startActivity(new Intent(getApplicationContext(),
                                About.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });
    }
    private void initRecyclerView() {
        GasStationAdapter GasAdapter = new GasStationAdapter(GasList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(GasAdapter);
    }
    private void initData() {
        GasList = new ArrayList<>();
        //add morrisons with relevant info from google
        GasList.add(new GasStations("Morrisons Gas Station", "4.9", "Last Updated: Yesterday", "UNLEADED: CHECK FILTER \nDIESEL: CHECK FILTER"));
        GasList.add(new GasStations("Tesco Esso Express", "4.0", "Last Updated: Yesterday", "UNLEADED: CHECK FILTER \nDIESEL: CHECK FILTER"));
        GasList.add(new GasStations("Shell", "4.2", "Last Updated: Yesterday", "UNLEADED: CHECK FILTER \nDIESEL: CHECK FILTER"));
        GasList.add(new GasStations("Asda Gas Station", "4.1", "Last Updated: Yesterday", "UNLEADED: CHECK FILTER \nDIESEL: CHECK FILTER"));
        GasList.add(new GasStations("Tesco EXTRA Gas Station", "4.2", "Last Updated: Yesterday", "UNLEADED: CHECK FILTER \nSUPER UNLEADED: CHECK FILTER \nDIESEL: CHECK FILTER"));
        GasList.add(new GasStations("BP Gas Station","4.1","Last Updated: Yesterday","UNLEADED: CHECK FILTER \nDIESEL: CHECK FILTER"));
        GasList.add(new GasStations("CO-OP Petrol Station","4.8","Last Updated: Yesterday","UNLEADED: CHECK FILTER \nDIESEL: CHECK FILTER"));
        GasList.add(new GasStations("Gulf Service Station","3.8","Last Updated: Yesterday","UNLEADED: CHECK FILTER \nDIESEL: CHECK FILTER"));
        }

}
