package com.example.agrimart;

import com.example.agrimart.model.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class ListProductActivity extends AppCompatActivity {

    private Spinner ProductList;
    private EditText etQuantity, etPhoneNumber;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseRef;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "ProductSubmissionPrefs";
    private static final String LAST_SUBMISSION_TIME = "lastSubmissionTime";
    private static final long HALF_HOUR_MILLIS = 30 * 60 * 1000; // 30 minutes in milliseconds


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_product);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference("Users");

        // SharedPreferences
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // UI Elements
        Button btnSubmit = findViewById(R.id.btnSubmit);
        etQuantity = findViewById(R.id.etQuantity);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        ProductList = findViewById(R.id.mySpinner);


        String productType = getIntent().getStringExtra("PRODUCT_TYPE");

        if (productType != null) {
            //selectedProductType.setText("Selected Product: " + productType);
            loadSpinnerData(productType);
        }

        ProductList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                Toast.makeText(ListProductActivity.this, "Selected: " + selectedItem, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Submit button
        btnSubmit.setOnClickListener(v -> {
            if (canSubmit()) {
                saveDataToFirebase();
            } else {
                Toast.makeText(this, "You can List this fruit after half hour .", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadSpinnerData(String productType) {
        int arrayResourceId;

        switch (productType) {
            case "Fruits":
                arrayResourceId = R.array.dropdown_fruits;
                break;
            case "Beans":
                arrayResourceId = R.array.dropdown_beans;
                break;
            case "Vegetables":
                arrayResourceId = R.array.dropdown_vegetables;
                break;
            default:
                arrayResourceId = R.array.dropdown_fruits;
                break;
        }

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, arrayResourceId, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ProductList.setAdapter(adapter);
    }

    private boolean canSubmit() {
        long lastSubmissionTime = sharedPreferences.getLong(LAST_SUBMISSION_TIME, 0);
        long currentTime = Calendar.getInstance().getTimeInMillis();
        return (currentTime - lastSubmissionTime) >= HALF_HOUR_MILLIS;
    }


    private void saveDataToFirebase() {
        // Get user inputs
        String quantity = etQuantity.getText().toString();
        String phoneNumber = etPhoneNumber.getText().toString();
        String selectedProduct = ProductList.getSelectedItem().toString();

        if (quantity.isEmpty() || phoneNumber.isEmpty() || selectedProduct.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the reference to the 'Users' node in Realtime Database
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");

        // Retrieve the user ID from Realtime Database, assuming the user's ID is stored under their 'user_id' node.
        usersRef.orderByChild("email").equalTo(mAuth.getCurrentUser().getEmail()) // Assuming you can identify user by email
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Fetch the user ID from the snapshot, assuming the user ID is the key.
                            String userId = dataSnapshot.getChildren().iterator().next().getKey();

                            // Save product data under that user ID
                            if (userId != null) {
                                DatabaseReference userProductsRef = usersRef.child(userId).child("products").push();

                                // Create a Product object
                                Product product = new Product(selectedProduct, quantity, phoneNumber);

                                // Save product data to Firebase
                                userProductsRef.setValue(product).addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        // Save the current time to SharedPreferences
                                        sharedPreferences.edit()
                                                .putLong(LAST_SUBMISSION_TIME, Calendar.getInstance().getTimeInMillis())
                                                .apply();

                                        Toast.makeText(ListProductActivity.this, "Data saved successfully!", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(ListProductActivity.this, MainActivity.class));
                                    } else {
                                        Toast.makeText(ListProductActivity.this, "Failed to save data", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                        } else {
                            Toast.makeText(ListProductActivity.this, "User not found in database", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(ListProductActivity.this, "Error retrieving user data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Product class for data structure
   /* public static class Product {
        public String productName;
        public String quantity;
        public String phoneNumber;

        public Product(String productName, String quantity, String phoneNumber) {
            this.productName = productName;
            this.quantity = quantity;
            this.phoneNumber = phoneNumber;
        }
    }*/
}
