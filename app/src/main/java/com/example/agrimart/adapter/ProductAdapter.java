package com.example.agrimart.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agrimart.R;
import com.example.agrimart.model.Product;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private final List<Product> productList;
    private final OnDeleteClickListener deleteClickListener;

    public ProductAdapter(List<Product> productList, OnDeleteClickListener deleteClickListener) {
        this.productList = productList;
        this.deleteClickListener = deleteClickListener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.productName.setText("Product Name: " + product.getProductName());
        holder.quantity.setText("Quantity (KG): " + product.getQuantity());
        holder.deleteButton.setOnClickListener(v -> {
            deleteClickListener.onDeleteClick(product);
        });

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void removeProduct(Product product) {
        int position = productList.indexOf(product);
        if (position != -1) {
            productList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public interface OnDeleteClickListener {
        void onDeleteClick(Product product);
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView productName, phoneNumber, quantity;
        ImageView deleteButton;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productName);
            phoneNumber = itemView.findViewById(R.id.phoneNumber);
            quantity = itemView.findViewById(R.id.quantity);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
