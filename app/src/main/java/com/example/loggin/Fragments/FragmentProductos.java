package com.example.loggin.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;

import com.example.loggin.Adaptadores.AdaptadorProductos;
import com.example.loggin.OnFragmentInteractionListener;
import com.example.loggin.ProductGridItemDecoration;
import com.example.loggin.Objetos.Producto;
import com.example.loggin.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;


public class FragmentProductos extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private StorageReference sto;
    private DatabaseReference ref;
    private ArrayList<Producto> items;
    private FrameLayout fondo;
    private RecyclerView lista_productos;
    private EditText nombre,precio_minimo,precio_maximo;
    private Spinner categoria;
    private AdaptadorProductos adapter;
    private Button buscar,mostrar_todos;
    private String[] lista_categorias;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private OnFragmentInteractionListener mListener;

    public FragmentProductos() {
        // Required empty public constructor
    }

    public static FragmentProductos newInstance(String param1, String param2) {
        FragmentProductos fragment = new FragmentProductos();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_fragment_productos, container, false);

        SharedPreferences credenciales = getActivity().getSharedPreferences("datos_usuario", Context.MODE_PRIVATE);
        final Boolean admin= credenciales.getBoolean("admin",false);

        fondo=(FrameLayout) v.findViewById(R.id.fondofrag);
        lista_productos = (RecyclerView) v.findViewById(R.id.lista_productos);
        items=new ArrayList<>();
        precio_maximo = (EditText) v.findViewById(R.id.precio_maximo);
        precio_minimo = (EditText) v.findViewById(R.id.precio_minimo);
        nombre=(EditText) v.findViewById(R.id.nombre_buscador);
        categoria = (Spinner) v.findViewById(R.id.categoria_buscador);
        buscar = (Button) v.findViewById(R.id.buscar);
        mostrar_todos = (Button) v.findViewById(R.id.mostrar_todos);



        ref= FirebaseDatabase.getInstance().getReference();
        sto= FirebaseStorage.getInstance().getReference();

        cargarCategorias();

        ref.child("tienda").child("productos").orderByChild("nombre").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                items.clear();
                for(DataSnapshot hijo:dataSnapshot.getChildren()) {
                    final Producto producto = hijo.getValue(Producto.class);
                    producto.setId(hijo.getKey());

                    if (admin){
                        items.add(producto);
                    }else{
                        if (producto.isDisponible()){
                            items.add(producto);
                         }
                    }
                }
                for(final Producto producto:items){
                    sto.child("tienda")
                            .child("productos")
                            .child("imagenes")
                            .child(producto.getId())
                            .getDownloadUrl()
                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    producto.setFoto_url(uri);
                                    adapter.notifyDataSetChanged();
                                }
                            });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



       comprobarNocheFragment();

       buscar.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               items.clear();
                String valor_precio_minimo = precio_minimo.getText().toString();
                String valor_precio_maximo = precio_maximo.getText().toString();
               final String nombre_buscador = nombre.getText().toString();
               final String valor_categoria = categoria.getSelectedItem().toString();


               if (valor_precio_maximo.equals("")){
                   valor_precio_maximo="10000";
               }
               if (valor_precio_minimo.equals("")){
                   valor_precio_minimo="0";
               }

               final double precioMax = Double.parseDouble(valor_precio_maximo);
               final double precioMin = Double.parseDouble(valor_precio_minimo);

               ref.child("tienda").child("productos").addValueEventListener(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                       if (dataSnapshot.hasChildren()){

                           for(DataSnapshot hijo:dataSnapshot.getChildren()){
                               final Producto producto = hijo.getValue(Producto.class);
                               producto.setId(hijo.getKey());

                               if(producto.getNombre().contains(nombre_buscador)
                                       && producto.getCategoria().equals(valor_categoria)
                                       && Double.parseDouble(producto.getPrecio()) > precioMin
                                       && Double.parseDouble(producto.getPrecio()) < precioMax){
                                   if (admin){
                                       items.add(producto);
                                   }else{
                                       if (producto.isDisponible()){
                                           items.add(producto);
                                       }
                                   }


                               }
                           }
                           for(final Producto producto:items){
                               sto.child("tienda")
                                       .child("productos")
                                       .child("imagenes")
                                       .child(producto.getId())
                                       .getDownloadUrl()
                                       .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                           @Override
                                           public void onSuccess(Uri uri) {
                                               producto.setFoto_url(uri);
                                               adapter.notifyDataSetChanged();
                                           }
                                       });
                           }

                       }
                   }

                   @Override
                   public void onCancelled(@NonNull DatabaseError databaseError) {

                   }
               });



               precio_maximo.setVisibility(View.GONE);
               precio_minimo.setVisibility(View.GONE);
               nombre.setVisibility(View.GONE);
               buscar.setVisibility(View.GONE);
               categoria.setVisibility(View.GONE);



           }
       });

       mostrar_todos.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               ref.child("tienda").child("productos").orderByChild("nombre").addValueEventListener(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                       items.clear();
                       for(DataSnapshot hijo:dataSnapshot.getChildren()) {
                           final Producto producto = hijo.getValue(Producto.class);
                           producto.setId(hijo.getKey());

                           if (admin){
                               items.add(producto);
                           }else{
                               if (producto.isDisponible()){
                                   items.add(producto);
                               }
                           }
                       }
                       for(final Producto producto:items){
                           sto.child("tienda")
                                   .child("productos")
                                   .child("imagenes")
                                   .child(producto.getId())
                                   .getDownloadUrl()
                                   .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                       @Override
                                       public void onSuccess(Uri uri) {
                                           producto.setFoto_url(uri);
                                           adapter.notifyDataSetChanged();
                                       }
                                   });
                       }
                   }
                   @Override
                   public void onCancelled(@NonNull DatabaseError databaseError) {

                   }
               });


               precio_maximo.setVisibility(View.VISIBLE);
               precio_minimo.setVisibility(View.VISIBLE);
               nombre.setVisibility(View.VISIBLE);
               buscar.setVisibility(View.VISIBLE);
               categoria.setVisibility(View.VISIBLE);


           }
       });


        adapter=new AdaptadorProductos(items);

        lista_productos.setHasFixedSize(true);
        lista_productos.setLayoutManager(new GridLayoutManager(getContext(), 3, GridLayoutManager.VERTICAL, false));
        lista_productos.setAdapter(adapter);
        int largePadding = 16;
        int smallPadding = 16;
        lista_productos.addItemDecoration(new ProductGridItemDecoration(largePadding, smallPadding));

       return v;
    }


    public void onButtonPressed(String TAG, Object data) {
        if (mListener != null) {
            mListener.onFragmentMessage(TAG,data);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void comprobarNocheFragment (){
        SharedPreferences modonoche = getActivity().getSharedPreferences("noche", Context.MODE_PRIVATE);
        Boolean modo_actual= modonoche.getBoolean("noche",false);
        if (modo_actual==true){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            fondo.setBackgroundResource(R.drawable.fondo_oscuro_fragment);
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            fondo.setBackgroundResource(R.drawable.fondo_claro_fragment);

        }
    }

    public void cargarCategorias(){

        ref.child("tienda").child("categorias").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChildren()){

                    int i=0;
                    int tamaño = (int) dataSnapshot.getChildrenCount();
                    lista_categorias = new String[tamaño];

                    for(DataSnapshot hijo:dataSnapshot.getChildren()) {

                        String heroe = hijo.getValue(String.class);
                        lista_categorias[i] = heroe;
                        i++;

                    }


                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, lista_categorias);
                    categoria.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

}
