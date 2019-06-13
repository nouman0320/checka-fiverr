package com.programrabbit.checka;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import dmax.dialog.SpotsDialog;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class PhoneVerificationActivity extends AppCompatActivity {

    Button btn_verify;


    EditText et_phone;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private AlertDialog progressDialog;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verification);
        getSupportActionBar().hide();

        btn_verify = findViewById(R.id.btn_verify);

        et_phone = findViewById(R.id.input_mobile);

        progressDialog = new SpotsDialog(this, R.style.Custom);



        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.

                //proceed_registration(user, pass);

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(PhoneVerificationActivity.this);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("number", et_phone.getText().toString());
                editor.apply();


                Intent mainIntent = new Intent(PhoneVerificationActivity.this, SignupActivity.class);
                PhoneVerificationActivity.this.startActivity(mainIntent);
                PhoneVerificationActivity.this.finish();

                progressDialog.dismiss();
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


        btn_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                int vSize = 0;

                if(TextUtils.isEmpty(et_phone.getText().toString())){
                    et_phone.setError("Phone number is required");
                    vSize++;
                }

                if(vSize > 0)
                    return;

                startPhoneNumberVerification(et_phone.getText().toString());     // OnVerificationStateChangedCallbacks


                progressDialog.show();
            }
        });
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
}
