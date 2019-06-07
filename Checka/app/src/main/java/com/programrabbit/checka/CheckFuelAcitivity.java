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

public class CheckFuelAcitivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FuelAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;


    ImageView iv_back;
    FloatingActionButton fab;

    ArrayList<Fuel> myFuelData;

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
        getSupportActionBar().hide();
        setContentView(R.layout.activity_check_fuel_acitivity);

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
                Intent mainIntent = new Intent(CheckFuelAcitivity.this, NewFuelActivity.class);
                CheckFuelAcitivity.this.startActivity(mainIntent);
            }
        });

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        myFuelData = new ArrayList<>();

        /*
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
        */

        databaseReference = FirebaseDatabase.getInstance().getReference("Fuel");
        firebaseAuth = FirebaseAuth.getInstance();

        // specify an adapter (see also next example)
        mAdapter = new FuelAdapter(CheckFuelAcitivity.this, myFuelData, keys);
        recyclerView.setAdapter(mAdapter);


        progressDialog.show();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Fuel> s_list = new ArrayList<>();
                keys = new ArrayList<>();
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    s_list.add(ds.getValue(Fuel.class));
                    keys.add(ds.getKey());
                    if(ds.getValue(Fuel.class).name == null){
                        databaseReference.child(ds.getKey()).removeValue();
                        Intent i = new Intent(CheckFuelAcitivity.this, CheckFuelAcitivity.class);
                        startActivity(i);
                        finish();
                    }
                    else{
                        Log.w("Firebase", ds.getValue(Fuel.class).name);
                        myFuelData = s_list;
                        mAdapter.add(myFuelData, keys);
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
