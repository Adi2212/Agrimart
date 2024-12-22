package com.example.agrimart.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agrimart.R;
import com.example.agrimart.model.Product;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> productList;
    private DeleteButtonClickListener deleteClickListener;

    public ProductAdapter(List<Product> productList,  DeleteButtonClickListener deleteClickListener) {
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
        if (product != null){
            holder.productName.setText("Product Name: " + product.getProductName());
            holder.quantity.setText("Quantity (KG): " + product.getQuantity());
            holder.phoneNumber.setText("Phone Number: " + product.getPhoneNumber());
            holder.deleteButton.setOnClickListener(v -> deleteClickListener.onDeleteClick(product));
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void updateProductList(List<Product> updatedList) {
        this.productList = updatedList;
        notifyDataSetChanged();
    }


    public interface DeleteButtonClickListener {
        void onDeleteClick(Product product);
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView productName, quantity, phoneNumber;
        ImageButton deleteButton;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productName);
            quantity = itemView.findViewById(R.id.quantity);
            phoneNumber = itemView.findViewById(R.id.phoneNumber);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
