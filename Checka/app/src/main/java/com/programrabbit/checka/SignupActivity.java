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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dmax.dialog.SpotsDialog;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SignupActivity extends AppCompatActivity {

    TextView tv_login;
    EditText et_name, et_address, et_email, et_number, et_password, et_re_password;
    Button btn_signup;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

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
        setContentView(R.layout.activity_signup);
        getSupportActionBar().hide();

        progressDialog = new SpotsDialog(this, R.style.Custom);

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
                //Intent mainIntent = new Intent(SignupActivity.this, MainActivity.class);
                //SignupActivity.this.startActivity(mainIntent);
                //SignupActivity.this.finish();

                final User user = new User(et_name.getText().toString()
                        , et_address.getText().toString()
                        , et_email.getText().toString()
                        , et_number.getText().toString());


                int vCount = 0;

                if(TextUtils.isEmpty(user.getName())){
                    et_name.setError("Name is required");
                    vCount++;
                }
                if(TextUtils.isEmpty(user.getAddress())){
                    et_address.setError("Address is required");
                    vCount++;
                }
                if(TextUtils.isEmpty(user.getEmail())){
                    et_email.setError("Email is required");
                    vCount++;
                }
                if(TextUtils.isEmpty(user.getMobile_number())){
                    et_number.setError("Number is required");
                    vCount++;
                }



                if(TextUtils.isEmpty(et_password.getText().toString())){
                    et_password.setError("Password is required");
                    vCount++;
                }
                if(TextUtils.isEmpty(et_re_password.getText().toString())){
                    et_re_password.setError("Password is required");
                    vCount++;
                }

                final String pass = et_password.getText().toString();

                if(!et_re_password.getText().toString().equals(pass)){
                    et_re_password.setError("Password doesn't match");
                    vCount++;
                }

                if(pass.length() < 6 && !TextUtils.isEmpty(pass) || pass.length() > 25) {
                    et_password.setError("Length of password must be between 6-25");
                    vCount++;
                }

                if(!isEmailValid(user.getEmail())) {
                    et_email.setError("Email is not valid");
                    vCount++;
                }

                if(vCount>0)
                    return;


                progressDialog.show();


                mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential credential) {
                        // This callback will be invoked in two situations:
                        // 1 - Instant verification. In some cases the phone number can be instantly
                        //     verified without needing to send or enter a verification code.
                        // 2 - Auto-retrieval. On some devices Google Play services can automatically
                        //     detect the incoming verification SMS and perform verification without
                        //     user action.

                        proceed_registration(user, pass);
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        // This callback is invoked in an invalid request for verification is made,
                        // for instance if the the phone number format is not valid.
                        Log.w("code", "onVerificationFailed", e);

                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            // Invalid request
                            Toast.makeText(getBaseContext(), "Phone number format: +92[country-code][number]", Toast.LENGTH_LONG).show();
                            // ...
                        } else if (e instanceof FirebaseTooManyRequestsException) {
                            // The SMS quota for the project has been exceeded
                            Toast.makeText(getBaseContext(), "SMS Limit reached.", Toast.LENGTH_SHORT).show();
                            // ...
                        }
                        progressDialog.dismiss();
                        // Show a message and update the UI
                        // ...
                    }

                    @Override
                    public void onCodeSent(String verificationId,
                                           PhoneAuthProvider.ForceResendingToken token) {
                        // The SMS verification code has been sent to the provided phone number, we
                        // now need to ask the user to enter the code and then construct a credential
                        // by combining the code with a verification ID.
                        Log.d("code", "onCodeSent:" + verificationId);

                        // Save verification ID and resending token so we can use them later
                        //mVerificationId = verificationId;
                        //mResendToken = token;

                        // ...
                    }
                };


                startPhoneNumberVerification(et_number.getText().toString());     // OnVerificationStateChangedCallbacks




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

    private void startPhoneNumberVerification(String phoneNumber) {
        // [START start_phone_auth]
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
        // [END start_phone_auth]
    }

    void proceed_registration(final User user, final String pass){
        firebaseAuth.createUserWithEmailAndPassword(user.getEmail(), pass)
                .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseDatabase.getInstance().getReference("User")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                // registration complete
                                                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(SignupActivity.this);
                                                SharedPreferences.Editor editor = prefs.edit();
                                                editor.putBoolean("loggedin",true);
                                                editor.putString("email", user.getEmail());
                                                editor.putString("password", pass);
                                                editor.apply();


                                                Intent mainIntent = new Intent(SignupActivity.this, MainActivity.class);
                                                SignupActivity.this.startActivity(mainIntent);
                                                SignupActivity.this.finish();
                                            }
                                            else {
                                                Toast.makeText(getApplicationContext(), "Something went wrong while creating account", Toast.LENGTH_SHORT).show();
                                            }
                                            progressDialog.dismiss();
                                        }
                                    });
                        } else {
                            Toast.makeText(getApplicationContext(), "Unable to create account", Toast.LENGTH_SHORT).show();
                            Log.d("register", task.getException().toString());
                            progressDialog.dismiss();
                        }
                    }
                });
    }

    private static boolean isValidPhoneNumber(String phoneNumber) {
        return !TextUtils.isEmpty(phoneNumber) && android.util.Patterns.PHONE.matcher(phoneNumber).matches();
    }
}
