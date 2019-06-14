package com.programrabbit.checka;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.maps.android.SphericalUtil;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import dmax.dialog.SpotsDialog;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class NewServiceActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private View mapView;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation;
    private LocationCallback locationCallback;

    ImageView iv_back;
    FloatingActionButton fab;

    AutocompleteSupportFragment autocompleteFragment;

    String address = "";

    TextView tv_address;

    EditText et_location_name;//, et_address;

    String apiKey = "AIzaSyDzx-vWPnVEyrYv93hjxmL4S7e1pUy8-JU";

    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;

    private AlertDialog progressDialog;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public LatLngBounds toBounds(LatLng center, double radiusInMeters) {
        double distanceFromCenterToCorner = radiusInMeters * Math.sqrt(2.0);
        LatLng southwestCorner =
                SphericalUtil.computeOffset(center, distanceFromCenterToCorner, 225.0);
        LatLng northeastCorner =
                SphericalUtil.computeOffset(center, distanceFromCenterToCorner, 45.0);
        return new LatLngBounds(southwestCorner, northeastCorner);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_service);

        Places.initialize(getApplicationContext(), apiKey);
        PlacesClient placesClient = Places.createClient(this);


        progressDialog = new SpotsDialog(this, R.style.Custom);
        tv_address = findViewById(R.id.tv_address);

        final int[] problemLevel = new int[1];
        final int[] serviceType = new int[1];

        et_location_name = findViewById(R.id.et_location_name);
        //et_address = findViewById(R.id.et_address);

        iv_back = findViewById(R.id.iv_back);
        fab= findViewById(R.id.fab);

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        Spinner serviceSpinner = (Spinner) findViewById(R.id.spinnerCategory);

        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(NewServiceActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.services));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        serviceSpinner.setAdapter(myAdapter);




        final Spinner problemSpinner = (Spinner) findViewById(R.id.spinnerProblem);

        final ArrayAdapter<String> myAdapter2 = new ArrayAdapter<String>(NewServiceActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.problem_level));

        final ArrayAdapter<String> myAdapter3 = new ArrayAdapter<String>(NewServiceActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.problem_level_electricity));

        myAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        problemSpinner.setAdapter(myAdapter3);

        problemSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                problemLevel[0] = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        serviceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                serviceType[0] = i;
                if(i==1 || i==2){
                    problemSpinner.setAdapter(myAdapter2);

                }
                else problemSpinner.setAdapter(myAdapter3);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        getSupportActionBar().hide();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapView = mapFragment.getView();
        View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
