package com.example.loggin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ProductCardRecyclerViewAdapter extends RecyclerView.Adapter<ProductCardViewHolder> {

    private List<Producto> productList;
    public Context context;

    ProductCardRecyclerViewAdapter(List<Producto> productList) {
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.carta, parent, false);

        return new ProductCardViewHolder(layoutView);

    }

    @Override
    public void onBindViewHolder(@NonNull final ProductCardViewHolder holder, int position) {
        // TODO: Put ViewHolder binding code here in MDC-102

//        holder.go.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                collapse_layout(holder.espacio,1000,800,360);
//            }
//        });
            final Producto producto = this.productList.get(position);

            holder.nombre_prod.setText(producto.getNombre());
            holder.categoria_prod.setText(producto.getCategoria());
            Glide.with(context).load(producto.getFoto_url()).into(holder.foto_prod);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }
}
