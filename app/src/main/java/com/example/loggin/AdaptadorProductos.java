package com.example.loggin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdaptadorProductos extends RecyclerView.Adapter<AdaptadorProductos.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView nombre,categoria,precio,descripcion;
        public ImageView foto_producto,disponible;
//        public Button button;

        public ViewHolder(final View itemView) {
            super(itemView);
            nombre = (TextView)itemView.findViewById(R.id.nombre_prod);
            categoria = (TextView)itemView.findViewById(R.id.categoria);
            precio = (TextView)itemView.findViewById(R.id.precio);
            descripcion = (TextView)itemView.findViewById(R.id.descripcion);

            foto_producto = (ImageView)itemView.findViewById(R.id.foto_producto);
            disponible = (ImageView) itemView.findViewById(R.id.disponible);
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
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        //Especificamos el fichero XML que se utilizará como vista
        View contactView = inflater.inflate(R.layout.elemento_lista, parent, false);
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(final AdaptadorProductos.ViewHolder viewHolder, final int position) {

        //Vamos obteniendo mail por mail
        final Producto user = this.productos.get(position);
        //Enlazamos los elementos de la vista con el modelo




        viewHolder.nombre.setText(user.getNombre());
        String nombre = user.getNombre();
        String letra = nombre.substring(0,1);

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
        return this.cosas.size();
    }

}
