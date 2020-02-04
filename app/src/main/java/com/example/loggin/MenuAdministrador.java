package com.example.loggin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TableLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MenuAdministrador extends AppCompatActivity implements OnFragmentInteractionListener {

    private ConstraintLayout fondo;
    private FrameLayout fragments;
    private BottomNavigationView menu_admin;
    private int posicionAnimacion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu__administrador);

        fondo = (ConstraintLayout) findViewById(R.id.fondo);
        fragments = (FrameLayout) findViewById(R.id.fragments_admin);
        menu_admin = (BottomNavigationView) findViewById(R.id.menuadmin);

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

        menu_admin.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.productos:

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
                            FragmentProductos frag = new FragmentProductos();
                            frag.setArguments(getIntent().getExtras());
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .setCustomAnimations(R.anim.enter_right_to_left, R.anim.exit_right_to_left)
                                    .replace(R.id.fragments_admin, frag)
                                    .addToBackStack(null)
                                    .commit();

                        }else if (posicionAnimacion>1){

                            posicionAnimacion = 1;
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
                    case R.id.clientes:

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
                            FragmentProductos frag = new FragmentProductos();
                            frag.setArguments(getIntent().getExtras());
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .setCustomAnimations(R.anim.enter_right_to_left, R.anim.exit_right_to_left)
                                    .replace(R.id.fragments_admin, frag)
                                    .addToBackStack(null)
                                    .commit();

                        }else if (posicionAnimacion>3){

                            posicionAnimacion = 3;
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

                    case R.id.volver:

                        Intent i = new Intent(getApplicationContext(),MenuGlobal.class);
                        startActivity(i);
                        overridePendingTransition(R.anim.enter_left_to_right, R.anim.exit_left_to_right);

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
//            nameRe=data.toString();

        }
    }
}
