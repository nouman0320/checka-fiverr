package com.programrabbit.checka;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SplashActivity extends AppCompatActivity {
    private final int SPLASH_DISPLAY_LENGTH = 400;

    private Context mContext= SplashActivity.this;
    private static final int REQUEST = 112;

    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();

        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {



                if (Build.VERSION.SDK_INT >= 23)
                {
                    String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET};
                    if (!hasPermissions(mContext, PERMISSIONS))
                    {
                        ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS, REQUEST );
                    }
                    else
                    {
                        //do here
                        loadMainApp();
                    }
                } else {
                    //do here
                    loadMainApp();
                }




            }
        }, SPLASH_DISPLAY_LENGTH);


        databaseReference = FirebaseDatabase.getInstance().getReference("User");
        firebaseAuth = FirebaseAuth.getInstance();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //do here
                    loadMainApp();
                } else {
                    Toast.makeText(mContext, "The app requires permission for proper functionality", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }
    }

    private static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public void loadMainApp(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(SplashActivity.this);
        Boolean loggedIn = prefs.getBoolean("loggedin", false);

        if(!loggedIn){
            Intent mainIntent = new Intent(SplashActivity.this, LoginActivity.class);
            SplashActivity.this.startActivity(mainIntent);
            SplashActivity.this.finish();
        }
        else
        {


            final String email = prefs.getString("email", "null@null.com");
            final String password = prefs.getString("password", "123456");

            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(SplashActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                //Toast.makeText(getApplicationContext(), "logged in", Toast.LENGTH_LONG).show();

                                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(SplashActivity.this);
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putBoolean("loggedin", true);
                                editor.putString("email", email);
                                editor.putString("password", password);
                                editor.apply();

                                Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                                SplashActivity.this.startActivity(mainIntent);
                                SplashActivity.this.finish();

                            } else {
                                Toast.makeText(getApplicationContext(), task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();

                                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(SplashActivity.this);
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putBoolean("loggedin", false);
                                editor.remove("email");
                                editor.remove("password");
                                editor.apply();

                                Intent mainIntent = new Intent(SplashActivity.this, LoginActivity.class);
                                SplashActivity.this.startActivity(mainIntent);
                                SplashActivity.this.finish();
                            }
                        }
                    });
        }

    }
}
