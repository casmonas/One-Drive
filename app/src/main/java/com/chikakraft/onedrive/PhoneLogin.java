package com.chikakraft.onedrive;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.chaos.view.PinView;
import com.chikakraft.onedrive.responses.ApiClient;
import com.chikakraft.onedrive.responses.ApiInterface;
import com.chikakraft.onedrive.responses.Users;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.Credentials;
import com.google.android.gms.auth.api.credentials.CredentialsApi;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.auth.api.credentials.IdentityProviders;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PhoneLogin extends AppCompatActivity {

    private CountryCodePicker ccp;
    private EditText phoneEditText;
    private String selectedCountryCode = "+234";
    private PinView pinView;
    private ConstraintLayout phoneLayout;
    private ProgressBar progressBar;

    private static final int CREDENTIAL_PICKER_REQUEST =120 ;

    ////firebase phone authentication////
    private String mVerification;
    private PhoneAuthProvider.ForceResendingToken mResendingToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks changedCallbacks;
    private FirebaseAuth mAuth;
    ////firebase phone authentication////

    ApiInterface apiInterface;

    private  String fcm_token;

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);
        ////////fcm tokenn///////////

        fcm_token = FirebaseInstanceId.getInstance().getToken();


        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if(!task.isSuccessful()){
                            return;
                        }

                        fcm_token = task.getResult().getToken();
                    }
                });

        ////////fcm tokenn///////////

        sessionManager = new SessionManager(this);

        ccp = findViewById(R.id.countryCodePicker);
        phoneEditText = findViewById(R.id.editTextTextPersonName);
        pinView = findViewById(R.id.firstPinView);
        phoneLayout = findViewById(R.id.phone_layout);
        progressBar = findViewById(R.id.progressBar2);
        mAuth = FirebaseAuth.getInstance();
        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);

        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                selectedCountryCode = ccp.getSelectedCountryCodeWithPlus();

            }
        });

        phoneEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().length()==10){
                    //Toast.makeText(PhoneLogin.this,"10 digit pin",Toast.LENGTH_SHORT).show();
                    /*phoneLayout.setVisibility(View.GONE);
                    pinView.setVisibility(View.VISIBLE);*/

                    sendOTP();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        pinView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().length()==6){
//                    Toast.makeText(PhoneLogin.this,"5 digit pin",Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.VISIBLE);
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerification,pinView.getText().toString().trim());
                    signInWithAuthCredential(credential);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        HintRequest hintRequest = new HintRequest.Builder()
                .setPhoneNumberIdentifierSupported(true)
//                .setEmailAddressIdentifierSupported(true)
//                .setAccountTypes(IdentityProviders.GOOGLE)
                .build();


        PendingIntent intent = Credentials.getClient(PhoneLogin.this).getHintPickerIntent(hintRequest);
        try
        {
            startIntentSenderForResult(intent.getIntentSender(), CREDENTIAL_PICKER_REQUEST, null, 0, 0, 0,new Bundle());
        }
        catch (IntentSender.SendIntentException e)
        {
            e.printStackTrace();
        }

        //// otp callbacks////
        changedCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                String code = phoneAuthCredential.getSmsCode();
                if(code!=null){
                    pinView.setText(code);
                    signInWithAuthCredential(phoneAuthCredential);
                }
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {

                Toast.makeText(PhoneLogin.this,"Something went wrong.",Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                phoneLayout.setVisibility(View.VISIBLE);
                pinView.setVisibility(View.GONE);
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);

                mVerification = s;
                mResendingToken = forceResendingToken;

                Toast.makeText(PhoneLogin.this,"6 digit OTP sent",Toast.LENGTH_SHORT).show();

                progressBar.setVisibility(View.GONE);
                phoneLayout.setVisibility(View.GONE);
                pinView.setVisibility(View.VISIBLE);
            }
        };


    }

    private void sendOTP() {

        progressBar.setVisibility(View.VISIBLE);
        String phoneNumber = selectedCountryCode+phoneEditText.getText().toString();

        PhoneAuthOptions phoneAuthOptions = PhoneAuthOptions.newBuilder(mAuth)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setPhoneNumber(phoneNumber)
                .setActivity(this)
                .setCallbacks(changedCallbacks)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(phoneAuthOptions);




    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CREDENTIAL_PICKER_REQUEST && resultCode == RESULT_OK)
        {
            // Obtain the phone number from the result
            Credential credentials = data.getParcelableExtra(Credential.EXTRA_KEY);
            /* EditText.setText(credentials.getId().substring(3));*/ //get the selected phone number
//Do what ever you want to do with your selected phone number here

            ///substring has to be 4 instead of 3 because of +234///
            Toast.makeText(this, "MOB"+credentials.getId().substring(4), Toast.LENGTH_SHORT).show();
            phoneEditText.setText(credentials.getId().substring(4));


        }
        else if (requestCode == CREDENTIAL_PICKER_REQUEST && resultCode == CredentialsApi.ACTIVITY_RESULT_NO_HINTS_AVAILABLE)
        {
            // *** No phone numbers available ***
            Toast.makeText(PhoneLogin.this, "No phone numbers found", Toast.LENGTH_LONG).show();
        }


    }

    private void signInWithAuthCredential(PhoneAuthCredential phoneAuthCredential) {
        mAuth.signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            Call<Users> call = apiInterface.login_register(phoneEditText.getText().toString(),"44771987134198744771987",fcm_token);
                            call.enqueue(new Callback<Users>() {
                                @Override
                                public void onResponse(Call<Users> call, Response<Users> response) {
                                    if (response.isSuccessful()){
                                        String status = response.body().getResponse();
                                        if(status.equals("already")){

                                            sessionManager.CreateSession(fcm_token,"user_type",phoneEditText.getText().toString(),selectedCountryCode);

                                            Toast.makeText(PhoneLogin.this, "Logged in successfully", Toast.LENGTH_LONG).show();
                                            Intent intent = new Intent(PhoneLogin.this,Home.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                            finish();

                                        }else    if(status.equals("new")){

                                            sessionManager.CreateSession(fcm_token,"user_type",phoneEditText.getText().toString(),selectedCountryCode);
                                            Toast.makeText(PhoneLogin.this, "Logged in successfully", Toast.LENGTH_LONG).show();
                                            Intent intent = new Intent(PhoneLogin.this,EditUserProfile.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                            finish();

                                        } else    if(status.equals("failed")){

                                            Toast.makeText(PhoneLogin.this, "Login Failed", Toast.LENGTH_LONG).show();
                                            Intent intent = new Intent(PhoneLogin.this,Login.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                            finish();
                                            Animatoo.animateSlideRight(PhoneLogin.this);
                                        }
                                        else {

                                            Toast.makeText(PhoneLogin.this, "Login Failed", Toast.LENGTH_LONG).show();
                                            Intent intent = new Intent(PhoneLogin.this,Login.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                            finish();
                                            Animatoo.animateSlideRight(PhoneLogin.this);
                                        }
                                    }else{
                                        Toast.makeText(PhoneLogin.this, "Something went wrong. Please try again.", Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<Users> call, Throwable t) {
                                    Toast.makeText(PhoneLogin.this, "Login Failed", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(PhoneLogin.this,Login.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                    Animatoo.animateSlideRight(PhoneLogin.this);
                                }
                            });

                        }else{
                            Toast.makeText(PhoneLogin.this, "Login Failed", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(PhoneLogin.this,Login.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                            Animatoo.animateSlideRight(PhoneLogin.this);
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(PhoneLogin.this,Login.class);
        startActivity(intent);
        finish();
        Animatoo.animateSlideLeft(PhoneLogin.this);

    }
}