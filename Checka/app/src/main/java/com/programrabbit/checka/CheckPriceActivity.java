package com.programrabbit.checka;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class CheckPriceActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    ArrayList<Price> myPriceData;

    ImageView iv_back;
    FloatingActionButton fab;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_price);

        iv_back = findViewById(R.id.iv_back);
        fab= findViewById(R.id.fab);


        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(CheckPriceActivity.this, NewPriceActivity.class);
                CheckPriceActivity.this.startActivity(mainIntent);
            }
        });

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
        myPriceData.add(new Price("Item 1", "Street# 1, abc", "Jan 02, 2019", new LatLng(0,0), 1000.0));
        myPriceData.add(new Price("Item 2", "Street# 1, abc", "Jan 02, 2019", new LatLng(0,0), 2000.0));
        myPriceData.add(new Price("Item 3", "Street# 1, abc", "Jan 02, 2019", new LatLng(0,0), 3000.0));
        myPriceData.add(new Price("Item 4", "Street# 1, abc", "Jan 02, 2019", new LatLng(0,0), 4000.0));

        // specify an adapter (see also next example)
        mAdapter = new PriceAdapter(this, myPriceData);
        recyclerView.setAdapter(mAdapter);

    }
}
