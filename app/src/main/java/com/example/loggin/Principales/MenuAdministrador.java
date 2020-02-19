package com.example.loggin.Principales;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
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
import com.example.loggin.Objetos.Reserva;
import com.example.loggin.Objetos.Tienda;
import com.example.loggin.OnFragmentInteractionListener;
import com.example.loggin.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.ChildEventListener;
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
    private NotificationManager mNotificationManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu__administrador);

        ref = FirebaseDatabase.getInstance().getReference();
        mNotificationManager=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        //notificaciones
        ref.child("tienda").child("reservas").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Reserva reserva = dataSnapshot.getValue(Reserva.class);
                String id_reserva = dataSnapshot.getKey();

                SharedPreferences credenciales = getSharedPreferences("datos_usuario", Context.MODE_PRIVATE);
                final Boolean admin= credenciales.getBoolean("admin",false);

                if(admin){

                    if (reserva.getEstado()==Reserva.RECIBIDO && !reserva.isEstado_notificado()){
                        ref.child("tienda").child("reservas").child(id_reserva).child("estado_notificado").setValue(true);
                        notificar(reserva.getId_producto(),reserva.getNombre_producto(),"Pedido recibido",FragmentReservas.class);
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });














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


    public void notificar(String id_pedido,String descripcion,String estado,Class destino){
        //Creamos notificación
        //Creamos un metodo con 3 parametros, mensaje, texto y activity destino
        Notification.Builder mBuilder = new Notification.Builder(getApplicationContext());

        //Añadimos icono a la notificación (icono de nuestra app normalmente)
        mBuilder.setSmallIcon(android.R.drawable.stat_notify_chat);


        //Añadimos título a la notificación
        mBuilder.setContentTitle(descripcion);

        //Añadimos texto a la notificación
        mBuilder.setContentText(estado);

        //Añadimos imagen a la notificación, pero tenemos que convertirla a Bitmap
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.icono_recibido);
        mBuilder.setLargeIcon(bmp);

        //Para hacer desaparecer la notificación cuando se pulse sobre esta y se abra la Activity de destino
        mBuilder.setAutoCancel(true);

        //Sonido notificación por defecto
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(alarmSound);

        //Para que vibre el dispositivo
        mBuilder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });

        //Para barra de progreso (true en movimiento y false con el estado en que se encuentra el progreso)
        mBuilder.setProgress(100,50,true);


        //Abrimos una activity al pulsar sobra la notificación
        //Creamos Intent
        Intent notIntent = new Intent(getApplicationContext(), destino);
        //Creamos PendingIntent al cual le pasamos nuestro Intent
        PendingIntent contIntent = PendingIntent.getActivity(getApplicationContext()  , 0,notIntent,0);
        //Añadimos nuestra PendingIntent a la notificación
        mBuilder.setContentIntent(contIntent);
        //Lanzamos la notificación

        mNotificationManager.notify(id_pedido.hashCode(), mBuilder.build());
    }


}
