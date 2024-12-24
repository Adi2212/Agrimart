package com.example.agrimart;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UpdateUserInfoActivity extends AppCompatActivity {

    private EditText editName, editPhone, editAddress;
    private Button updateButton;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user_info);

        // Initialize views
        editName = findViewById(R.id.editName);
        editPhone = findViewById(R.id.editPhone);
        editAddress = findViewById(R.id.editAddress);
        updateButton = findViewById(R.id.updateButton);

        // Initialize Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            String emailKey = currentUser.getEmail().replace(".", "_");
            databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(emailKey);
        }

        // Set OnClickListener for the Update button
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserInfo();
            }
        });
    }

    private void updateUserInfo() {
        String name = editName.getText().toString().trim();
        String phone = editPhone.getText().toString().trim();
        String address = editAddress.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(address)) {
            Toast.makeText(UpdateUserInfoActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }
        // Update user info in Firebase
        if (databaseReference != null) {
            databaseReference.child("name").setValue(name);


            databaseReference.child("phone").setValue(phone);


            databaseReference.child("address").setValue(address)

                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(UpdateUserInfoActivity.this, "Information updated successfully!", Toast.LENGTH_SHORT).show();
                            finish(); // Close the activity
                        } else {
                            Toast.makeText(UpdateUserInfoActivity.this, "Failed to update information.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
