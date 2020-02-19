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
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.DialogFragment;

import com.example.loggin.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class DialogoAdmin extends DialogFragment {

    private TextView contraseña_actual;
    private EditText contraseña_nueva;
    private Button modo_noche;
    private LinearLayout fondo;
    private Button cancelar, aceptar;
    private DatabaseReference ref;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialogo_admin, container, false);

        ref = FirebaseDatabase.getInstance().getReference();

        modo_noche = (Button) v.findViewById(R.id.modoNoche);
        contraseña_nueva = (EditText) v.findViewById(R.id.contraseña_nueva);
        contraseña_actual = (TextView) v.findViewById(R.id.contraseña_actual);
        cancelar = (Button) v.findViewById(R.id.cancelar);
        fondo = (LinearLayout) v.findViewById(R.id.fondo);
        aceptar = (Button) v.findViewById(R.id.aceptar);

        comprobarNocheFragment();

        ref.child("tienda").child("administrador").child("contraseña").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                contraseña_actual.setText(dataSnapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        modo_noche.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (AppCompatDelegate.getDefaultNightMode()==AppCompatDelegate.MODE_NIGHT_NO){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

                    Boolean dayNight=true;
                    guardarPreferencias(dayNight);

                }else{
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

                    Boolean dayNight=false;
                    guardarPreferencias(dayNight);

                }

            }
        });


        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });


        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String valor_contraseña_nueva= contraseña_nueva.getText().toString();

                System.out.println(valor_contraseña_nueva);

                if (valor_contraseña_nueva.equals("")){

                    Toast.makeText(getContext(), "No se ha modificado la contraseña", Toast.LENGTH_SHORT).show();
                    dismiss();

                }else{

                    ref.child("tienda").child("administrador").child("contraseña").setValue(contraseña_nueva.getText().toString());
                    Toast.makeText(getContext(), "Contraseña modificada con éxito", Toast.LENGTH_SHORT).show();

                    dismiss();

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
            modo_noche.setBackgroundResource(R.drawable.boton_noche_dialogo);
            aceptar.setBackgroundResource(R.drawable.boton_noche_dialogo);
        } else {
            fondo.setBackgroundResource(R.drawable.fondo_claro_fragment);
            cancelar.setBackgroundResource(R.drawable.boton_dia_dialogo);
            modo_noche.setBackgroundResource(R.drawable.boton_dia_dialogo);
            aceptar.setBackgroundResource(R.drawable.boton_dia_dialogo);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }


    public void guardarPreferencias(Boolean dayNight){
        SharedPreferences modonoche = getActivity().getSharedPreferences("noche", Context.MODE_PRIVATE);
        SharedPreferences.Editor obj_editor = modonoche.edit();
        if (dayNight==true){
            obj_editor.putBoolean("noche",true);
            obj_editor.commit();
        }else{
            obj_editor.putBoolean("noche",false);
            obj_editor.commit();
        }

    }
}
