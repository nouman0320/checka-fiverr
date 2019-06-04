package com.programrabbit.checka;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SignupActivity extends AppCompatActivity {

    TextView tv_login;
    EditText et_name, et_address, et_email, et_number, et_password, et_re_password;
    Button btn_signup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getSupportActionBar().hide();


        tv_login = findViewById(R.id.link_login);
        et_name = findViewById(R.id.input_name);
        et_address = findViewById(R.id.input_address);
        et_email = findViewById(R.id.input_email);
        et_number = findViewById(R.id.input_mobile);
        et_password = findViewById(R.id.input_password);
        et_re_password = findViewById(R.id.input_reEnterPassword);
        btn_signup = findViewById(R.id.btn_signup);


        tv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(SignupActivity.this, LoginActivity.class);
                SignupActivity.this.startActivity(mainIntent);
                SignupActivity.this.finish();
            }
        });


        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(SignupActivity.this, MainActivity.class);
                SignupActivity.this.startActivity(mainIntent);
                SignupActivity.this.finish();
            }
        });


    }
}
