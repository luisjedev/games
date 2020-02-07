package com.example.loggin;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.SQLOutput;

public class dialogoCategoria extends DialogFragment {

    private EditText categoria;
    private Button cancelar, aceptar;
    private LinearLayout fondo;
    private DatabaseReference ref;
    private String[] lista_categorias;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialogo_categorias, container, false);

        categoria = (EditText) v.findViewById(R.id.añadir_categoria);
        cancelar = (Button) v.findViewById(R.id.cancelar);
        fondo = (LinearLayout) v.findViewById(R.id.fondo);
        aceptar = (Button) v.findViewById(R.id.añadir);

        ref = FirebaseDatabase.getInstance().getReference();


        comprobarNocheFragment();

        cargarCategorias();

        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });


        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (categoria.getText().toString().equals("")){
                    Toast.makeText(getContext(), "Nombra la categoría", Toast.LENGTH_SHORT).show();
                }else{

                boolean existe = false;

                for(int i = 0; i < lista_categorias.length; i++){
                    if (lista_categorias[i].equals(categoria.getText().toString())) {
                        System.out.println(lista_categorias[i]);
                        existe = true;
                    }
                }

                System.out.println(existe);

                if (existe) {
                    Toast.makeText(getContext(), "Ya existe la categoría", Toast.LENGTH_SHORT).show();
                } else {


                    ref.child("tienda").child("categorias").push().setValue(categoria.getText().toString());
                    dismiss();

                   }
                }

            }
        });


        return v;
    }


    public void comprobarNocheFragment() {
        SharedPreferences modonoche = getActivity().getSharedPreferences("noche", Context.MODE_PRIVATE);
        Boolean modo_actual = modonoche.getBoolean("noche", false);
        if (modo_actual == true) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            fondo.setBackgroundResource(R.drawable.fondo_oscuro_fragment);
            cancelar.setBackgroundResource(R.drawable.boton_noche_dialogo);
            aceptar.setBackgroundResource(R.drawable.boton_noche_dialogo);
        } else {
            fondo.setBackgroundResource(R.drawable.fondo_claro_fragment);
            cancelar.setBackgroundResource(R.drawable.boton_dia_dialogo);
            aceptar.setBackgroundResource(R.drawable.boton_dia_dialogo);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    public void cargarCategorias() {

        ref.child("tienda").child("categorias").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChildren()) {

                    int i = 0;
                    int tamaño = (int) dataSnapshot.getChildrenCount();
                    lista_categorias = new String[tamaño];

                    for (DataSnapshot hijo : dataSnapshot.getChildren()) {

                        String heroe = hijo.getValue(String.class);
                        lista_categorias[i] = heroe;
                        i++;

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

}
