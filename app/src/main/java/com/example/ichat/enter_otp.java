package com.example.ichat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class enter_otp extends AppCompatActivity {
    EditText one, two, three, four, five, six;
    Button login;
    ProgressBar progressBar;
    TextView entered_Num, resendOtp, timerOtp;
    FirebaseAuth mAuth;
    String phoneNumber = "";
    String otp = "";

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_otp);
        one = findViewById(R.id.oneOtp);
        two = findViewById(R.id.twoOtp);
        three = findViewById(R.id.threeOtp);
        four = findViewById(R.id.fourOtp);
        five = findViewById(R.id.fiveOtp);
        six = findViewById(R.id.sixOtp);
        login = findViewById(R.id.login);
        entered_Num = findViewById(R.id.entered_num);
        progressBar = findViewById(R.id.progressOtp);
        resendOtp = findViewById(R.id.resendOtp);
        timerOtp = findViewById(R.id.timerOtp);
        mAuth = FirebaseAuth.getInstance();
        toolbar = findViewById(R.id.otpToolbar);
        progressBar = new ProgressBar(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        changeCursorNext(one, two);
        changeCursorNext(two, three);
        changeCursorNext(three, four);
        changeCursorNext(four, five);
        changeCursorNext(five, six);


        sendOtp();
        entered_Num.setText(phoneNumber);

        resendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendOtp();
            }
        });


    }

    private void sendOtp() {
        phoneNumber = getIntent().getStringExtra("number").toString().trim();
        progressBar.setVisibility(View.VISIBLE);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(30L, TimeUnit.SECONDS)
                .setActivity(enter_otp.this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        progressBar.setVisibility(View.INVISIBLE);
                        signInUser(phoneAuthCredential);
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(enter_otp.this, "OTP Sent!", Toast.LENGTH_SHORT).show();
                        timerOtp.setVisibility(View.VISIBLE);
                        timerForResend();
                        String verification = s;
                        login.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                otp = (one.getText().toString()
                                        + two.getText().toString()
                                        + three.getText().toString()
                                        + four.getText().toString()
                                        + five.getText().toString()
                                        + six.getText().toString()).toString();
                                if (s.isEmpty()) {
                                    return;
                                }
                                if (otp.equals("")) {
                                    Toast.makeText(enter_otp.this, "Please Enter Sent OTP!", Toast.LENGTH_SHORT).show();
                                } else if (otp.length() != 6) {
                                    Toast.makeText(enter_otp.this, "Please Enter Valid OTP!", Toast.LENGTH_SHORT).show();
                                } else {
                                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verification, otp);
                                    signInUser(credential);
                                }

                            }
                        });
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(enter_otp.this, "Failed to Sent OTP!", Toast.LENGTH_SHORT).show();
                    }

                }).build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void signInUser(PhoneAuthCredential phoneAuthCredential) {
        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    startActivity(new Intent(enter_otp.this, UserProfile.class));
                    Toast.makeText(enter_otp.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(enter_otp.this, "Please Try Some Time Later", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void changeCursorNext(EditText editText, EditText editText2) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (editText.getText().toString().length() == 1) {
                    editText2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public void timerForResend() {
        new Handler().postDelayed(new Runnable() { // It helps to hold the screen
            @Override
            public void run() {
                resendOtp.setVisibility(View.VISIBLE);
            }
        }, 30000);  // Set Screen Holds Timing

    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}