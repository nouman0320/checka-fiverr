package com.programrabbit.checka;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class CheckServiceActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    ArrayList<Service> myServiceData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_service);
        getSupportActionBar().hide();

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        myServiceData = new ArrayList<>();

        myServiceData.add(new Service("Service 1", "Street123, Abc", 0, 0,new LatLng(0,0), "Jan 01, 2019"));
        myServiceData.add(new Service("Service 2", "Street123, Abc", 1, 1,new LatLng(0,0), "Jan 01, 2019"));
        myServiceData.add(new Service("Service 3", "Street123, Abc", 2, 2,new LatLng(0,0), "Jan 01, 2019"));
        myServiceData.add(new Service("Service 4", "Street123, Abc", 0, 0,new LatLng(0,0), "Jan 01, 2019"));


        // specify an adapter (see also next example)
        mAdapter = new ServiceAdapter(this, myServiceData);
        recyclerView.setAdapter(mAdapter);
    }
}
