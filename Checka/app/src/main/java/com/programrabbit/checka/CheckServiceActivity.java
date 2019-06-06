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

public class CheckServiceActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;


    ImageView iv_back;
    FloatingActionButton fab;

    ArrayList<Service> myServiceData;

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
        setContentView(R.layout.activity_check_service);
        getSupportActionBar().hide();

        progressDialog = new SpotsDialog(this, R.style.Custom);



        iv_back = findViewById(R.id.iv_back);
        fab= findViewById(R.id.fab);


        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        progressDialog = new SpotsDialog(this, R.style.Custom);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(CheckServiceActivity.this, NewServiceActivity.class);
                CheckServiceActivity.this.startActivity(mainIntent);
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

        myServiceData = new ArrayList<>();

        /*
        myServiceData.add(new Service("Service 1", "Street123, Abc", 0, 0,new LatLng(0,0), "Jan 01, 2019"));
        myServiceData.add(new Service("Service 2", "Street123, Abc", 1, 1,new LatLng(0,0), "Jan 01, 2019"));
        myServiceData.add(new Service("Service 3", "Street123, Abc", 2, 2,new LatLng(0,0), "Jan 01, 2019"));
        myServiceData.add(new Service("Service 4", "Street123, Abc", 0, 0,new LatLng(0,0), "Jan 01, 2019"));
        */

        // specify an adapter (see also next example)





        databaseReference = FirebaseDatabase.getInstance().getReference("Service");
        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog.show();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Service> s_list = new ArrayList<>();
                ArrayList<String> keys = new ArrayList<>();
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    s_list.add(ds.getValue(Service.class));
                    keys.add(ds.getKey());
                    Log.w("Firebase", ds.getValue(Service.class).name);
                    myServiceData = s_list;
                    mAdapter = new ServiceAdapter(CheckServiceActivity.this, myServiceData, keys);
                    recyclerView.setAdapter(mAdapter);
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