// position on right bottom
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        rlp.setMargins(0, 360, 220, 0);


        mapFragment.getMapAsync(this);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(NewServiceActivity.this);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = et_location_name.getText().toString();
                //String address = et_address.getText().toString();


                int vCount = 0;

                if(TextUtils.isEmpty(name)){
                    et_location_name.setError("Location name can not be empty");
                    vCount++;
                }

                if(TextUtils.isEmpty(address)){
                    Toast.makeText(getBaseContext(), "Address can not be empty", Toast.LENGTH_SHORT).show();
                    vCount++;
                }

                if(vCount>0)
                    return;

                progressDialog.show();

                int t_problemLevel = problemLevel[0];
                int t_serviceType = serviceType[0];

                if(t_serviceType==0 && t_problemLevel==1)
                    t_problemLevel=2;

                LatLng latLng = mMap.getCameraPosition().target;

                DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy  HH:mm");
                Date datex = new Date();
                String date = dateFormat.format(datex);

                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                String child_uid = databaseReference.push().getKey();

                Service service = new Service(name, address, t_problemLevel, t_serviceType,latLng,date,uid,0);
                FirebaseDatabase.getInstance().getReference("Service")
                        .child(child_uid)
                        .setValue(service)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Updated", Toast.LENGTH_SHORT).show();

                                    NotificationCompat.Builder mBuilder =   new NotificationCompat.Builder(NewServiceActivity.this)
                                            .setSmallIcon(R.drawable.ic_service) // notification icon
                                            .setContentTitle("Service Update") // title for notification
                                            .setContentText("New service update has been added! check out the app") // message for notification
                                            .setAutoCancel(true); // clear notification after click
                                    Intent intent = new Intent(NewServiceActivity.this, MainActivity.class);
                                    @SuppressLint("WrongConstant") PendingIntent pi = PendingIntent.getActivity(NewServiceActivity.this,0,intent, Intent.FLAG_ACTIVITY_NEW_TASK);
                                    mBuilder.setContentIntent(pi);
                                    NotificationManager mNotificationManager =
                                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                    mNotificationManager.notify(0, mBuilder.build());

                                    finish();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Unable to add the service right now", Toast.LENGTH_SHORT).show();
                                }
                                progressDialog.dismiss();
                            }
                        });

            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference("Service");
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        this, R.raw.style_night_json));

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
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);


        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        SettingsClient settingsClient = LocationServices.getSettingsClient(getBaseContext());
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());

        task.addOnSuccessListener(NewServiceActivity.this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                getDeviceLocation();
            }
        });

        task.addOnFailureListener(NewServiceActivity.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if(e instanceof ResolvableApiException){
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    try {
                        resolvable.startResolutionForResult(NewServiceActivity.this, 51);
                    } catch (IntentSender.SendIntentException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });


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


                                address = getAddress(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()));
                                tv_address.setText(address);

                                mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                                    @Override
                                    public void onCameraIdle() {
                                        // Cleaning all the markers.
                                        if (mMap != null) {

                                            address = getAddress(new LatLng(mMap.getCameraPosition().target.latitude, mMap.getCameraPosition().target.longitude));
                                            tv_address.setText(address);
                                        }

                                    }
                                });

                                // Initialize the AutocompleteSupportFragment.
                                autocompleteFragment = (AutocompleteSupportFragment)
                                        getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

// Specify the types of place data to return.
                                autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS));

// Set up a PlaceSelectionListener to handle the response.


                                LatLng center = new LatLng(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude());

                                Log.d("Place", center.toString());

                                LatLng east = SphericalUtil.computeOffset(center, 15000, 90); // Shift 500 meters to the east
                                LatLng west = SphericalUtil.computeOffset(center, 15000, 270); // Shift 500 meters to the west


                                autocompleteFragment.setLocationRestriction(RectangularBounds.newInstance(west, east));
                                //autocompleteFragment.setTypeFilter(TypeFilter.ADDRESS);
                                autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                                                                                    @Override
                                                                                    public void onPlaceSelected(@NonNull Place place) {
                                                                                        // TODO: Get info about the selected place.
                                                                                        Log.i("Place", "Address: " + place.getAddress() + ", " + place.getId());
                                                                                        address=place.getAddress();
                                                                                        tv_address.setText(address);
                                                                                    }

                                                                                    @Override
                                                                                    public void onError(@NonNull Status status) {
                                                                                        // TODO: Handle the error.
                                                                                        Log.i("Place", "An error occurred: " + status);
                                                                                    }
                                                                                }
                                );

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

    String getAddress(LatLng latLng){
        Geocoder gc = new Geocoder(this);

        if(gc.isPresent()){
            List<Address> list = null;
            try {
                list = gc.getFromLocation(latLng.latitude, latLng.longitude,1);
                Address address = list.get(0);

                /*
                StringBuffer str = new StringBuffer();
                str.append("Name: " + address.getLocality() + "\n");
                str.append("Sub-Admin Ares: " + address.getSubAdminArea() + "\n");
                str.append("Admin Area: " + address.getAdminArea() + "\n");
                str.append("Country: " + address.getCountryName() + "\n");
                str.append("Country Code: " + address.getCountryCode() + "\n");*/

                String strAddress = address.getAddressLine(0);
                return strAddress;

            } catch (IOException e) {
                e.printStackTrace();
                return "Unable to find the address";
            }
        }
        return "";
    }
}
