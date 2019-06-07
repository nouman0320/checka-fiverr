package com.programrabbit.checka;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class DetailServiceActivity extends AppCompatActivity implements OnMapReadyCallback {


    private GoogleMap mMap;
    private View mapView;

    Service service;

    ImageView iv_back;
    TextView tv_name
            , tv_category
            , tv_problem
            , tv_address
            , tv_created_by
            , tv_updated_by
            , tv_date
            , tv_update_by_heading;

    Button btn_update
            ,btn_remove;

    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;

    String key;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_service);
        getSupportActionBar().hide();


        iv_back = findViewById(R.id.iv_back);
        tv_name = findViewById(R.id.tv_name);
        tv_category = findViewById(R.id.tv_category);
        tv_problem = findViewById(R.id.tv_problem);
        tv_address = findViewById(R.id.tv_address);
        tv_created_by = findViewById(R.id.tv_created_by);
        tv_updated_by = findViewById(R.id.tv_updated_by);
        tv_date = findViewById(R.id.tv_date);
        btn_remove = findViewById(R.id.btn_delete);
        btn_update = findViewById(R.id.btn_update);
        tv_update_by_heading = findViewById(R.id.tv_update_by_heading);

        tv_updated_by.setVisibility(View.GONE);
        tv_update_by_heading.setVisibility(View.GONE);

        service = (Service) getIntent().getSerializableExtra("service");
        key = getIntent().getStringExtra("key");

        tv_name.setText(service.getName());

        tv_date.setText(service.getLastUpdate());

        databaseReference = FirebaseDatabase.getInstance().getReference("Service");
        firebaseAuth = FirebaseAuth.getInstance();

        FirebaseDatabase.getInstance().getReference("User/"+service.getUid()).addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User temp = dataSnapshot.getValue(User.class);
                if(temp!=null){
                    tv_created_by.setText(temp.getName());
                }
                else
                    tv_created_by.setText("-");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                tv_created_by.setText("-");
            }
        });


        FirebaseDatabase.getInstance().getReference("User/"+service.getUid_updated()).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User temp = dataSnapshot.getValue(User.class);
                if(temp!=null){
                    tv_updated_by.setText(temp.getName());
                    tv_updated_by.setVisibility(View.VISIBLE);
                    tv_update_by_heading.setVisibility(View.VISIBLE);
                }
                else {
                    tv_updated_by.setText("-");
                    tv_updated_by.setVisibility(View.GONE);
                    tv_update_by_heading.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                tv_updated_by.setText("-");
                tv_updated_by.setVisibility(View.GONE);
                tv_update_by_heading.setVisibility(View.GONE);
            }
        });

        tv_address.setText(service.getAddress());

        switch(service.getServiceType()){
            case 0:
                tv_category.setText("Electricity");
                break;
            case 1:
                tv_category.setText("Water");
                break;
            case 2:
                tv_category.setText("Sewage");
                break;
        }


        switch(service.getProblemLevel()){
            case 0:
                tv_problem.setText("No Problem");
                break;
            case 1:
                tv_problem.setText("Medium Problem");
                break;
            case 2:
                tv_problem.setText("High Problem");
                break;
        }

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                rootRef.child("Service").child(key).removeValue();
                finish();
                Toast.makeText(getBaseContext(), "Removed", Toast.LENGTH_LONG).show();
            }
        });

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder b = new AlertDialog.Builder(DetailServiceActivity.this);
                b.setTitle("Update Problem Level");
                String[] types = {"No Problem ", "Medium Problem", "High Problem"};
                b.setItems(types, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                        switch(which){
                            case 0:
                                updateProblem(0);
                                break;
                            case 1:
                                //onCategoryRequested();
                                updateProblem(1);
                                break;
                            case 2:
                                updateProblem(2);
                                break;
                        }
                    }

                });

                b.show();
            }
        });


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapView = mapFragment.getView();

        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng markerLoc=new LatLng(service.getLat(), service.getLng());
        final CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(markerLoc)      // Sets the center of the map to Mountain View
                .zoom(13)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();                   //
        mMap.addMarker(new MarkerOptions().position(markerLoc).title(service.getName()));
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                return true;
            }
        });
        // add marker here
    }

    void updateProblem(int level){
        String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference("Service/"+key+"/problemLevel").setValue(level);
        //uid_updated
        FirebaseDatabase.getInstance().getReference("Service/"+key+"/uid_updated").setValue(currentUser);
        //
        DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy  HH:mm");
        Date datex = new Date();
        String date = dateFormat.format(datex);

        FirebaseDatabase.getInstance().getReference("Service/"+key+"/lastUpdate").setValue(date);

        switch(level){
            case 0:
                tv_problem.setText("No Problem");
                break;
            case 1:
                tv_problem.setText("Medium Problem");
                break;
            case 2:
                tv_problem.setText("High Problem");
                break;
        }

        FirebaseDatabase.getInstance().getReference("User/"+service.getUid_updated()).addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User temp = dataSnapshot.getValue(User.class);
                if(temp!=null){
                    tv_updated_by.setText(temp.getName());
                    tv_updated_by.setVisibility(View.VISIBLE);
                    tv_update_by_heading.setVisibility(View.VISIBLE);
                }
                else {
                    tv_updated_by.setText("-");
                    tv_updated_by.setVisibility(View.GONE);
                    tv_update_by_heading.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                tv_updated_by.setText("-");
                tv_updated_by.setVisibility(View.GONE);
                tv_update_by_heading.setVisibility(View.GONE);
            }
        });

        tv_date.setText(date);
    }
}
