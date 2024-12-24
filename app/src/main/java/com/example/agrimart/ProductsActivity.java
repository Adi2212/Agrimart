package com.example.agrimart;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
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
    private List<Product> filteredList = new ArrayList<>();
    String category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);


        // Initialize toolbar
        Toolbar toolbar = findViewById(R.id.toolbar1);
        SearchView searchView = findViewById(R.id.searchview);


        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationIcon(R.drawable.back_icon);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());


        // Initialize Firebase and RecyclerView
        mAuth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference("Users");

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));



        productAdapter = new ProductAdapter(filteredList, product -> confirmDeleteProduct(product));
        recyclerView.setAdapter(productAdapter);



        // Set up SearchView

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterProducts(query);
                searchView.clearFocus();
                return true;
            }


            @Override
            public boolean onQueryTextChange(String newText) {
                filterProducts(newText);
                return true;
            }
        });


        loadProductsFromFirebase();

        category = getIntent().getStringExtra("category");
    }

    private void loadProductsFromFirebase() {
        String formattedEmail = getFormattedEmail();
        if (formattedEmail == null) {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show();
            return;
        }

        databaseRef.child(formattedEmail).child("products").addValueEventListener(new ValueEventListener() {
            @Override

            public void onDataChange(DataSnapshot dataSnapshot) {
                productList.clear();
                for (DataSnapshot categorySnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot productSnapshot : categorySnapshot.getChildren()) {
                        Product product = productSnapshot.getValue(Product.class);
                        if (product != null) {
                            product.setKey(productSnapshot.getKey());
                            product.setCategory(categorySnapshot.getKey());
                            productList.add(product);
                        }
                    }
                }

                // Filter by category if one is provided, otherwise show all products
                if (category != null) {
                    filterProductsByCategory(category);
                } else {
                    filteredList.clear();
                    filteredList.addAll(productList);
                    productAdapter.notifyDataSetChanged();
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ProductsActivity.this, "Failed to load products.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getFormattedEmail() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            return user.getEmail().replace(".", "_");
        }
        return null;
    }


    private void filterProducts(String query) {
        filteredList.clear();
        for (Product product : productList) {
            if (product.getProductName().toLowerCase().contains(query.toLowerCase()) ||
                    product.getCategory().toLowerCase().contains(query.toLowerCase())) {
                if (!filteredList.contains(product)) { // Prevent duplicates
                    filteredList.add(product);
                }
            }
        }
        productAdapter.notifyDataSetChanged();
    }

    private void filterProductsByCategory(String category) {
        filteredList.clear();
        for (Product product : productList) {
            if (product.getCategory() != null && product.getCategory().equalsIgnoreCase(category)) {
                filteredList.add(product);
            }
        }
        productAdapter.notifyDataSetChanged();
    }


    private void confirmDeleteProduct(Product product) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Confirmation")
                .setMessage("Are you sure you want to delete this product?")
                .setPositiveButton("Yes", (dialog, which) -> deleteProductFromFirebase(product))
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void deleteProductFromFirebase(Product product) {
        String formattedEmail = getFormattedEmail();
        if (formattedEmail == null || product.getCategory() == null || product.getKey() == null) {
            Toast.makeText(this, "Error: Invalid product data.", Toast.LENGTH_SHORT).show();
            return;
        }

        databaseRef.child(formattedEmail).child("products").child(product.getCategory()).child(product.getKey()).removeValue()
                .addOnSuccessListener(aVoid -> {
                    filteredList.remove(product);
                    productAdapter.notifyDataSetChanged();
                    Toast.makeText(this, "Product deleted successfully.", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to delete product: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.side_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.accountMenuBtn) {
            toAccount();
            return true;
        } else if (item.getItemId() == R.id.logoutMenuBtn) {
            logOut();
            return true;
        } else if (item.getItemId() == R.id.productsMenuBtn) {
            toProducts();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void toAccount() {
        startActivity(new Intent(ProductsActivity.this, AccountActivity.class));
    }

    private void toProducts() {
        startActivity(new Intent(ProductsActivity.this, ProductsActivity.class));
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
