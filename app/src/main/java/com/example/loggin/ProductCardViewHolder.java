package com.example.loggin;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.NetworkImageView;

public class ProductCardViewHolder extends RecyclerView.ViewHolder {


    public ImageView foto_prod;
    public TextView nombre_prod;
    public TextView categoria_prod;
//    public Button go;
//    public LinearLayout espacio;



    public ProductCardViewHolder(@NonNull View itemView) {
        super(itemView);

        foto_prod = itemView.findViewById(R.id.foto_prod);
        nombre_prod = itemView.findViewById(R.id.nombre_prod);
        categoria_prod = itemView.findViewById(R.id.catego_prod);
//        go = (Button) itemView.findViewById(R.id.go);
//        espacio = (LinearLayout) itemView.findViewById(R.id.espacio);
        
    }
}

