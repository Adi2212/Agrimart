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
    private EditText phoneNumber;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseRef;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "ProductSubmissionPrefs";
    private static final String LAST_SUBMISSION_TIME = "lastSubmissionTime";
    private static final long HALF_HOUR_MILLIS = 30 * 60 * 1000 * 0; // Half-hour interval

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_product);
        getWindow().setStatusBarColor(getResources().getColor(R.color.black));

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
        phoneNumber = findViewById(R.id.phoneNumber);

        String productType = getIntent().getStringExtra("category");
        category.setText(productType);

        String productnamed = getIntent().getStringExtra("productName");
        productName.setText(productnamed);

        // Submit button
        btnSubmit.setOnClickListener(v -> {
            if (canSubmit()) {
                saveDataToFirebase();
            } else {
                Toast.makeText(this, "You can list this product after 30 minutes.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean canSubmit() {
        long lastSubmissionTime = sharedPreferences.getLong(LAST_SUBMISSION_TIME, 0);
        long currentTime = Calendar.getInstance().getTimeInMillis();
        return (currentTime - lastSubmissionTime) >= HALF_HOUR_MILLIS;
    }

    private void saveDataToFirebase() {
        // Get user inputs
        String quantity = etQuantity.getText().toString();
        String listProduct = productName.getText().toString();
        String productCategory = category.getText().toString();
        String phoneNumber = this.phoneNumber.getText().toString(); // Corrected line

        if (quantity.isEmpty() || listProduct.isEmpty() || productCategory.isEmpty() || phoneNumber.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Format email as key
        String emailKey = mAuth.getCurrentUser().getEmail().replace(".", "_");

        // Save product data under the specific category
        DatabaseReference categoryRef = databaseRef.child(emailKey).child("products").child(productCategory).push();
        Product product = new Product(listProduct, quantity, phoneNumber);

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