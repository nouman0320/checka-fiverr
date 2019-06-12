package com.programrabbit.checka;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
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

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class CommentsFuelActivity extends AppCompatActivity {

    Fuel fuel;
    String key;

    EditText et_report;
    Button btn_submit;

    TextView tv_report;

    ImageView iv_back;

    boolean isAdmin;

    DatabaseReference fuelDatabaseReference;
    FirebaseAuth firebaseAuth;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments_fuel);
        getSupportActionBar().hide();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        isAdmin = prefs.getBoolean("admin", false);


        fuel = (Fuel) getIntent().getSerializableExtra("fuel");
        key = getIntent().getStringExtra("key");


        fuelDatabaseReference = FirebaseDatabase.getInstance().getReference("Fuel");

        et_report = findViewById(R.id.et_report);
        btn_submit = findViewById(R.id.btn_submit);

        tv_report = findViewById(R.id.tv_report);

        iv_back = findViewById(R.id.iv_back);

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if(isAdmin){
            et_report.setVisibility(View.GONE);
            btn_submit.setVisibility(View.GONE);
            tv_report.setVisibility(View.VISIBLE);
            tv_report.setText("> Reported to admin:\n");

            fuelDatabaseReference.child(key+"/comments").addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        tv_report.setText(tv_report.getText().toString() + "+ " + ds.getValue(String.class) + "\n");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else {
            et_report.setVisibility(View.VISIBLE);
            btn_submit.setVisibility(View.VISIBLE);
            tv_report.setVisibility(View.GONE);
        }



        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(et_report.getText().toString()))
                {
                    et_report.setError("Report can not be empty");
                    return;
                }

                ArrayList<String> temp = new ArrayList<>();
                temp.addAll(fuel.getComments());
                temp.add(et_report.getText().toString());
                FirebaseDatabase.getInstance().getReference("Fuel")
                        .child(key+"/comments").setValue(temp);

                Toast.makeText(getBaseContext(), "Thank you for feedback", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

    }
}
