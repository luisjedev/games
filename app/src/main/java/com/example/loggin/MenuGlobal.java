package com.example.loggin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

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

public class MenuGlobal extends AppCompatActivity implements OnFragmentInteractionListener{

    private BottomNavigationView menuBottom;
    private MaterialToolbar topbar;
    private int posicionAnimacion;
    private FrameLayout fondo;
    private DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_global);

        ref = FirebaseDatabase.getInstance().getReference();

        guardarTienda();

        fondo = (FrameLayout) findViewById(R.id.frame_fragments);

        topbar = (MaterialToolbar) findViewById(R.id.materialToolbar);
        menuBottom =(BottomNavigationView)findViewById(R.id.bottom_navigation);

        SharedPreferences credenciales = getSharedPreferences("datos_usuario", Context.MODE_PRIVATE);
        Boolean admin= credenciales.getBoolean("admin",false);

        if (admin){
            topbar.inflateMenu(R.menu.top_menu_admin);
        }else {
            topbar.inflateMenu(R.menu.top_menu);
        }

        posicionAnimacion = 0;
        FragmentProductos frag = new FragmentProductos();
        frag.setArguments(getIntent().getExtras());
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.enter_left_to_right, R.anim.exit_left_to_right)
                .replace(R.id.frame_fragments, frag)
                .addToBackStack(null)
                .commit();

        comprobarNoche();

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

                    case R.id.administracion:
                        Intent intent = new Intent(getApplicationContext(), MenuAdministrador.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.enter_right_to_left, R.anim.exit_right_to_left);
                        break;

                    default:
                }
                return false;
            }
        });
        menuBottom.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.productos:

                        if (posicionAnimacion!=0) {

                            posicionAnimacion = 0;
                            FragmentProductos frag = new FragmentProductos();
                            frag.setArguments(getIntent().getExtras());
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .setCustomAnimations(R.anim.enter_left_to_right, R.anim.exit_left_to_right)
                                    .replace(R.id.frame_fragments, frag)
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
                                    .replace(R.id.frame_fragments, frag)
                                    .addToBackStack(null)
                                    .commit();

                        }else if (posicionAnimacion>1){

                            posicionAnimacion = 1;
                            FragmentProductos frag = new FragmentProductos();
                            frag.setArguments(getIntent().getExtras());
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .setCustomAnimations(R.anim.enter_left_to_right, R.anim.exit_left_to_right)
                                    .replace(R.id.frame_fragments, frag)
                                    .addToBackStack(null)
                                    .commit();
                        }

                        break;
                    case R.id.buscar:

                        if (posicionAnimacion<2){

                            posicionAnimacion = 2;
                            FragmentProductos frag = new FragmentProductos();
                            frag.setArguments(getIntent().getExtras());
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .setCustomAnimations(R.anim.enter_right_to_left, R.anim.exit_right_to_left)
                                    .replace(R.id.frame_fragments, frag)
                                    .addToBackStack(null)
                                    .commit();

                        }else if (posicionAnimacion>2){

                            posicionAnimacion = 2;
                            FragmentProductos frag = new FragmentProductos();
                            frag.setArguments(getIntent().getExtras());
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .setCustomAnimations(R.anim.enter_left_to_right, R.anim.exit_left_to_right)
                                    .replace(R.id.frame_fragments, frag)
                                    .addToBackStack(null)
                                    .commit();
                        }

                        break;
                    case R.id.favoritos:

                        if (posicionAnimacion<3){

                            posicionAnimacion = 3;
                            FragmentProductos frag = new FragmentProductos();
                            frag.setArguments(getIntent().getExtras());
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .setCustomAnimations(R.anim.enter_right_to_left, R.anim.exit_right_to_left)
                                    .replace(R.id.frame_fragments, frag)
                                    .addToBackStack(null)
                                    .commit();

                        }else if (posicionAnimacion>3){

                            posicionAnimacion = 3;
                            FragmentProductos frag = new FragmentProductos();
                            frag.setArguments(getIntent().getExtras());
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .setCustomAnimations(R.anim.enter_left_to_right, R.anim.exit_left_to_right)
                                    .replace(R.id.frame_fragments, frag)
                                    .addToBackStack(null)
                                    .commit();
                        }

                        break;

                    case R.id.tienda:

                        if (posicionAnimacion<4){

                            posicionAnimacion = 4;
                            FragmentProductos frag = new FragmentProductos();
                            frag.setArguments(getIntent().getExtras());
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .setCustomAnimations(R.anim.enter_right_to_left, R.anim.exit_right_to_left)
                                    .replace(R.id.frame_fragments, frag)
                                    .addToBackStack(null)
                                    .commit();

                        }else if (posicionAnimacion>4){

                            posicionAnimacion = 4;
                            FragmentProductos frag = new FragmentProductos();
                            frag.setArguments(getIntent().getExtras());
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .setCustomAnimations(R.anim.enter_left_to_right, R.anim.exit_left_to_right)
                                    .replace(R.id.frame_fragments, frag)
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

    @Override
    public void onFragmentMessage(String TAG, Object data) {
        if (TAG.equals("opcion")){
//            nameRe=data.toString();

        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public boolean comprobarNoche(){

        boolean res;

        SharedPreferences modonoche = getSharedPreferences("noche", Context.MODE_PRIVATE);
        Boolean modo_actual= modonoche.getBoolean("noche",false);

        if (modo_actual==true){
            res=true;
            fondo.setBackgroundResource(R.drawable.fondo_oscuro_fragment);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else{
            fondo.setBackgroundResource(R.drawable.fondo_claro_fragment);
            res=false;
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        return res;
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
