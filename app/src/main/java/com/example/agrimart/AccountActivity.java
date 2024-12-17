package com.example.agrimart;

import android.os.Bundle;
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

    private TextView userEmail, userName, userPhone, userAddress, totalProducts;
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
            String emailKey = currentUser.getEmail().replace(".", "_"); // Convert email to match Firebase keys
            databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(emailKey);

            // Load user data from Firebase
            loadUserData();
        } else {
            // If user is not logged in
            userEmail.setText("No User Logged In");
        }
    }

    private void loadUserData() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Fetch user details
                    String email = dataSnapshot.child("email").getValue(String.class);
                    String name = dataSnapshot.child("name").getValue(String.class);
                    String phone = dataSnapshot.child("phone").getValue(String.class);
                    String address = dataSnapshot.child("address").getValue(String.class);

                    // Fetch total number of products
                    DataSnapshot productsSnapshot = dataSnapshot.child("products");
                    int productCount = 0;

                    if (productsSnapshot.exists()) {
                        productCount = (int) productsSnapshot.getChildrenCount();
                    }

                    // Update UI with data
                    userEmail.setText("Email: " + email);
                    userName.setText("Name: " + name);
                    userPhone.setText("Phone: " + phone);
                    userAddress.setText("Address: " + address);
                    totalProducts.setText("Total Products: " + productCount);
                } else {
                    userEmail.setText("No data found for this user");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
                totalProducts.setText("Error loading data.");
            }
        });
    }
}
