package com.example.agrimart;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agrimart.adapter.ProductCardAdapter;

import java.util.ArrayList;

public class SelectProduct extends AppCompatActivity {
    String[] allImagePaths = {
            // Fruits
            "fruits/apple.png", "fruits/banana.png", "fruits/blueberry.png",
            "fruits/CustardApple_sitaphal.png", "fruits/grapes.png", "fruits/guava.png",
            "fruits/jackfruit.png", "fruits/kiwi.png", "fruits/mango.png", "fruits/orange.png",
            "fruits/papaya.png", "fruits/pineapple.png", "fruits/pomegranate.png",
            "fruits/strawberry.png", "fruits/watermelon.png",

            // Beans
            "beans/blackgram_uraddal.png", "beans/chickpea_chana.png", "beans/greanbean_moongdal.png",
            "beans/pigeonpea_toor.png", "beans/red-beans_rajma.png", "beans/soyabean.png",

            // Vegetables
            "vegetables/beetroot.png", "vegetables/bitter-gourd_karela.png", "vegetables/brinjal_baingan.png",
            "vegetables/cabbage.png", "vegetables/capsicum.png", "vegetables/carrots.png",
            "vegetables/cauliflower.png", "vegetables/drumstick_shevga.png", "vegetables/garlic.png",
            "vegetables/okra_bhindi.png", "vegetables/onion.png", "vegetables/potato.png",
            "vegetables/radish.png", "vegetables/spinach.png", "vegetables/tomato.png"
    };

    String[] allTitles = {
            // Fruits
            "Apple", "Banana", "Blueberry", "Custard Apple", "Grapes", "Guava",
            "Jackfruit", "Kiwi", "Mango", "Orange", "Papaya", "Pineapple",
            "Pomegranate", "Strawberry", "Watermelon",

            // Beans
            "Blackgram Urad Dal", "Chickpea Chana", "Greenbean Moongdal",
            "Pigeonpea Toor", "Red Beans Rajma", "Soyabean",

            // Vegetables
            "Beetroot", "Bitter Gourd (Karela)", "Brinjal (Baingan)",
            "Cabbage", "Capsicum", "Carrots",
            "Cauliflower", "Drumstick (Shevga)", "Garlic",
            "Okra (Bhindi)", "Onion", "Potato",
            "Radish", "Spinach", "Tomato"
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_product);
        getWindow().setStatusBarColor(getResources().getColor(R.color.black));

        Toolbar toolbar = findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationIcon(R.drawable.back_icon);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());

        String category = getIntent().getStringExtra("category");
        if (category == null) {
            category = "fruits"; // Default category
        }

        Toast.makeText(this, category, Toast.LENGTH_SHORT).show();

        // Filter data based on category
        ArrayList<String> filteredImagePaths = new ArrayList<>();
        ArrayList<String> filteredTitles = new ArrayList<>();

        for (int i = 0; i < allImagePaths.length; i++) {
            if (allImagePaths[i].startsWith(category)) {
                filteredImagePaths.add(allImagePaths[i]);
                filteredTitles.add(allTitles[i]);
            }
        }


        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        ProductCardAdapter adapter = new ProductCardAdapter(
                this,
                filteredImagePaths.toArray(new String[0]),
                filteredTitles.toArray(new String[0])
        );
        recyclerView.setAdapter(adapter);
    }
}