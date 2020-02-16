package com.example.loggin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;


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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu__administrador);



        fondo = (ConstraintLayout) findViewById(R.id.fondo);
        fragments = (FrameLayout) findViewById(R.id.fragments_admin);
        menu_admin = (BottomNavigationView) findViewById(R.id.menuadmin);
        topbar = (MaterialToolbar) findViewById(R.id.materialToolbar);

        comprobarNoche();
        if (comprobarNoche()){
            fondo.setBackgroundResource(R.drawable.fondo_oscuro_fragment);
        }


        posicionAnimacion = 0;
        AgregarProductos frag = new AgregarProductos();
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
                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(i);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;

                    case R.id.configuracion:
//                        Intent intent = new Intent(getApplicationContext(), MenuAdministrador.class);
//                        startActivity(intent);
//                        overridePendingTransition(R.anim.enter_right_to_left, R.anim.exit_right_to_left);
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
                            AgregarProductos frag = new AgregarProductos();
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
                            EditarMapa frag = new EditarMapa();
                            frag.setArguments(getIntent().getExtras());
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .setCustomAnimations(R.anim.enter_right_to_left, R.anim.exit_right_to_left)
                                    .replace(R.id.fragments_admin, frag)
                                    .addToBackStack(null)
                                    .commit();

                        }else if (posicionAnimacion>3){

                            posicionAnimacion = 3;
                            EditarMapa frag = new EditarMapa();
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

                            posicionAnimacion = 4;
                            FragmentUsuarios frag = new FragmentUsuarios();
                            frag.setArguments(getIntent().getExtras());
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .setCustomAnimations(R.anim.enter_right_to_left, R.anim.exit_right_to_left)
                                    .replace(R.id.fragments_admin, frag)
                                    .addToBackStack(null)
                                    .commit();

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



}
