package com.example.loggin.Adaptadores;

import android.app.DownloadManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.loggin.Fragments.FragmentEditarProducto;
import com.example.loggin.Objetos.Producto;
import com.example.loggin.R;
import com.example.loggin.Objetos.Reserva;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class AdaptadorProductos extends RecyclerView.Adapter<AdaptadorProductos.ViewHolder> {

    public Context context;
    private DatabaseReference ref;
    private StorageReference sto;
    private String valor_dolar_euro;
    private String valor_libra_euro;
    private String VALOR_EURO="1";
    private RequestQueue mQueue;

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView nombre,categoria,precio,descripcion;
        public ImageView foto_producto,disponible;
        public CardView fondo;

        public String valor_dolar,valor_libra;
        NotificationManager mNotificationManager;
        public Button boton;

        public ViewHolder(final View itemView) {
            super(itemView);



            mQueue = Volley.newRequestQueue(context);
            leerValoresMonedas();


            nombre = (TextView)itemView.findViewById(R.id.nombre_prod);
            categoria = (TextView)itemView.findViewById(R.id.categoria);
            precio = (TextView)itemView.findViewById(R.id.precio);
            descripcion = (TextView)itemView.findViewById(R.id.descripcion);
            fondo = (CardView) itemView.findViewById(R.id.fondo_item);



            foto_producto = (ImageView)itemView.findViewById(R.id.foto_producto);
            disponible = (ImageView) itemView.findViewById(R.id.disponible);
            boton = (Button) itemView.findViewById(R.id.reservar);
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

        System.out.println(valor_dolar_euro);
        System.out.println(valor_libra_euro);
        LayoutInflater inflater = LayoutInflater.from(context);
        //Especificamos el fichero XML que se utilizará como vista
        View contactView = inflater.inflate(R.layout.elemento_lista, parent, false);
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final AdaptadorProductos.ViewHolder viewHolder, final int position) {

        ref = FirebaseDatabase.getInstance().getReference();
        sto = FirebaseStorage.getInstance().getReference();

//       final AppCompatActivity activity=(AppCompatActivity)viewHolder.itemView.getContext();

        SharedPreferences credenciales = context.getSharedPreferences("datos_usuario", Context.MODE_PRIVATE);
        final String dueño_actual = credenciales.getString("id_usuario","");
        final Boolean admin= credenciales.getBoolean("admin",false);
        final int tipo_moneda= credenciales.getInt("moneda",0);
        System.out.println(tipo_moneda);
        final String nombre_cliente = credenciales.getString("nombre_usuario","");

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


        viewHolder.foto_producto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentEditarProducto frag = new FragmentEditarProducto();

                //ARREGLAR
//              frag.newInstance(producto.getId(),"id");

                if (admin){

                    Bundle args = new Bundle();
                    args.putString("param1",producto.getId());
                    frag.setArguments(args);
                    AppCompatActivity activity= (AppCompatActivity) viewHolder.itemView.getContext();
                    activity.getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                            .replace(R.id.fragments_admin, frag)
                            .addToBackStack(null)
                            .commit();
                }else{

                    Bundle args = new Bundle();
                    args.putString("param1",producto.getId());
                    frag.setArguments(args);
                    AppCompatActivity activity= (AppCompatActivity) viewHolder.itemView.getContext();
                    activity.getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                            .replace(R.id.frame_fragments, frag)
                            .addToBackStack(null)
                            .commit();

                }

            }
        });

        viewHolder.boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ref.child("tienda").
                        child("reservas").
                        orderByChild("nombre_producto")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    Calendar calendar = Calendar.getInstance();

                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                                    String fecha_creacion = sdf.format(calendar.getTime());

                                        Reserva nueva_reserva = new Reserva(producto.getNombre(),nombre_cliente,producto.getId(),dueño_actual,fecha_creacion);
                                        String clave = ref.child("tienda").child("reservas").push().getKey();

                                        ref.child("tienda").child("reservas").child(clave).setValue(nueva_reserva);
                                        Toast.makeText(context, "Reserva registrada con éxito", Toast.LENGTH_LONG).show();
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.productos.size();
    }

    //API
    public void leerValoresMonedas(){
        String url_monedas = "https://api.exchangeratesapi.io/latest?symbols=USD,GBP";

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url_monedas, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {

                    JSONObject respuestaAPI=response.getJSONObject("rates");

                     valor_dolar_euro=respuestaAPI.getString("USD");
                     valor_libra_euro= respuestaAPI.getString("GBP");

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
          }
        );
        mQueue.add(jsonObjectRequest);
    }

}
