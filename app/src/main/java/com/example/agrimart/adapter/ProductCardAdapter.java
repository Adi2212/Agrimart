package com.example.agrimart.adapter;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agrimart.R;

import java.io.IOException;
import java.io.InputStream;

public class ProductCardAdapter extends RecyclerView.Adapter<ProductCardAdapter.ProductViewHolder> {

    private final Context context;
    private final String[] imageNames;
    private final String[] titles;

    public ProductCardAdapter(Context context, String[] imageNames, String[] titles) {
        this.context = context;
        this.imageNames = imageNames;
        this.titles = titles;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_card, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        try {
            // Load image from assets
            InputStream inputStream = context.getAssets().open(imageNames[position]);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            holder.itemImage.setImageBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }

        holder.itemText.setText(titles[position]);
    }

    @Override
    public int getItemCount() {
        return titles.length;
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;
        TextView itemText;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.item_image);
            itemText = itemView.findViewById(R.id.item_text);
        }
    }
}
