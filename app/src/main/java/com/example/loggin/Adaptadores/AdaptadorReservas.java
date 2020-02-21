package com.example.loggin.Adaptadores;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.loggin.Dialogos.DialogoEstado;
import com.example.loggin.R;
import com.example.loggin.Objetos.Reserva;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class AdaptadorReservas extends RecyclerView.Adapter<AdaptadorReservas.ViewHolder>{

    public Context context;
    private DatabaseReference ref;
    private StorageReference sto;
    private Handler mWaitHandler = new Handler();
    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView nombre,fecha,estado;
        private ImageView foto;
        private ImageButton borrar;
        private LinearLayout fondo_estado,fondo_cancelar;
        private CardView fondo;

        public ViewHolder(final View itemView) {
            super(itemView);

            fondo_cancelar = (LinearLayout) itemView.findViewById(R.id.fondocancelar);
            fondo_estado = (LinearLayout) itemView.findViewById(R.id.fondo_estado);
            fondo = (CardView) itemView.findViewById(R.id.fondo_reserva);
            nombre = (TextView) itemView.findViewById(R.id.nombre_reserva);
            fecha = (TextView) itemView.findViewById(R.id.fecha_reserva);
            estado = (TextView) itemView.findViewById(R.id.estado);
            foto = (ImageView) itemView.findViewById(R.id.foto_reserva);

            borrar = (ImageButton) itemView.findViewById(R.id.borrar_reserva);

        }
    }

    public List<Reserva> reservas;

    public AdaptadorReservas(List<Reserva> reservas){
        this.reservas = reservas;
    }

    @NonNull
    @Override
    public AdaptadorReservas.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        //Especificamos el fichero XML que se utilizará como vista
        View contactView = inflater.inflate(R.layout.elemento_lista_reservas, parent, false);
        AdaptadorReservas.ViewHolder viewHolder = new AdaptadorReservas.ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final AdaptadorReservas.ViewHolder viewHolder, final int position) {

        ref = FirebaseDatabase.getInstance().getReference();
        sto = FirebaseStorage.getInstance().getReference();

        SharedPreferences credenciales = context.getSharedPreferences("datos_usuario", Context.MODE_PRIVATE);
        final Boolean admin= credenciales.getBoolean("admin",false);

        //Vamos obteniendo mail por mail
        final Reserva reserva = this.reservas.get(position);
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

        String estado_pedido="";

        switch (reserva.getEstado()){

            case 0:
                estado_pedido="Recibido";
                viewHolder.fondo_estado.setBackgroundResource(R.drawable.recibido);
                break;
            case 1:
                estado_pedido="Preparado";
                viewHolder.fondo_estado.setBackgroundResource(R.drawable.preparado);
                break;
            case 2:
                estado_pedido="Enviado";
                viewHolder.fondo_estado.setBackgroundResource(R.drawable.enviado);
                break;
        }

        viewHolder.nombre.setText(reserva.getNombre_producto());
        viewHolder.fecha.setText(reserva.getFecha());
        viewHolder.estado.setText(estado_pedido);

        Glide.with(context).load(reserva.getFoto_url()).into(viewHolder.foto);


        if (admin){
            viewHolder.foto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AppCompatActivity activity= (AppCompatActivity) viewHolder.itemView.getContext();
                    DialogoEstado dia = new DialogoEstado();
                    Bundle args = new Bundle();
                    args.putString("id",reserva.getId());
                    dia.setArguments(args);
                    dia.show(activity.getSupportFragmentManager(),"estado");

                }
            });
        }





        viewHolder.borrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (reserva.getEstado()>0){
                    Toast.makeText(context, "Es demasiado tarde para cancelar la reserva", Toast.LENGTH_SHORT).show();
                }else{
                    reservas.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(0, reservas.size());

                    mWaitHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                ref.child("tienda").child("reservas").child(reserva.getId()).removeValue();
                                Toast.makeText(context, "Reserva cancelada", Toast.LENGTH_SHORT).show();
                            } catch (Exception ignored) {
                                ignored.printStackTrace();
                            }
                        }
                    }, 1000);  // Give a 5 seconds delay.
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.reservas.size();
    }

}
