package com.programrabbit.checka;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class CheckFuelAcitivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    ArrayList<Fuel> myFuelData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_check_fuel_acitivity);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        myFuelData = new ArrayList<>();

        myFuelData.add(new Fuel("Pump 1", "Road 2, ABC ABC", new LatLng(0,0), true, "Jan 01, 2019"));
        myFuelData.add(new Fuel("Pump 2", "Road 2, ABC ABC", new LatLng(0,10), false, "Jan 01, 2019"));
        myFuelData.add(new Fuel("Pump 3", "Road 2, ABC ABC", new LatLng(0,20), true, "Jan 01, 2019"));
        myFuelData.add(new Fuel("Pump 1", "Road 2, ABC ABC", new LatLng(0,0), true, "Jan 01, 2019"));
        myFuelData.add(new Fuel("Pump 2", "Road 2, ABC ABC", new LatLng(0,10), false, "Jan 01, 2019"));
        myFuelData.add(new Fuel("Pump 3", "Road 2, ABC ABC", new LatLng(0,20), true, "Jan 01, 2019"));
        myFuelData.add(new Fuel("Pump 1", "Road 2, ABC ABC", new LatLng(0,0), true, "Jan 01, 2019"));
        myFuelData.add(new Fuel("Pump 2", "Road 2, ABC ABC", new LatLng(0,10), false, "Jan 01, 2019"));
        myFuelData.add(new Fuel("Pump 3", "Road 2, ABC ABC", new LatLng(0,20), true, "Jan 01, 2019"));
        myFuelData.add(new Fuel("Pump 1", "Road 2, ABC ABC", new LatLng(0,0), true, "Jan 01, 2019"));
        myFuelData.add(new Fuel("Pump 2", "Road 2, ABC ABC", new LatLng(0,10), false, "Jan 01, 2019"));
        myFuelData.add(new Fuel("Pump 3", "Road 2, ABC ABC", new LatLng(0,20), true, "Jan 01, 2019"));

        // specify an adapter (see also next example)
        mAdapter = new FuelAdapter(this, myFuelData);
        recyclerView.setAdapter(mAdapter);

    }
}
