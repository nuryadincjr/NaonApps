package com.abuunity.naonapp.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.abuunity.naonapp.Models.User;
import com.abuunity.naonapp.databinding.ActivitySetupProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class SetupProfileActivity extends AppCompatActivity {

    ActivitySetupProfileBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    Uri selectedImg;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySetupProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        dialog = new ProgressDialog(this);
        dialog.setMessage("Updating Profil");
        dialog.setCancelable(false);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();

        binding.imgProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 45);

            }
        });

        binding.btnContinue.setOnClickListener(v -> {
            String name = binding.inputName.getText().toString();

            if(name.isEmpty()) {
                binding.inputName.setError("Please type a name");
                return;
            }

            dialog.show();
            if(selectedImg != null) {
                StorageReference reference = storage.getReference().child("Profiles").child(auth.getUid());
                reference.putFile(selectedImg).addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        reference.getDownloadUrl().addOnSuccessListener(uri -> {
                            String imgUrl = uri.toString();
                            String uid = auth.getUid();
                            String number = auth.getCurrentUser().getPhoneNumber();

                            User user = new User(uid, name, number, imgUrl);
                            database.getReference()
                            .child("users")
                            .child(uid)
                            .setValue(user)
                            .addOnSuccessListener(aVoid -> {
                                dialog.dismiss();
                                Intent intent = new Intent(SetupProfileActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            });
                        });
                    }
                });
            } else {
                String uid = auth.getUid();
                String number = auth.getCurrentUser().getPhoneNumber();

                User user = new User(uid, name, number, "Empety");
                database.getReference()
                        .child("users")
                        .child(uid)
                        .setValue(user)
                        .addOnSuccessListener(aVoid -> {
                            dialog.dismiss();
                            Intent intent = new Intent(SetupProfileActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        });
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data != null) {
            if (data.getData() != null) {
                binding.imgProfil.setImageURI(data.getData());
                selectedImg = data.getData();
            }
        }
    }
}