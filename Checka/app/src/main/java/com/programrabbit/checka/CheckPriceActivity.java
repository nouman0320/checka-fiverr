package com.programrabbit.checka;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import dmax.dialog.SpotsDialog;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class CheckPriceActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PriceAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    ArrayList<Price> myPriceData;

    ImageView iv_back;
    FloatingActionButton fab;

    ArrayList<String> keys = new ArrayList<>();

    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;

    private AlertDialog progressDialog;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_price);

        progressDialog = new SpotsDialog(this, R.style.Custom);


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
/*
      myPriceData.add(new Price("Item 1", "Street# 1, abc", "Jan 02, 2019", new LatLng(0,0), 1000.0));
      myPriceData.add(new Price("Item 2", "Street# 1, abc", "Jan 02, 2019", new LatLng(0,0), 2000.0));
      myPriceData.add(new Price("Item 3", "Street# 1, abc", "Jan 02, 2019", new LatLng(0,0), 3000.0));
      myPriceData.add(new Price("Item 4", "Street# 1, abc", "Jan 02, 2019", new LatLng(0,0), 4000.0));
        myPriceData.add(new Price("Item 1", "Street# 1, abc", "Jan 02, 2019", new LatLng(0,0), 1000.0));
        myPriceData.add(new Price("Item 2", "Street# 1, abc", "Jan 02, 2019", new LatLng(0,0), 2000.0));
        myPriceData.add(new Price("Item 3", "Street# 1, abc", "Jan 02, 2019", new LatLng(0,0), 3000.0));
        myPriceData.add(new Price("Item 4", "Street# 1, abc", "Jan 02, 2019", new LatLng(0,0), 4000.0));
*/

        databaseReference = FirebaseDatabase.getInstance().getReference("Price");
        firebaseAuth = FirebaseAuth.getInstance();
        // specify an adapter (see also next example)
        mAdapter = new PriceAdapter(CheckPriceActivity.this, myPriceData, keys);
        recyclerView.setAdapter(mAdapter);

        progressDialog.show();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Price> s_list = new ArrayList<>();
                keys = new ArrayList<>();
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    s_list.add(ds.getValue(Price.class));
                    keys.add(ds.getKey());
                    if(ds.getValue(Price.class).name == null){
                        databaseReference.child(ds.getKey()).removeValue();
                        Intent i = new Intent(CheckPriceActivity.this, CheckPriceActivity.class);
                        startActivity(i);
                        finish();
                    }
                    else{
                        Log.w("Firebase", ds.getValue(Price.class).name);
                        myPriceData = s_list;
                        mAdapter.add(myPriceData, keys);
                    }
                }
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Firebase", "onCancelled", databaseError.toException());
                progressDialog.dismiss();
            }
        });


    }
}
