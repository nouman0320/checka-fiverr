package com.programrabbit.checka;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dmax.dialog.SpotsDialog;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class MainActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener,NavigationView.OnNavigationItemSelectedListener {
    private GoogleMap mMap;

    private PlacesClient placesClient;
    private List<AutocompletePrediction> predictionList;

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation;
    private LocationCallback locationCallback;


    private View mapView;

    private BottomSheetBehavior mBottomSheetBehavior;
    private Button btnMoreOptions;

    private ImageView ivSearch;

    private DrawerLayout mDrawerLayout;

    private ImageView iv_menu;

    private ConstraintLayout cl_service;
    private ConstraintLayout cl_fuel;
    private ConstraintLayout cl_price;

    private EditText et_search;


    private TextView tv_service;
    private TextView tv_fuel;
    private TextView tv_price;

    private AlertDialog progressDialog;

    DatabaseReference priceDatabaseReference;
    DatabaseReference fuelDatabaseReference;
    DatabaseReference serviceDatabaseReference;
    FirebaseAuth firebaseAuth;

    ArrayList<Price> price = new ArrayList<>();
    ArrayList<Fuel> fuel = new ArrayList<>();
    ArrayList<Service> service = new ArrayList<>();

    ArrayList<String> pkeys = new ArrayList<>();
    ArrayList<String> fkeys = new ArrayList<>();
    ArrayList<String> skeys = new ArrayList<>();

    ArrayList<Integer> p_i = new ArrayList<>();
    ArrayList<Integer> s_i = new ArrayList<>();
    ArrayList<Integer> f_i = new ArrayList<>();


    ArrayList<Marker> pSearchMarkers = new ArrayList<>();
    ArrayList<Marker> sSearchMarkers = new ArrayList<>();
    ArrayList<Marker> fSearchMarkers = new ArrayList<>();

    ConstraintLayout cl_admin;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setNavigationViewListner();


        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean admin = prefs.getBoolean("admin", false);
        cl_admin = findViewById(R.id.cl_admin);
        if(!admin)
            cl_admin.setVisibility(View.GONE);

        progressDialog = new SpotsDialog(this, R.style.Custom);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/MYRIADPRO-REGULAR.OTF");


        tv_service = findViewById(R.id.tv_service);
        tv_fuel = findViewById(R.id.tv_fuel);
        tv_price = findViewById(R.id.tv_price);

        tv_service.setTypeface(custom_font);
        tv_fuel.setTypeface(custom_font);
        tv_price.setTypeface(custom_font);

        et_search = findViewById(R.id.et_search);
        et_search.setTypeface(custom_font);
        et_search.setSelected(false);

        cl_service = findViewById(R.id.cl_service);
        cl_fuel = findViewById(R.id.cl_fuel);
        cl_price = findViewById(R.id.cl_price);

        View bottomSheet = findViewById(R.id.bottom_sheet);

        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        btnMoreOptions = findViewById(R.id.btn_options);
        btnMoreOptions.setTypeface(custom_font);

        ivSearch = findViewById(R.id.imageViewSearch);

        mDrawerLayout = findViewById(R.id.drawer_layout);


        btnMoreOptions.setVisibility(View.GONE);


        iv_menu = findViewById(R.id.iv_menu);

        iv_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(Gravity.START);
            }
        });


        cl_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(MainActivity.this, CheckServiceActivity.class);
                MainActivity.this.startActivity(mainIntent);
                //SignupActivity.this.finish();
            }
        });

        cl_fuel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(MainActivity.this, CheckFuelAcitivity.class);
                MainActivity.this.startActivity(mainIntent);
                //SignupActivity.this.finish();
            }
        });


        cl_price.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(MainActivity.this, CheckPriceActivity.class);
                MainActivity.this.startActivity(mainIntent);
                //SignupActivity.this.finish();
            }
        });


        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

                if (newState == BottomSheetBehavior.STATE_HIDDEN){
                    btnMoreOptions.setVisibility(View.VISIBLE);
                    ivSearch.setVisibility(View.VISIBLE);
                }
                else if(newState == BottomSheetBehavior.STATE_COLLAPSED){
                    ivSearch.setVisibility(View.VISIBLE);
                }
                else if(newState == BottomSheetBehavior.STATE_HALF_EXPANDED){
                    ivSearch.setVisibility(View.VISIBLE);
                }
                else {
                    btnMoreOptions.setVisibility(View.GONE);
                    ivSearch.setVisibility(View.GONE);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // React to dragging events
            }
        });

        btnMoreOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });



        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapView = mapFragment.getView();
        /*
        View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
// position on right bottom
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        rlp.setMargins(0, 360, 220, 0);*/


        mapFragment.getMapAsync(this);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);
        Places.initialize(MainActivity.this, "AIzaSyC9d4khZy2rzPtpwIzFvr4J7BzJypHPUK0");
        placesClient = Places.createClient(this);
        final AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();


        et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    queryAndDisplayMarkers(et_search.getText().toString());
                    //Toast.makeText(MainActivity.this, "it's not working as expected.. it will show the markers on map", Toast.LENGTH_LONG).show();
                    return true;
                }
                return false;
            }
        });


        priceDatabaseReference = FirebaseDatabase.getInstance().getReference("Price");
        fuelDatabaseReference = FirebaseDatabase.getInstance().getReference("Fuel");
        serviceDatabaseReference = FirebaseDatabase.getInstance().getReference("Service");
        firebaseAuth = FirebaseAuth.getInstance();


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        /*
        googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        this, R.raw.style_json));*/

        googleMap.getUiSettings().setMapToolbarEnabled(false);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(getBaseContext(),"Location permission is not available",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        googleMap.setMyLocationEnabled(true);
        googleMap.setOnMarkerClickListener(this);
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);


        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        SettingsClient settingsClient = LocationServices.getSettingsClient(getBaseContext());
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());

        task.addOnSuccessListener(MainActivity.this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                getDeviceLocation();
            }
        });

        task.addOnFailureListener(MainActivity.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if(e instanceof ResolvableApiException){
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    try {
                        resolvable.startResolutionForResult(MainActivity.this, 51);
                    } catch (IntentSender.SendIntentException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });


        /*
        mMap.addMarker(new MarkerOptions().position(new LatLng(30.2316056, 71.4917693))
                .title("Test marker"))
                .setIcon(bitmapDescriptorFromVector(this, R.drawable.ic_gps_service));*/



        //Toast.makeText(getBaseContext(),"map is ready",
          //      Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 51){
            if(resultCode == RESULT_OK){
                getDeviceLocation();
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void getDeviceLocation(){
        mFusedLocationProviderClient.getLastLocation()
                .addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if(task.isSuccessful()){
                            mLastKnownLocation = task.getResult();
                            if(mLastKnownLocation != null){
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), 18));
                            } else {
                                final LocationRequest locationRequest = LocationRequest.create();
                                locationRequest.setInterval(10000);
                                locationRequest.setFastestInterval(5000);
                                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                                locationCallback = new LocationCallback(){
                                    @Override
                                    public void onLocationResult(LocationResult locationResult) {
                                        super.onLocationResult(locationResult);
                                        if(locationResult == null)
                                            return;
                                        mLastKnownLocation = locationResult.getLastLocation();
                                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), 18));
                                        mFusedLocationProviderClient.removeLocationUpdates(locationCallback);
                                    }
                                };

                                mFusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);

                            }

                        } else {
                            Toast.makeText(getBaseContext(), "unable to get last location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {

            case R.id.nav_signout: {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("loggedin",false);
                editor.remove("email");
                editor.remove("password");
                editor.apply();

                Intent mainIntent = new Intent(MainActivity.this, LoginActivity.class);
                MainActivity.this.startActivity(mainIntent);
                MainActivity.this.finish();

                break;
            }
        }
        return true;
    }

    private void setNavigationViewListner() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }


    void queryAndDisplayMarkers(final String query){
        progressDialog.show();


        price.clear();
        service.clear();
        fuel.clear();

        pkeys.clear();
        skeys.clear();
        fkeys.clear();

        p_i.clear();
        s_i.clear();
        f_i.clear();

        for(int i=0;i<pSearchMarkers.size();i++)
            pSearchMarkers.get(i).remove();
        pSearchMarkers.clear();

        for(int i=0;i<sSearchMarkers.size();i++)
            sSearchMarkers.get(i).remove();
        sSearchMarkers.clear();

        for(int i=0;i<fSearchMarkers.size();i++)
            fSearchMarkers.get(i).remove();
        fSearchMarkers.clear();


        priceDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("Search", "Firebase got search data");
                ArrayList<Price> s_list = new ArrayList<>();
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    s_list.add(ds.getValue(Price.class));
                    pkeys.add(ds.getKey());
                    if(ds.getValue(Price.class).name == null){
                        priceDatabaseReference.child(ds.getKey()).removeValue();
                    }
                    else{
                        price = s_list;
                    }
                }

                for(int i=0;i<price.size();i++){
                    Price p = price.get(i);
                    if(contains(p.getName(), query) || contains(p.getAddress(), query))
                        p_i.add(i);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Firebase", "onCancelled", databaseError.toException());
            }
        });


        fuelDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Fuel> s_list = new ArrayList<>();
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    s_list.add(ds.getValue(Fuel.class));
                    fkeys.add(ds.getKey());
                    if(ds.getValue(Fuel.class).name == null){
                        fuelDatabaseReference.child(ds.getKey()).removeValue();
                    }
                    else{
                        fuel = s_list;
                    }
                }

                for(int i=0;i<fuel.size();i++){
                    Fuel f = fuel.get(i);
                    if(contains(f.getName(), query) || contains(f.getAddress(), query))
                        f_i.add(i);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Firebase", "onCancelled", databaseError.toException());
            }
        });


        serviceDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Service> s_list = new ArrayList<>();
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    s_list.add(ds.getValue(Service.class));
                    skeys.add(ds.getKey());
                    if(ds.getValue(Service.class).name == null){
                        serviceDatabaseReference.child(ds.getKey()).removeValue();
                    }
                    else{
                        service = s_list;
                    }
                }

                for(int i=0;i<service.size();i++){
                    Service s = service.get(i);
                    if(contains(s.getName(), query) || contains(s.getAddress(), query))
                        s_i.add(i);
                }


                Log.d("Search", p_i.size() +" "+s_i.size() +" "+ f_i.size());

                ArrayList<LatLng> allMarkerPositions = new ArrayList<>();

                for(int i=0;i<p_i.size();i++){
                    LatLng latLng = new LatLng(price.get(p_i.get(i)).lat,price.get(p_i.get(i)).lng);
                    allMarkerPositions.add(latLng);
                    Marker temp = mMap.addMarker(new MarkerOptions()
                            .position(latLng));
                    temp.setIcon(bitmapDescriptorFromVector(MainActivity.this, R.drawable.ic_price_tag));
                    pSearchMarkers.add(temp);

                }


                for(int i=0;i<s_i.size();i++){
                    LatLng latLng = new LatLng(service.get(s_i.get(i)).lat,service.get(s_i.get(i)).lng);
                    allMarkerPositions.add(latLng);
                    Marker temp = mMap.addMarker(new MarkerOptions()
                            .position(latLng));
                    temp.setIcon(bitmapDescriptorFromVector(MainActivity.this, R.drawable.ic_service));
                    sSearchMarkers.add(temp);
                }


                for(int i=0;i<f_i.size();i++){
                    LatLng latLng = new LatLng(fuel.get(f_i.get(i)).lat,fuel.get(f_i.get(i)).lng);
                    allMarkerPositions.add(latLng);
                    Marker temp = mMap.addMarker(new MarkerOptions()
                            .position(latLng));
                    temp.setIcon(bitmapDescriptorFromVector(MainActivity.this, R.drawable.ic_fuel));
                    fSearchMarkers.add(temp);
                }

                LatLng centerPos;

                if(allMarkerPositions.size()>0){
                    centerPos = computeCentroid(allMarkerPositions);
                }
                else{
                    centerPos = new LatLng(mMap.getCameraPosition().target.latitude, mMap.getCameraPosition().target.longitude);
                }


                progressDialog.dismiss();

                CameraUpdate center=CameraUpdateFactory.newLatLng(centerPos);
                CameraUpdate zoom=CameraUpdateFactory.zoomTo(12);
                mMap.moveCamera(center);
                mMap.animateCamera(zoom);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Firebase", "onCancelled", databaseError.toException());
            }
        });



    }

    public boolean contains( String haystack, String needle ) {
        haystack = haystack == null ? "" : haystack;
        needle = needle == null ? "" : needle;

        // Works, but is not the best.
        //return haystack.toLowerCase().indexOf( needle.toLowerCase() ) > -1

        return haystack.toLowerCase().contains( needle.toLowerCase() );
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {


        //Toast.makeText(getBaseContext(), "onMarkerClicked",Toast.LENGTH_SHORT).show();

        for(int i=0;i<pSearchMarkers.size();i++){
            if(marker.equals(pSearchMarkers.get(i))){
                Intent t = new Intent(MainActivity.this, DetailPriceActivity.class);
                t.putExtra("key", pkeys.get(p_i.get(i)));
                t.putExtra("price", price.get(p_i.get(i)));
                Log.d("Marker", pkeys.get(i));
                MainActivity.this.startActivity(t);
                return true;
            }
        }


        for(int i=0;i<sSearchMarkers.size();i++){
            if(marker.equals(sSearchMarkers.get(i))){
                Intent t = new Intent(MainActivity.this, DetailServiceActivity.class);
                t.putExtra("key", skeys.get(s_i.get(i)));
                t.putExtra("service", service.get(s_i.get(i)));
                Log.d("Marker", skeys.get(i));
                MainActivity.this.startActivity(t);
                return true;
            }
        }


        for(int i=0;i<fSearchMarkers.size();i++){
            if(marker.equals(fSearchMarkers.get(i))){
                Intent t = new Intent(MainActivity.this, DetailFuelActivity.class);
                t.putExtra("key", fkeys.get(f_i.get(i)));
                t.putExtra("fuel", fuel.get(f_i.get(i)));
                Log.d("Marker", fkeys.get(i));
                MainActivity.this.startActivity(t);
                return true;
            }
        }
        return false;
    }


    private LatLng computeCentroid(List<LatLng> points) {
        double latitude = 0;
        double longitude = 0;
        int n = points.size();

        for (LatLng point : points) {
            latitude += point.latitude;
            longitude += point.longitude;
        }

        return new LatLng(latitude/n, longitude/n);
    }
}
