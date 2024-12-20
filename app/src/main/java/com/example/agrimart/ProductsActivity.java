package com.example.agrimart;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agrimart.adapter.ProductAdapter;
import com.example.agrimart.model.Product;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProductsActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference databaseRef;
    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        Toolbar toolbar = findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationIcon(R.drawable.back_icon);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());


        mAuth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference("Users");

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set an empty adapter initially to avoid "No adapter attached" error
        productAdapter = new ProductAdapter(productList, product -> confirmDeleteProduct(product, getFormattedEmail()));
        recyclerView.setAdapter(productAdapter);

        loadProductsFromFirebase();
    }

    private String getFormattedEmail() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            return user.getEmail().replace(".", "_");
        }
        return null;
    }

    private void loadProductsFromFirebase() {
        String formattedEmail = getFormattedEmail();
        if (formattedEmail == null) {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Listen to all product categories
        databaseRef.child(formattedEmail).child("products").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productList.clear();

                // Loop through all categories
                for (DataSnapshot categorySnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot productSnapshot : categorySnapshot.getChildren()) {
                        Product product = productSnapshot.getValue(Product.class);
                        if (product != null) {
                            product.setKey(productSnapshot.getKey());
                            product.setCategory(categorySnapshot.getKey()); // Set the category
                            Log.d("FirebaseData", "Product: " + product.getProductName() + ", Key: " + product.getKey() + ", Category: " + product.getCategory());
                            productList.add(product);
                        } else {
                            Log.e("FirebaseData", "Product is null");
                        }
                    }
                }

                if (productAdapter != null) {
                    productAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ProductsActivity.this, "Failed to load products.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmDeleteProduct(Product product, String userId) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Confirmation")
                .setMessage("Are you sure you want to delete this product?")
                .setPositiveButton("Yes", (dialog, which) -> deleteProductFromFirebase(product, userId))
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void deleteProductFromFirebase(Product product, String userId) {
        if (product == null || product.getCategory() == null || product.getKey() == null) {
            Toast.makeText(ProductsActivity.this, "Error: Could not delete product. Invalid product data.", Toast.LENGTH_SHORT).show();
            return;
        }

        databaseRef.child(userId).child("products").child(product.getCategory()).child(product.getKey()).removeValue()
                .addOnSuccessListener(aVoid -> {
                    productAdapter.removeProduct(product);
                    Toast.makeText(ProductsActivity.this, "Product deleted successfully.", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(ProductsActivity.this, "Failed to delete product: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.side_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logoutMenuBtn) {
            logOut();
        }
        return true;
    }

    private void logOut() {
        SharedPreferences preferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();

        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(ProductsActivity.this, HomeActivity.class));
        finish();
    }
}