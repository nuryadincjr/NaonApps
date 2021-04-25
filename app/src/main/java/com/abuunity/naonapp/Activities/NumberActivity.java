package com.abuunity.naonapp.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.abuunity.naonapp.databinding.ActivityNumberBinding;
import com.google.firebase.auth.FirebaseAuth;

public class NumberActivity extends AppCompatActivity {

    ActivityNumberBinding binding;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNumberBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        auth = FirebaseAuth.getInstance();

        if(auth.getCurrentUser() != null) {
            Intent intent = new Intent(NumberActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        binding.inputPhone.requestFocus();

        binding.btnContinue.setOnClickListener(v -> {
            Intent intent = new Intent(NumberActivity.this, OTPActivity.class);
            intent.putExtra("NUMBER", binding.inputPhone.getText().toString());
            startActivity(intent);
        });

    }
}