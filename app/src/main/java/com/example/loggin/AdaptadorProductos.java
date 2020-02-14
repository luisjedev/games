package com.example.loggin;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class AdaptadorProductos extends RecyclerView.Adapter<AdaptadorProductos.ViewHolder> {

    public Context context;

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView nombre,categoria,precio,descripcion;
        public ImageView foto_producto,disponible;
        public CardView fondo;
        public Button boton;

//        public Button button;

        public ViewHolder(final View itemView) {
            super(itemView);
            nombre = (TextView)itemView.findViewById(R.id.nombre_prod);
            categoria = (TextView)itemView.findViewById(R.id.categoria);
            precio = (TextView)itemView.findViewById(R.id.precio);
            descripcion = (TextView)itemView.findViewById(R.id.descripcion);
            fondo = (CardView) itemView.findViewById(R.id.fondo_item);

            foto_producto = (ImageView)itemView.findViewById(R.id.foto_producto);
            disponible = (ImageView) itemView.findViewById(R.id.disponible);
            boton = (Button) itemView.findViewById(R.id.reservar);
//            button = (Button)itemView.findViewById(R.id.button);
        }
    }

    public List<Producto> productos;

    public AdaptadorProductos(List<Producto> productos){
        this.productos = productos;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        //Especificamos el fichero XML que se utilizará como vista
        View contactView = inflater.inflate(R.layout.elemento_lista, parent, false);
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(final AdaptadorProductos.ViewHolder viewHolder, final int position) {

        //Vamos obteniendo mail por mail
        final Producto producto = this.productos.get(position);
        //Enlazamos los elementos de la vista con el modelo

        SharedPreferences modonoche = context.getSharedPreferences("noche", Context.MODE_PRIVATE);
        Boolean modo_actual= modonoche.getBoolean("noche",false);

        if (modo_actual==true){

            viewHolder.fondo.setBackgroundResource(R.drawable.fondo_oscuro_fragment);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else{
            viewHolder.fondo.setBackgroundResource(R.drawable.fondo_claro_fragment);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        viewHolder.nombre.setText(producto.getNombre());
        viewHolder.categoria.setText(producto.getCategoria());
        viewHolder.descripcion.setText(producto.getDescripcion());
        viewHolder.precio.setText(producto.getPrecio()+" €");

        Glide.with(context).load(producto.getFoto_url()).into(viewHolder.foto_producto);

//        String nombre = user.getNombre();
//        String letra = nombre.substring(0,1);

//        viewHolder.button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                productos.remove(position);
//                notifyItemRemoved(position);
//                notifyItemRangeChanged(0, productos.size());
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return this.productos.size();
    }

}
