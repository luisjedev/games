package com.example.loggin.Principales;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;


import com.example.loggin.Dialogos.DialogoAdmin;
import com.example.loggin.Dialogos.DialogoCategoria;
import com.example.loggin.Fragments.FragmentAgregarProductos;
import com.example.loggin.Fragments.FragmentMapa;
import com.example.loggin.Fragments.FragmentProductos;
import com.example.loggin.Fragments.FragmentReservas;
import com.example.loggin.Fragments.FragmentUsuarios;
import com.example.loggin.Objetos.Tienda;
import com.example.loggin.OnFragmentInteractionListener;
import com.example.loggin.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MenuAdministrador extends AppCompatActivity implements OnFragmentInteractionListener {

    private ConstraintLayout fondo;
    private FrameLayout fragments;
    private BottomNavigationView menu_admin;
    private int posicionAnimacion;
    private MaterialToolbar topbar;
    private DatabaseReference ref;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu__administrador);

        ref = FirebaseDatabase.getInstance().getReference();
        guardarTienda();

        fondo = (ConstraintLayout) findViewById(R.id.fondo);
        fragments = (FrameLayout) findViewById(R.id.fragments_admin);
        menu_admin = (BottomNavigationView) findViewById(R.id.menuadmin);
        topbar = (MaterialToolbar) findViewById(R.id.materialToolbar);

        comprobarNoche();
        if (comprobarNoche()){
            fondo.setBackgroundResource(R.drawable.fondo_oscuro_fragment);
        }


        posicionAnimacion = 0;
        FragmentAgregarProductos frag = new FragmentAgregarProductos();
        frag.setArguments(getIntent().getExtras());
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.enter_left_to_right, R.anim.exit_left_to_right)
                .replace(R.id.fragments_admin, frag)
                .addToBackStack(null)
                .commit();

        topbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.opciones:
                        SharedPreferences credenciales = getSharedPreferences("datos_usuario", Context.MODE_PRIVATE);
                        Boolean modo_actual= credenciales.getBoolean("google",false);

                        if (modo_actual==true){
                            Intent intent = new Intent(getApplicationContext(), MenuGoogle.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.enter_right_to_left, R.anim.exit_right_to_left);
                        }else{
                            Intent intent = new Intent(getApplicationContext(), Preferencias.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.enter_right_to_left, R.anim.exit_right_to_left);
                        }

                        break;

                    case R.id.salir:
                        Intent i = new Intent(getApplicationContext(), Login.class);
                        startActivity(i);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;

                    case R.id.configuracion:

                        DialogoAdmin dia = new DialogoAdmin();
                        dia.show(getSupportFragmentManager(),"configuracion");

                        break;

                    default:
                }
                return false;
            }
        });

        menu_admin.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.agregar:

                        if (posicionAnimacion!=0) {

                            posicionAnimacion = 0;
                            FragmentAgregarProductos frag = new FragmentAgregarProductos();
                            frag.setArguments(getIntent().getExtras());
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .setCustomAnimations(R.anim.enter_left_to_right, R.anim.exit_left_to_right)
                                    .replace(R.id.fragments_admin, frag)
                                    .addToBackStack(null)
                                    .commit();
                        }
                        break;
                    case R.id.reservas:

                        if (posicionAnimacion<1){

                            posicionAnimacion = 1;
                            FragmentReservas frag = new FragmentReservas();
                            frag.setArguments(getIntent().getExtras());
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .setCustomAnimations(R.anim.enter_right_to_left, R.anim.exit_right_to_left)
                                    .replace(R.id.fragments_admin, frag)
                                    .addToBackStack(null)
                                    .commit();

                        }else if (posicionAnimacion>1){

                            posicionAnimacion = 1;
                            FragmentReservas frag = new FragmentReservas();
                            frag.setArguments(getIntent().getExtras());
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .setCustomAnimations(R.anim.enter_left_to_right, R.anim.exit_left_to_right)
                                    .replace(R.id.fragments_admin, frag)
                                    .addToBackStack(null)
                                    .commit();
                        }

                        break;
                    case R.id.productos:

                        if (posicionAnimacion<2){

                            posicionAnimacion = 2;
                            FragmentProductos frag = new FragmentProductos();
                            frag.setArguments(getIntent().getExtras());
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .setCustomAnimations(R.anim.enter_right_to_left, R.anim.exit_right_to_left)
                                    .replace(R.id.fragments_admin, frag)
                                    .addToBackStack(null)
                                    .commit();

                        }else if (posicionAnimacion>2){

                            posicionAnimacion = 2;
                            FragmentProductos frag = new FragmentProductos();
                            frag.setArguments(getIntent().getExtras());
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .setCustomAnimations(R.anim.enter_left_to_right, R.anim.exit_left_to_right)
                                    .replace(R.id.fragments_admin, frag)
                                    .addToBackStack(null)
                                    .commit();
                        }

                        break;
                    case R.id.tienda:

                        if (posicionAnimacion<3){

                            posicionAnimacion = 3;
                            FragmentMapa frag = new FragmentMapa();
                            frag.setArguments(getIntent().getExtras());
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .setCustomAnimations(R.anim.enter_right_to_left, R.anim.exit_right_to_left)
                                    .replace(R.id.fragments_admin, frag)
                                    .addToBackStack(null)
                                    .commit();

                        }else if (posicionAnimacion>3){

                            posicionAnimacion = 3;
                            FragmentMapa frag = new FragmentMapa();
                            frag.setArguments(getIntent().getExtras());
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .setCustomAnimations(R.anim.enter_left_to_right, R.anim.exit_left_to_right)
                                    .replace(R.id.fragments_admin, frag)
                                    .addToBackStack(null)
                                    .commit();
                        }

                        break;

                    case R.id.usuarios:

                        if (posicionAnimacion<4){

                            posicionAnimacion = 4;
                            FragmentUsuarios frag = new FragmentUsuarios();
                            frag.setArguments(getIntent().getExtras());
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .setCustomAnimations(R.anim.enter_right_to_left, R.anim.exit_right_to_left)
                                    .replace(R.id.fragments_admin, frag)
                                    .addToBackStack(null)
                                    .commit();
                           }
                        break;
                    default:
                }
                return true;
            }
        });
    }

    public boolean comprobarNoche(){

        boolean res;

        SharedPreferences modonoche = getSharedPreferences("noche", Context.MODE_PRIVATE);
        Boolean modo_actual= modonoche.getBoolean("noche",false);

        if (modo_actual==true){
            res=true;
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else{
            res=false;
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        return res;
    }

    @Override
    public void onFragmentMessage(String TAG, Object data) {
        if (TAG.equals("opcion")){

        }
    }

    public void guardarTienda(){

        ref.child("tienda").child("datos").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Tienda tienda = dataSnapshot.getValue(Tienda.class);

                SharedPreferences credenciales = getSharedPreferences("tienda", Context.MODE_PRIVATE);
                SharedPreferences.Editor obj_editor = credenciales.edit();

                obj_editor.putString("latitud",""+tienda.getLatitud());
                obj_editor.putString("longitud",""+tienda.getLongitud());
                obj_editor.commit();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



}
