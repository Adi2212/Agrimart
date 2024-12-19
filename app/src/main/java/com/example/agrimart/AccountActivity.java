package com.example.agrimart;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AccountActivity extends AppCompatActivity {

    private TextView userEmail, totalProducts;
    private EditText userName, userPhone, userAddress;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        // Initialize views
        userEmail = findViewById(R.id.userEmail);
        userName = findViewById(R.id.userName);
        userPhone = findViewById(R.id.userPhone);
        userAddress = findViewById(R.id.userAddress);
        totalProducts = findViewById(R.id.totalProducts);

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            String emailKey = currentUser.getEmail().replace(".", "_");
            databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(emailKey);

            // Load user data from Firebase
            loadUserData();
        } else {
            userEmail.setText("No User Logged In");
        }

        // Save Button Listener
        Button saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(v -> saveUserData());
    }

    private void loadUserData() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String email = dataSnapshot.child("email").getValue(String.class);
                    String name = dataSnapshot.child("name").getValue(String.class);
                    String phone = dataSnapshot.child("phone").getValue(String.class);
                    String address = dataSnapshot.child("address").getValue(String.class);

                    DataSnapshot productsSnapshot = dataSnapshot.child("products");
                    int productCount = (productsSnapshot.exists()) ? (int) productsSnapshot.getChildrenCount() : 0;

                    // Update UI
                    userEmail.setText("Email: " + email);
                    userName.setText(name);
                    userPhone.setText(phone);
                    userAddress.setText(address);
                    totalProducts.setText("Total Products: " + productCount);
                } else {
                    userEmail.setText("No data found for this user");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                totalProducts.setText("Error loading data.");
            }
        });
    }

    private void saveUserData() {
        databaseReference.child("name").setValue(userName.getText().toString());
        databaseReference.child("phone").setValue(userPhone.getText().toString());
        databaseReference.child("address").setValue(userAddress.getText().toString());
    }
}
