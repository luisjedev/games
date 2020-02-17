package com.example.loggin.Dialogos;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.DialogFragment;

import com.example.loggin.R;
import com.google.firebase.database.DatabaseReference;

public class DialogoEstado extends DialogFragment {

    private Button cancelar, aceptar;
    private LinearLayout fondo,fondo_botones;
    private DatabaseReference ref;
    private RadioButton recibido, preparado, enviado;
    private RadioGroup botones;


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialogo_estado_pedido, container, false);


        cancelar = (Button) v.findViewById(R.id.cancelar);
        aceptar = (Button) v.findViewById(R.id.aceptar);
        fondo = (LinearLayout) v.findViewById(R.id.fondo);
        fondo_botones = (RadioGroup) v.findViewById(R.id.fondo_botones);
        recibido = (RadioButton) v.findViewById(R.id.recibido);
        preparado = (RadioButton) v.findViewById(R.id.preparado);
        enviado = (RadioButton) v.findViewById(R.id.enviado);


        comprobarNocheFragment();

        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });


        aceptar.setOnClickListener(new View.OnClickListener() {
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