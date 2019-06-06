package com.programrabbit.checka;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dmax.dialog.SpotsDialog;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class LoginActivity extends AppCompatActivity {

    TextView tv_register;
    EditText et_email;
    EditText et_password;
    Button btn_login;


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
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        progressDialog = new SpotsDialog(this, R.style.Custom);


        tv_register = findViewById(R.id.link_signup);
        et_email = findViewById(R.id.input_email);
        et_password = findViewById(R.id.input_password);
        btn_login = findViewById(R.id.btn_login);


        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int vCount = 0;

                if(TextUtils.isEmpty(et_email.getText().toString())){
                    et_email.setError("Email is required");
                    vCount++;
                }

                if(TextUtils.isEmpty(et_password.getText().toString())){
                    et_password.setError("Password is required");
                    vCount++;
                }

                String pass = et_password.getText().toString();

                if(pass.length() < 6 && !TextUtils.isEmpty(pass) || pass.length() > 25) {
                    et_password.setError("Length of password must be between 6-25");
                    vCount++;
                }

                if(!isEmailValid(et_email.getText().toString())) {
                    et_email.setError("Email is not valid");
                    vCount++;
                }

                if(vCount>0)
                    return;

                progressDialog.show();

                firebaseAuth.signInWithEmailAndPassword(et_email.getText().toString(), et_password.getText().toString())
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(getApplicationContext(), "logged in", Toast.LENGTH_LONG).show();

                                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.putBoolean("loggedin",true);
                                    editor.putString("email", et_email.getText().toString());
                                    editor.putString("password", et_password.getText().toString());
                                    editor.apply();

                                    Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                                    LoginActivity.this.startActivity(mainIntent);
                                    LoginActivity.this.finish();

                                } else {
                                    Toast.makeText(getApplicationContext(), task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                }
                                progressDialog.dismiss();
                            }
                        });
            }
        });

        tv_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(LoginActivity.this, SignupActivity.class);
                LoginActivity.this.startActivity(mainIntent);
                LoginActivity.this.finish();
            }
        });





        databaseReference = FirebaseDatabase.getInstance().getReference("User");
        firebaseAuth = FirebaseAuth.getInstance();

    }

    public boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
