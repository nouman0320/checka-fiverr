package com.programrabbit.checka;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
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

public class DetailPriceActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private View mapView;

    Price price;

    ImageView iv_back;
    TextView tv_name
            , tv_address
            , tv_created_by
            , tv_updated_by
            , tv_date
            , tv_update_by_heading;
    EditText et_price;

    Button btn_update
            ,btn_remove;

    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;

    FloatingActionButton fab_more;
    FloatingActionButton fab_up;
    FloatingActionButton fab_down;
    FloatingActionButton fab_comment;

    ConstraintLayout cl_admin;

    private BottomSheetBehavior mBottomSheetBehavior;

    String key;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_price);
        getSupportActionBar().hide();

        price = (Price) getIntent().getSerializableExtra("price");
        key = getIntent().getStringExtra("key");


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean admin = prefs.getBoolean("admin", false);
        cl_admin = findViewById(R.id.cl_admin);
        if(!admin)
            cl_admin.setVisibility(View.GONE);


        View bottomSheet = findViewById(R.id.bottom_sheet);

        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);


        fab_more = findViewById(R.id.fab_more);
        fab_up = findViewById(R.id.fab_thumbs_up);
        fab_down = findViewById(R.id.fab_thumbs_down);
        fab_comment = findViewById(R.id.fab_comments);

        fab_more.setVisibility(View.GONE);

        fab_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final ArrayList<String> positive = price.getPositiveVoteUsers();
        final ArrayList<String> negative = price.getNegativeVoteUsers();



        fab_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int user_vote_state = 0;
                for(int i=0;i<positive.size();i++)
                {
                    if(uid.equals(positive.get(i))){
                        user_vote_state = 1;
                        break;
                    }
                }

                if(user_vote_state != 0){
                    new MaterialStyledDialog.Builder(DetailPriceActivity.this)
                            .setIcon(R.drawable.ic_testing)
                            .setTitle("Vote")
                            .setDescription("You have already voted for this price update")
                            .setHeaderColor(R.color.colorPrimary)
                            .setPositiveText("Close")
                            .show();
                    return;
                }

                final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                Toast.makeText(DetailPriceActivity.this, "+1", Toast.LENGTH_SHORT).show();
                FirebaseDatabase.getInstance().getReference("Price")
                        .child(key+"/voteCount").setValue(price.getVoteCount()+1);
                price.getPositiveVoteUsers().add(uid);
                FirebaseDatabase.getInstance().getReference("Price")
                        .child(key+"/positiveVoteUsers").setValue(price.getPositiveVoteUsers());

                fab_up.setBackgroundColor(DetailPriceActivity.this.getResources().getColor(R.color.quantum_googgreen));
                fab_down.setBackgroundColor(DetailPriceActivity.this.getResources().getColor(R.color.card_neutral));
            }
        });

        fab_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int user_vote_state = 0;
                for(int i=0;i<negative.size();i++)
                {
                    if(uid.equals(negative.get(i))){
                        user_vote_state = -1;
                        break;
                    }
                }

                if(user_vote_state != 0){
                    new MaterialStyledDialog.Builder(DetailPriceActivity.this)
                            .setIcon(R.drawable.ic_testing)
                            .setTitle("Vote")
                            .setDescription("You have already voted for this price update")
                            .setHeaderColor(R.color.colorPrimary)
                            .setPositiveText("Close")
                            .show();
                    return;
                }

                final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                Toast.makeText(DetailPriceActivity.this, "-1", Toast.LENGTH_SHORT).show();
                FirebaseDatabase.getInstance().getReference("Price")
                        .child(key+"/voteCount").setValue(price.getVoteCount()-1);
                price.getPositiveVoteUsers().add(uid);
                FirebaseDatabase.getInstance().getReference("Price")
                        .child(key+"/negativeVoteUsers").setValue(price.getPositiveVoteUsers());

                fab_down.setBackgroundColor(DetailPriceActivity.this.getResources().getColor(R.color.quantum_googred));
                fab_up.setBackgroundColor(DetailPriceActivity.this.getResources().getColor(R.color.card_neutral));

            }
        });

        fab_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DetailPriceActivity.this, CommentsPriceActivity.class);
                i.putExtra("key", key);
                i.putExtra("price", price);
                DetailPriceActivity.this.startActivity(i);
            }
        });

        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

                if (newState == BottomSheetBehavior.STATE_HIDDEN){
                    fab_more.setVisibility(View.VISIBLE);
                }
                else if(newState == BottomSheetBehavior.STATE_COLLAPSED){

                }
                else if(newState == BottomSheetBehavior.STATE_HALF_EXPANDED){

                }
                else {
                    fab_more.setVisibility(View.GONE);

                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });



        iv_back = findViewById(R.id.iv_back);
        tv_name = findViewById(R.id.tv_name);
        et_price = findViewById(R.id.et_price);
        tv_address = findViewById(R.id.tv_address);
        tv_created_by = findViewById(R.id.tv_created_by);
        tv_updated_by = findViewById(R.id.tv_updated_by);
        tv_date = findViewById(R.id.tv_date);
        btn_remove = findViewById(R.id.btn_delete);
        btn_update = findViewById(R.id.btn_update);
        tv_update_by_heading = findViewById(R.id.tv_update_by_heading);

        tv_updated_by.setVisibility(View.GONE);
        tv_update_by_heading.setVisibility(View.GONE);

        if(!admin){
            btn_remove.setVisibility(View.GONE);
        }




        tv_name.setText(price.getName());

        tv_date.setText(price.getLastUpdate());

        databaseReference = FirebaseDatabase.getInstance().getReference("price");
        firebaseAuth = FirebaseAuth.getInstance();

        FirebaseDatabase.getInstance().getReference("User/"+price.getUid()).addListenerForSingleValueEvent(new ValueEventListener()
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

        FirebaseDatabase.getInstance().getReference("User/"+price.getUid_updated()).addValueEventListener(new ValueEventListener()
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

        tv_address.setText(price.getAddress());

        et_price.setText(price.getPrice().toString());

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
                rootRef.child("Price").child(key).removeValue();
                finish();
                //Toast.makeText(getBaseContext(), "Removed", Toast.LENGTH_LONG).show();
            }
        });

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Double.parseDouble(et_price.getText().toString())>=0){
                    updatePrice(Double.parseDouble(et_price.getText().toString()));
                    finish();
                }
                else et_price.setError("Price can't be negative");
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

        LatLng markerLoc=new LatLng(price.getLat(), price.getLng());
        final CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(markerLoc)      // Sets the center of the map to Mountain View
                .zoom(13)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();                   //
        mMap.addMarker(new MarkerOptions().position(markerLoc).title(price.getName()));
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                return true;
            }
        });
    }


    void updatePrice(double i){
        String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference("Price/"+key+"/price").setValue(i);
        //uid_updated
        FirebaseDatabase.getInstance().getReference("Price/"+key+"/uid_updated").setValue(currentUser);
        //
        DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy  HH:mm");
        Date datex = new Date();
        String date = dateFormat.format(datex);

        FirebaseDatabase.getInstance().getReference("Price/"+key+"/lastUpdate").setValue(date);

        FirebaseDatabase.getInstance().getReference("User/"+price.getUid_updated()).addListenerForSingleValueEvent(new ValueEventListener()
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
