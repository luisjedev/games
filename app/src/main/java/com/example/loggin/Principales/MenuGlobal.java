package com.example.loggin.Principales;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.loggin.Fragments.FragmentMapa;
import com.example.loggin.Fragments.FragmentProductos;
import com.example.loggin.Fragments.FragmentReservas;
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

import org.json.JSONException;
import org.json.JSONObject;

public class MenuGlobal extends AppCompatActivity implements OnFragmentInteractionListener {

    private BottomNavigationView menuBottom;
    private MaterialToolbar topbar;
    private int posicionAnimacion;
    private FrameLayout fondo;
    private String ENVIADO="Pedido enviado";
    private String PREPARADO="Pedido preparado";
    private DatabaseReference ref;
    private String[] lista_categorias;
    private RequestQueue mQueue;
    private NotificationManager mNotificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_global);

        SharedPreferences id_usuario = getSharedPreferences("datos_usuario", Context.MODE_PRIVATE);
        String dueño_actual = id_usuario.getString("id_usuario", "");

        mNotificationManager=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);


        ref = FirebaseDatabase.getInstance().getReference();

        //notificaciones

        ref.child("tienda").child("reservas").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                //PREGUNTAR A DANI POR QUE ESTA MIERDA PARA QUE FUNCIONE EN UN MISMO MOVIL ADMIN/USER

                SharedPreferences credenciales = getSharedPreferences("datos_usuario", Context.MODE_PRIVATE);
                String dueño_actual = credenciales.getString("id_usuario", "");

                Reserva reserva = dataSnapshot.getValue(Reserva.class);
                String id_reserva = dataSnapshot.getKey();
                String id_cliente_reserva = reserva.getId_cliente();
                System.out.println("id reserva: " + id_reserva);
                System.out.println("id cliente: " + id_cliente_reserva);
                System.out.println("dueño actual: " + dueño_actual);

//                Toast.makeText(MenuGlobal.this, "dueño actual: "+dueño_actual+"id_c_res: "+id_cliente_reserva, Toast.LENGTH_SHORT).show();

                if (id_cliente_reserva.equals(dueño_actual) && reserva.getEstado() == Reserva.PREPARADO && !reserva.isEstado_notificado()) {
                    ref.child("tienda").child("reservas").child(id_reserva).child("estado_notificado").setValue(true);
                    notificar(reserva.getId_producto(), reserva.getNombre_producto(), PREPARADO, MenuGlobal.class,0);

                } else if (id_cliente_reserva.equals(dueño_actual) && reserva.getEstado() == Reserva.ENVIADO && !reserva.isEstado_notificado()) {
                    ref.child("tienda").child("reservas").child(id_reserva).child("estado_notificado").setValue(true);
                    notificar(reserva.getId_producto(), reserva.getNombre_producto(), ENVIADO, MenuGlobal.class,1);

                }


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                SharedPreferences credenciales = getSharedPreferences("datos_usuario", Context.MODE_PRIVATE);
                String dueño_actual = credenciales.getString("id_usuario", "");

                Reserva reserva = dataSnapshot.getValue(Reserva.class);
                String id_reserva = dataSnapshot.getKey();
                String id_cliente_reserva = reserva.getId_cliente();
                System.out.println("id reserva: " + id_reserva);
                System.out.println("id cliente: " + id_cliente_reserva);
                System.out.println("dueño actual: " + dueño_actual);

