package com.example.loggin.Adaptadores;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.loggin.Objetos.Cliente;
import com.example.loggin.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class AdaptadorUsuarios extends RecyclerView.Adapter<AdaptadorUsuarios.ViewHolder> {

    public Context context;
    private DatabaseReference ref;
    private StorageReference sto;
    private Handler mWaitHandler = new Handler();

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView email,nombre,contraseña,moneda;
        private ImageView foto;
        private ImageButton borrar;


        public ViewHolder(final View itemView) {
            super(itemView);

            context = itemView.getContext();
            email = (TextView) itemView.findViewById(R.id.email_usuario);
            nombre = (TextView) itemView.findViewById(R.id.nombre_usuario);
            contraseña = (TextView) itemView.findViewById(R.id.contraseña_usuario);
            moneda = (TextView) itemView.findViewById(R.id.moneda_usuario);

            borrar = (ImageButton) itemView.findViewById(R.id.borrar_usuario);
            foto = (ImageView) itemView.findViewById(R.id.imagen_usuario);


        }
    }


    public List<Cliente> clientes;

    public AdaptadorUsuarios(List<Cliente> clientes){
        this.clientes = clientes;
    }

    @NonNull
    @Override
    public AdaptadorUsuarios.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        //Especificamos el fichero XML que se utilizará como vista
        View contactView = inflater.inflate(R.layout.elemento_lista_usuario, parent, false);
        AdaptadorUsuarios.ViewHolder viewHolder = new AdaptadorUsuarios.ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final AdaptadorUsuarios.ViewHolder viewHolder, final int position) {

        ref = FirebaseDatabase.getInstance().getReference();
        sto = FirebaseStorage.getInstance().getReference();


        final Cliente cliente = this.clientes.get(position);

        String valor_moneda = "";

        switch (cliente.getMoneda()){
            case 0:
                valor_moneda="Euro";
                break;
            case 1:
                valor_moneda="Dolar";
                break;
            case 2:
                valor_moneda="Libra";
                break;
        }

        viewHolder.nombre.setText(cliente.getNombre()+" "+cliente.getApellidos());
        viewHolder.email.setText(cliente.getEmail());
        viewHolder.contraseña.setText(cliente.getContraseña());
        viewHolder.moneda.setText(valor_moneda);

                //PROBLEMA DE LA OSTIA
        System.out.println(cliente.getUrl_foto());
        Glide.with(context)
                .load(cliente.getUrl_foto())
                .into(viewHolder.foto);

        viewHolder.borrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ref.child("tienda").child("reservas").orderByChild("id_cliente").equalTo(cliente.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChildren()){
                            for(DataSnapshot hijo:dataSnapshot.getChildren()) {
                                String clave = hijo.getKey();
                                ref.child("tienda").child("reservas").child(clave).removeValue();
                            }
                        }else{
                            System.out.println("No he entrado");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                ref.child("tienda").child("clientes").child(cliente.getId()).removeValue();
                Toast.makeText(context, "Cliente borrado con éxito", Toast.LENGTH_SHORT).show();
            }
        });



    }


    @Override
    public int getItemCount() {
        return this.clientes.size();
    }

}



