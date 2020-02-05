package com.example.loggin;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.DialogFragment;

public class dialogoCategoria extends DialogFragment {

    private EditText categoria;
    private Button cancelar, aceptar;
    private LinearLayout fondo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialogo_categorias, container, false);

        categoria = (EditText) v.findViewById(R.id.añadir_categoria);
        cancelar = (Button) v.findViewById(R.id.cancelar);
        fondo = (LinearLayout) v.findViewById(R.id.fondo);
        aceptar = (Button) v.findViewById(R.id.añadir);


        comprobarNocheFragment();


        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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




}
