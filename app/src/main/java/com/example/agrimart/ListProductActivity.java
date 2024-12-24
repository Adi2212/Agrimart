package com.example.agrimart;

import com.example.agrimart.model.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Calendar;

public class ListProductActivity extends AppCompatActivity {

    private TextView category, productName;
    private EditText etQuantity;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseRef;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "ProductSubmissionPrefs";
    private static final String LAST_SUBMISSION_TIME = "lastSubmissionTime";
    private static final long HALF_HOUR_MILLIS =  0; // Half-hour interval

    // Removed text views for phone number and username
    private String userPhoneNumber; // Variable to store phone number
    private String userName; // Variable to store user name
    private String Location;
    private boolean isUserDataFetched = false; // Flag to check if user data is fetched

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_product);


        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar2);
        category = findViewById(R.id.category);
        productName = findViewById(R.id.productName);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.back_icon);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference("Users");

        // SharedPreferences
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // UI Elements
        Button btnSubmit = findViewById(R.id.btnSubmit);
        etQuantity = findViewById(R.id.etQuantity);

        // Get product details from intent
        String productType = getIntent().getStringExtra("category");
        category.setText(productType);

        String productNamed = getIntent().getStringExtra("productName");
        productName.setText(productNamed);

        // Submit button listener
        btnSubmit.setOnClickListener(v -> {
            if (canSubmit() && isUserDataFetched) {
                saveDataToFirebase();
            } else {
                Toast.makeText(this, "You can list this product after 30 minutes, or ensure all user data is available.", Toast.LENGTH_SHORT).show();
            }
        });

        // Fetch phone number and user name from Firebase
        fetchPhoneNumber();
        fetchUserName();
        fetchLocation();
    }

    // Check if the product can be submitted (30-minute interval)
    private boolean canSubmit() {
        long lastSubmissionTime = sharedPreferences.getLong(LAST_SUBMISSION_TIME, 0);
        long currentTime = Calendar.getInstance().getTimeInMillis();
        return (currentTime - lastSubmissionTime) >= HALF_HOUR_MILLIS;
    }

    // Fetch phone number from Firebase (but don't show it on UI)
    private void fetchPhoneNumber() {
        String emailKey = mAuth.getCurrentUser().getEmail().replace(".", "_");

        // Fetch phone number from Firebase
        databaseRef.child(emailKey).child("phone").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                userPhoneNumber = task.getResult().getValue(String.class); // Store phone number
            }
            checkUserDataFetched();
        });
    }

    // Fetch user name from Firebase (but don't show it on UI)
    private void fetchUserName() {
        String emailKey = mAuth.getCurrentUser().getEmail().replace(".", "_");

        // Fetch user name from Firebase
        databaseRef.child(emailKey).child("name").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                userName = task.getResult().getValue(String.class); // Store user name
            }
            checkUserDataFetched();
        });
    }

    private void fetchLocation(){
        String emailKey = mAuth.getCurrentUser().getEmail().replace(".", "_");
        databaseRef.child(emailKey).child("address").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                Location = task.getResult().getValue(String.class);
            }
            checkUserDataFetched();
        });

    }

    // Check if both user data (phone number and user name) are fetched
    private void checkUserDataFetched() {
        if (userPhoneNumber != null && userName != null) {
            isUserDataFetched = true;
        }
    }

    // Save product data to Firebase
    private void saveDataToFirebase() {
        // Get user inputs
        String quantity = etQuantity.getText().toString();
        String listProduct = productName.getText().toString();
        String productCategory = category.getText().toString();

        // Check if all fields are filled
        if (quantity.isEmpty() || listProduct.isEmpty() || productCategory.isEmpty() || userPhoneNumber == null || userName == null) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Format email as key
        String emailKey = mAuth.getCurrentUser().getEmail().replace(".", "_");

        // Save product data under the specific category
        DatabaseReference categoryRef = databaseRef.child(emailKey).child("products").child(productCategory).push();
        Product product = new Product(listProduct, quantity, userPhoneNumber, userName,Location); // Use the stored phone number and name

        categoryRef.setValue(product).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                sharedPreferences.edit().putLong(LAST_SUBMISSION_TIME, Calendar.getInstance().getTimeInMillis()).apply();
                Toast.makeText(ListProductActivity.this, "Data saved successfully!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ListProductActivity.this, MainActivity.class));
            } else {
                Toast.makeText(ListProductActivity.this, "Failed to save data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
