package com.example.loggin;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.EventListener;


public class AgregarProductos extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ImageView foto_prod;
    private EditText nombre, precio, descripcion;
    private Spinner categoria;
    private Button añadir;
    private Uri foto_url;
    private String[] lista_categorias;
    private DatabaseReference ref;
    private StorageReference sto;
    private CheckBox estado_producto;
    private FrameLayout fondo;

    private OnFragmentInteractionListener mListener;

    public AgregarProductos() {
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
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_agregar_productos, container, false);

        fondo = (FrameLayout) v.findViewById(R.id.fondofrag);
        añadir = (Button) v.findViewById(R.id.button);

        comprobarNocheFragment();

        nombre = (EditText) v.findViewById(R.id.nombre_prod);
        categoria = (Spinner) v.findViewById(R.id.categoria);
        descripcion = (EditText) v.findViewById(R.id.descripcion);
        precio = (EditText) v.findViewById(R.id.precio);
        estado_producto = (CheckBox) v.findViewById(R.id.estado);
        foto_prod = (ImageView) v.findViewById(R.id.foto_prod);
        añadir = (Button) v.findViewById(R.id.button);



        ref = FirebaseDatabase.getInstance().getReference();
        sto = FirebaseStorage.getInstance().getReference();
        foto_url = null;

        cargarCategorias();

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String TAG, Object data) {
        if (mListener != null) {
            mListener.onFragmentMessage(TAG, data);
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

    public void comprobarNocheFragment() {
        SharedPreferences modonoche = getActivity().getSharedPreferences("noche", Context.MODE_PRIVATE);
        Boolean modo_actual = modonoche.getBoolean("noche", false);
        if (modo_actual == true) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            fondo.setBackgroundResource(R.drawable.fondo_oscuro_fragment);
            añadir.setBackgroundResource(R.drawable.boton_redondo);
        } else {
            fondo.setBackgroundResource(R.drawable.fondo_claro_fragment);
            añadir.setBackgroundResource(R.drawable.boton_dia_naranja);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    public void registrarProducto(View view) {

        final String valor_nombre = nombre.getText().toString();
        final String valor_descripcion = descripcion.getText().toString();
        final String valor_precio = precio.getText().toString();
        final String valor_categoria = categoria.getSelectedItem().toString();
        final boolean valor_estado = estado_producto.isChecked();
        final int disponible;

        if (valor_estado) {
            disponible = 1;
        } else {
            disponible = 0;
        }

        if (valor_nombre.equals("") || valor_descripcion.equals("") || valor_precio.equals("") || valor_categoria.equals("")) {

            Toast.makeText(getContext(), "Completa los campos necesarios", Toast.LENGTH_LONG).show();

        } else {

            ref.child("tienda").
                    child("productos").
                    orderByChild("nombre")
                    .equalTo(valor_nombre)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChildren()) {
                        Toast.makeText(getContext(), "El producto ya existe", Toast.LENGTH_LONG).show();
                    } else {

                        if (foto_url != null) {

                                Producto nuevo_cliente = new Producto();

                                String clave = ref.child("tienda").child("productos").push().getKey();

                                ref.child("tienda").child("productos").child(clave).setValue(nuevo_cliente);
                                sto.child("tienda").child("productos").child("imagenes").child(clave).putFile(foto_url);
                                Toast.makeText(getContext(), "Producto registrado con éxito", Toast.LENGTH_LONG).show();


                        } else {
                            Toast.makeText(getContext(), "No se ha seleccionado una imagen", Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
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
