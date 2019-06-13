package com.programrabbit.checka;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import dmax.dialog.SpotsDialog;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class CheckRateActivity extends AppCompatActivity {

    TextView tv_rtgs;
    TextView tv_bond;

    EditText et_rtgs;
    EditText et_bond;

    Button btn_rtgs;
    Button btn_bond;

    ImageView iv_back;

    Rate myRateData;

    TextView tv_date;

    private AlertDialog progressDialog;

    ConstraintLayout cl_admin;


    DatabaseReference databaseRateReference;
    FirebaseAuth firebaseAuth;


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_rate);
        getSupportActionBar().hide();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean admin = prefs.getBoolean("admin", false);
        cl_admin = findViewById(R.id.cl_admin);


        iv_back = findViewById(R.id.iv_back);

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        progressDialog = new SpotsDialog(this, R.style.Custom);

        progressDialog.show();


        tv_rtgs = findViewById(R.id.tv_rtgs);
        tv_bond = findViewById(R.id.tv_bond);
        tv_date = findViewById(R.id.tv_update);


        et_rtgs = findViewById(R.id.et_rtgs);
        et_bond = findViewById(R.id.et_bond);

        btn_rtgs = findViewById(R.id.btn_rtgs);
        btn_bond = findViewById(R.id.btn_bond);

        if(!admin){
            cl_admin.setVisibility(View.GONE);
            et_rtgs.setVisibility(View.GONE);
            et_bond.setVisibility(View.GONE);
            btn_rtgs.setVisibility(View.GONE);
            btn_bond.setVisibility(View.GONE);
        }


        databaseRateReference = FirebaseDatabase.getInstance().getReference("rate");
        firebaseAuth = FirebaseAuth.getInstance();


        btn_rtgs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(et_rtgs.getText().toString())){
                    et_rtgs.setError("Can't be empty");
                    return;
                }
                if(myRateData!=null){
                    myRateData.setRtgs(et_rtgs.getText().toString());

                    DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy  HH:mm");
                    Date datex = new Date();
                    String date = dateFormat.format(datex);

                    myRateData.setDate(date);

                    databaseRateReference.setValue(myRateData);

                    Toast.makeText(getBaseContext(), "Updated", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_bond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(et_bond.getText().toString())){
                    et_rtgs.setError("Can't be empty");
                    return;
                }
                if(myRateData!=null){
                    myRateData.setBond(et_bond.getText().toString());

                    DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy  HH:mm");
                    Date datex = new Date();
                    String date = dateFormat.format(datex);

                    myRateData.setDate(date);

                    databaseRateReference.setValue(myRateData);

                    Toast.makeText(getBaseContext(), "Updated", Toast.LENGTH_SHORT).show();

                }
            }
        });



        databaseRateReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                myRateData = dataSnapshot.getValue(Rate.class);

                if(myRateData == null)
                {
                    tv_rtgs.setText("1 USD = N/A RTGS");
                    tv_bond.setText("1 USD = N/A BOND");
                    tv_date.setText("Last Updated: N/A");

                    myRateData = new Rate("N/A", "N/A", "N/A");
                    progressDialog.dismiss();
                    return;
                }

                tv_rtgs.setText("1 USD = "+myRateData.rtgs+" RTGS");
                tv_bond.setText("1 USD = "+myRateData.bond+" BOND");
                tv_date.setText("Last Updated: "+myRateData.date);
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