//                Toast.makeText(MenuGlobal.this, "dueño actual: "+dueño_actual+"id_c_res: "+id_cliente_reserva, Toast.LENGTH_SHORT).show();

                if (id_cliente_reserva.equals(dueño_actual) && reserva.getEstado() == Reserva.PREPARADO && !reserva.isEstado_notificado()) {
                    ref.child("tienda").child("reservas").child(id_reserva).child("estado_notificado").setValue(true);
                    notificar(reserva.getId_producto(), reserva.getNombre_producto(), PREPARADO, MenuGlobal.class,0);

                } else if (id_cliente_reserva.equals(dueño_actual) && reserva.getEstado() == Reserva.ENVIADO && !reserva.isEstado_notificado()) {
                    ref.child("tienda").child("reservas").child(id_reserva).child("estado_notificado").setValue(true);
                    notificar(reserva.getId_producto(), reserva.getNombre_producto(), ENVIADO, MenuGlobal.class,1);

                }
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








        mQueue = Volley.newRequestQueue(this);

        cargarCategorias();
        leerValoresMonedas();

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


        String menuFragment = getIntent().getStringExtra("menuFragment");
        if (menuFragment!=null && menuFragment.equals("FragmentReservas")){
            posicionAnimacion = 1;
            FragmentReservas frag = new FragmentReservas();
            frag.setArguments(getIntent().getExtras());
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                    .replace(R.id.frame_fragments, frag)
                    .addToBackStack(null)
                    .commit();
        }else {

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
                        Intent i = new Intent(getApplicationContext(), Login.class);
                        startActivity(i);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;

                    case R.id.configuracion:
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
                            FragmentReservas frag = new FragmentReservas();
                            frag.setArguments(getIntent().getExtras());
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .setCustomAnimations(R.anim.enter_right_to_left, R.anim.exit_right_to_left)
                                    .replace(R.id.frame_fragments, frag)
                                    .addToBackStack(null)
                                    .commit();

                        }else if (posicionAnimacion>1){

                            posicionAnimacion = 1;
                            FragmentReservas frag = new FragmentReservas();
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

                        if (posicionAnimacion<2){

                            posicionAnimacion = 2;
                            FragmentMapa frag = new FragmentMapa();
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

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void leerValoresMonedas(){
        String url_monedas = "https://api.exchangeratesapi.io/latest?symbols=USD,GBP";

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET, url_monedas, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {
                    SharedPreferences monedas = getSharedPreferences("valor_moneda", Context.MODE_PRIVATE);
                    SharedPreferences.Editor obj_editor = monedas.edit();

                    JSONObject respuestaAPI=response.getJSONObject("rates");

                   String valor_dolar_euro=respuestaAPI.getString("USD");
                   String valor_libra_euro= respuestaAPI.getString("GBP");

                   obj_editor.putString("dolar",valor_dolar_euro);
                   obj_editor.putString("libra",valor_libra_euro);
                   obj_editor.commit();

                    System.out.println(valor_dolar_euro+" dolares + Euros: "+valor_libra_euro);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }
        );
        mQueue.add(jsonObjectRequest);
    }


    public void notificar(String id_pedido, String descripcion, String estado, Class destino, int foto){
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

        if (foto==0){
            Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.icono_preparado);
            mBuilder.setLargeIcon(bmp);
        }else{

            Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.icono_enviado);
            mBuilder.setLargeIcon(bmp);
        }


        //Para hacer desaparecer la notificación cuando se pulse sobre esta y se abra la Activity de destino
        mBuilder.setAutoCancel(true);

        //Sonido notificación por defecto
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(alarmSound);

        //Para que vibre el dispositivo
        mBuilder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });

        //Para barra de progreso (true en movimiento y false con el estado en que se encuentra el progreso)
//        mBuilder.setProgress(100,50,true);




        //Abrimos una activity al pulsar sobra la notificación
        //Creamos Intent
        Intent notIntent = new Intent(getApplicationContext(), destino);
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        notIntent.putExtra("menuFragment", "FragmentReservas");

        //Creamos PendingIntent al cual le pasamos nuestro Intent
        PendingIntent contIntent = PendingIntent.getActivity(getApplicationContext()  , 0,notIntent,0);
        //Añadimos nuestra PendingIntent a la notificación
        mBuilder.setContentIntent(contIntent);
        //Lanzamos la notificación

        mNotificationManager.notify(id_pedido.hashCode(), mBuilder.build());

    }

}
