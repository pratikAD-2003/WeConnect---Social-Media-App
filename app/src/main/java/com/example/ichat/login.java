package com.example.ichat;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.hbb20.CountryCodePicker;
public class login extends AppCompatActivity {
    CountryCodePicker picker;
    EditText number;
    Button verify;
    String phoneNumber = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        picker = findViewById(R.id.ccp);
        number = findViewById(R.id.numberPhone);
        verify = findViewById(R.id.verify);

        picker.registerCarrierNumberEditText(number);
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phoneNumber = number.getText().toString().trim();
                if (phoneNumber.equals("")) {
                    number.setError("Please Enter Number.");
                } else if (phoneNumber.length() != 10) {
                    number.setError("Please Enter Valid Number");
                } else {
                    startActivity(new Intent(login.this, enter_otp.class).putExtra("number", picker.getFullNumberWithPlus()));
                    finish();
                }
            }
        });
    }


}