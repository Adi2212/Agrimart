package com.example.agrimart;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.agrimart.adapter.ProductAdapter;
import com.example.agrimart.model.Product;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference databaseRef;
    private RadioGroup productRadioGroup;
    private ViewPager2 viewPager;
    private List<Integer> images;
    private int currentPage = 0;
    private Handler handler = new Handler();
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.viewPager);
        initializeSlideshow();
        startAutoSlide();

        productRadioGroup = findViewById(R.id.productRadioGroup);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findViewById(R.id.gotoProducts).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ProductsActivity.class));
            }
        });

        findViewById(R.id.ListProduct).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = productRadioGroup.getCheckedRadioButtonId();

                if (selectedId == -1) {
                    Toast.makeText(MainActivity.this, "Please select a product", Toast.LENGTH_SHORT).show();
                } else {
                    RadioButton selectedButton = findViewById(selectedId);
                    String selectedProduct = selectedButton.getText().toString();

                    Intent intent = new Intent(MainActivity.this, ListProductActivity.class);
                    intent.putExtra("PRODUCT_TYPE", selectedProduct);
                    startActivity(intent);
                }
            }
        });

        mAuth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference("Users");

         MobileAds.initialize(this, new OnInitializationCompleteListener() {
             @Override
             public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {

             }
         });
         AdView mAdview=findViewById(R.id.adView);
        AdRequest adRequest=new AdRequest.Builder().build();
        mAdview.loadAd(adRequest);

    }

    private void initializeSlideshow() {
        images = new ArrayList<>();
        images.add(R.drawable.farmer1);
        images.add(R.drawable.farmer2);
        images.add(R.drawable.farmer3);
        images.add(R.drawable.farmer4);

        viewPager.setAdapter(new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull android.view.ViewGroup parent, int viewType) {
                ImageView imageView = new ImageView(parent.getContext());
                imageView.setLayoutParams(new android.view.ViewGroup.LayoutParams(
                        android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                        android.view.ViewGroup.LayoutParams.MATCH_PARENT));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setBackgroundResource(R.drawable.btn_shape);
                return new RecyclerView.ViewHolder(imageView) {
                };
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                ((ImageView) holder.itemView).setImageResource(images.get(position));
            }

            @Override
            public int getItemCount() {
                return images.size();
            }
        });
    }

    private void startAutoSlide() {
        runnable = new Runnable() {
            @Override
            public void run() {
                if (currentPage == images.size()) {
                    currentPage = 0;
                }
                viewPager.setCurrentItem(currentPage++, true);
                handler.postDelayed(this, 4000);
            }
        };
        handler.postDelayed(runnable, 4000);
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
        }
        else if (item.getItemId() == R.id.logoutMenuBtn) {
            logOut();
        }
        else if (item.getItemId() == R.id.productsMenuBtn) {
           toProducts();
        }
        return true;
    }

    private void toAccount() {
        startActivity(new Intent(MainActivity.this, AccountActivity.class));
    }

    private void toProducts() {
        startActivity(new Intent(MainActivity.this, ProductsActivity.class));
    }

    private void logOut() {
        SharedPreferences preferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();

        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(MainActivity.this, HomeActivity.class));
        finish();
    }
}
