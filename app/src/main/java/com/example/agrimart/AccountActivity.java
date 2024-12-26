package com.example.agrimart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AccountActivity extends AppCompatActivity {

    private TextView userEmail, totalProducts, totalVegetables, totalFruits, totalBeans, userName, userPhone, userLocation;
    private CardView totalCard;
    private Button updateInfoButton;
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
        userLocation = findViewById(R.id.userLocation);
        totalProducts = findViewById(R.id.totalProducts);
        totalVegetables = findViewById(R.id.totalVegetables);
        totalFruits = findViewById(R.id.totalFruits);
        totalBeans = findViewById(R.id.totalBeans);
        totalCard = findViewById(R.id.totalCard);
        CardView vegetableCard = findViewById(R.id.vegetableCard);
        CardView fruitCard = findViewById(R.id.fruitCard);
        CardView beansCard = findViewById(R.id.beansCard);
        updateInfoButton = findViewById(R.id.updateInfoButton);

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        // Initialize toolbar
        Toolbar toolbar = findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationIcon(R.drawable.back_icon);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        if (currentUser != null) {
            String emailKey = currentUser.getEmail().replace(".", "_");
            databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(emailKey);

            // Load user data from Firebase
            loadUserData();
        } else {
            userEmail.setText("No User Logged In");
        }

        vegetableCard.setOnClickListener(view -> navigateToCategory("Vegetables"));
        fruitCard.setOnClickListener(view -> navigateToCategory("Fruits"));
        beansCard.setOnClickListener(view -> navigateToCategory("Beans"));

        totalCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to redirect to another activity
                Intent intent = new Intent(AccountActivity.this, ProductsActivity.class);

                // Start the new activity
                startActivity(intent);
            }
        });

        updateInfoButton.setOnClickListener(v -> {
            Intent intent = new Intent(AccountActivity.this, UpdateUserInfoActivity.class);
            startActivity(intent);
        });
    }

    private void navigateToCategory(String category) {
        Intent intent = new Intent(this, ProductsActivity.class); // Replace with your target activity class
        intent.putExtra("category", category);
        startActivity(intent);
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

                    // Update user info UI
                    userEmail.setText("Email: " + email);
                    userName.setText("Hey! " + name);
                    userPhone.setText("Phone: " + phone);
                    userLocation.setText("Address: " + address);

                    // Count products in each category
                    if (productsSnapshot.exists()) {
                        int vegetableCount = countProductsInCategory(productsSnapshot, "vegetables");
                        int fruitCount = countProductsInCategory(productsSnapshot, "fruits");
                        int beanCount = countProductsInCategory(productsSnapshot, "beans");

                        int totalCount = vegetableCount + fruitCount + beanCount;

                        // Update UI with product counts
                        totalProducts.setText(String.valueOf(totalCount));
                        totalVegetables.setText(String.valueOf(vegetableCount));
                        totalFruits.setText(String.valueOf(fruitCount));
                        totalBeans.setText(String.valueOf(beanCount));
                    }
                } else {
                    userEmail.setText("No data found for this user");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                totalProducts.setText("Error loading data.");
                totalVegetables.setText("Error loading data.");
                totalFruits.setText("Error loading data.");
                totalBeans.setText("Error loading data.");
            }
        });
    }

    private int countProductsInCategory(DataSnapshot productsSnapshot, String category) {
        DataSnapshot categorySnapshot = productsSnapshot.child(category);
        return categorySnapshot.exists() ? (int) categorySnapshot.getChildrenCount() : 0;
    }
}
