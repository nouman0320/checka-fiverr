package com.programrabbit.checka;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class CheckPriceActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    ArrayList<Price> myPriceData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_price);


        getSupportActionBar().hide();

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        myPriceData = new ArrayList<>();

      myPriceData.add(new Price("Item 1", "Street# 1, abc", "Jan 02, 2019", new LatLng(0,0), 1000.0));
      myPriceData.add(new Price("Item 2", "Street# 1, abc", "Jan 02, 2019", new LatLng(0,0), 2000.0));
      myPriceData.add(new Price("Item 3", "Street# 1, abc", "Jan 02, 2019", new LatLng(0,0), 3000.0));
      myPriceData.add(new Price("Item 4", "Street# 1, abc", "Jan 02, 2019", new LatLng(0,0), 4000.0));

        // specify an adapter (see also next example)
        mAdapter = new PriceAdapter(this, myPriceData);
        recyclerView.setAdapter(mAdapter);

    }
}
