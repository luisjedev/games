package com.example.loggin;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.EventListener;


public class EditarMapa extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public static final int CODE_LOC_PERMISSION = 0;
    public static final int PLACE_PICKER_REQUEST = 1;

    MapView mMapView;
    GoogleMap mGoogleMap;
    private ScrollView fondo;
    private Button modificar;
    private Double latitud_valor,longitud_valor;
    private DatabaseReference ref;


    private String mParam1;
    private String mParam2;

    private EditText latitud,longitud,nombre,direccion,email,telefono;

    private OnFragmentInteractionListener mListener;

    public EditarMapa() {

    }

    public static EditarMapa newInstance(String param1, String param2) {
        EditarMapa fragment = new EditarMapa();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.activity_maps, container, false);

        SharedPreferences credenciales = getActivity().getSharedPreferences("datos_usuario", Context.MODE_PRIVATE);
        Boolean admin= credenciales.getBoolean("admin",false);




        fondo = (ScrollView) v.findViewById(R.id.fondo);
        modificar = (Button) v.findViewById(R.id.modificar_tienda);

        nombre = (EditText) v.findViewById(R.id.nombre);
        latitud = (EditText) v.findViewById(R.id.latitud);
        longitud = (EditText) v.findViewById(R.id.longitud);
        email = (EditText) v.findViewById(R.id.email);
        telefono = (EditText) v.findViewById(R.id.telefono);
        direccion = (EditText) v.findViewById(R.id.direccion);

        ref = FirebaseDatabase.getInstance().getReference();

        verDatosTienda();

        if (!admin){
            latitud.setVisibility(View.GONE);
            longitud.setVisibility(View.GONE);
            nombre.setFocusable(false);
            email.setFocusable(false);
            telefono.setFocusable(false);
            direccion.setFocusable(false);
            modificar.setVisibility(View.GONE);
        }


        //Todo 1. Conectamos el MapView
        mMapView = (MapView) v.findViewById(R.id.mapView);

        //Todo 2. Tenemos que implementar el ciclo de vida propio del map.
        mMapView.onCreate(savedInstanceState);

        //Todo 3.  Implementamos la interfaz 'OnMapReadyCallback' y la asignamos a la vista
        mMapView.getMapAsync(this);


        comprobarNocheFragment();


        modificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String valor_nombre=nombre.getText().toString();
                final String valor_direccion=direccion.getText().toString();
                final String valor_email=email.getText().toString();
                final String valor_telefono=telefono.getText().toString();
                final String valor_latitud = latitud.getText().toString();
                final String valor_longitud = longitud.getText().toString();

                if (valor_email.equals("") || valor_direccion.equals("") || valor_nombre.equals("") || valor_telefono.equals("")
                        || valor_latitud.equals("") || valor_longitud.equals("")) {
                    Toast.makeText(getContext(), "Completa los campos necesarios", Toast.LENGTH_LONG).show();
                } else {

                    Tienda tienda = new Tienda(valor_nombre,valor_direccion,valor_telefono,valor_email,Double.parseDouble(valor_latitud)
                    ,Double.parseDouble(valor_longitud));

                    ref.child("tienda").child("datos").setValue(tienda);

                    guardarTienda(valor_latitud,valor_longitud);

                    Toast.makeText(getContext(), "Datos de la tienda modificados", Toast.LENGTH_SHORT).show();

                }
            }
        });


        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String TAG,Object data) {
        if (mListener != null) {
            mListener.onFragmentMessage(TAG,data);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void comprobarNocheFragment (){
        SharedPreferences modonoche = getActivity().getSharedPreferences("noche", Context.MODE_PRIVATE);
        Boolean modo_actual= modonoche.getBoolean("noche",false);
        if (modo_actual==true){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            fondo.setBackgroundResource(R.drawable.fondo_oscuro_fragment);
            modificar.setBackgroundResource(R.drawable.boton_redondo);
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            fondo.setBackgroundResource(R.drawable.fondo_claro_fragment);
            modificar.setBackgroundResource(R.drawable.boton_dia_naranja);

        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        //Todo 4. Este método será llamado cuando la preparación del mapa esté listo.
        // todo -> Este proceso es asíncrono por lo que no se puede suponer su llamada.
        mGoogleMap = googleMap;

        SharedPreferences credenciales = getActivity().getSharedPreferences("tienda", Context.MODE_PRIVATE);
        String latitud = credenciales.getString("latitud","");
        String longitud = credenciales.getString("longitud","");

        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(Double.parseDouble(latitud),Double.parseDouble(longitud)))
                .title("Pxxr Gvmes"));

        applyMapStyle();
        applyUiSettings();
        controlCamera(new LatLng(Double.parseDouble(latitud),Double.parseDouble(longitud)));
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    //Todo Personalizacion 1. Podemos configurar el estilo del mapa configurando el estilo
    //todo -> a traves de la pagina https://mapstyle.withgoogle.com/
    public boolean applyMapStyle() {

        //Todo -> Los ajustes de estilo solo funciona en el tipo de mapa Normal
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        MapStyleOptions styleOptions = new MapStyleOptions(getResources()
                .getString(R.string.map_style_json));


        return mGoogleMap.setMapStyle(styleOptions);
    }

    //Todo Personalizacion 2. Podemos definir elementos para la interaccion con el mapa a traves
    // todo -> de UiSettings
    public void applyUiSettings() {
        UiSettings settings = mGoogleMap.getUiSettings();

        //Todo -> Habilitamos la opcion de zoom
        settings.setZoomControlsEnabled(true);

        //Todo -> Habilitamos la opcione de brújula (aunque esta aparecerá en ciertos momentos)
        // todo -> no puede ser forzada a aparecer
        settings.setCompassEnabled(true);

        //Todo -> Habilitar el seleccionador de niveles
        settings.setIndoorLevelPickerEnabled(true);

        //Todo -> Se puede (des)Habilitar los gestos de zoom
        settings.setZoomGesturesEnabled(true);

        //Todo -> se puede (des)habilitar el desplazamiento
        settings.setScrollGesturesEnabled(true);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        try {
            //Todo Personalización 3. El botón solo es habilitado, para capturar el evento
            //todo -> debemos implementar OnMyLocationButtonClickListener
            mGoogleMap.setOnMyLocationButtonClickListener(this);
            mGoogleMap.setMyLocationEnabled(true);
        }catch (SecurityException e){
            e.printStackTrace();
        }

    }

    //Todo Personalizacion 3.1 En cada pulsación podemos capturar este evento sobrecargando
    //todo -> este metodo que no devuelve nada, para ello deberemos acceder al GPS.
    @Override
    public boolean onMyLocationButtonClick() {

        SharedPreferences credenciales = getActivity().getSharedPreferences("tienda", Context.MODE_PRIVATE);
        String latitud = credenciales.getString("latitud","");
        String longitud = credenciales.getString("longitud","");

        Toast.makeText(getContext(), "Llendo a la tienda", Toast.LENGTH_SHORT).show();

        controlCamera(new LatLng(Double.parseDouble(latitud),Double.parseDouble(longitud)));

        return true;
    }

    //Todo Personalizacion 4. Se puede cambiar la configuracion de la cámara
    public void controlCamera(LatLng latLng){

        //Todo -> Para activar edificios 3D
        mGoogleMap.setBuildingsEnabled(true);

        //Todo -> Podemos cambiar los valores de la camara accediendo a diferentes detalles
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(15); // + valor + cerca

        //ejemplo new LatLng(36.5297800,-6.2946500)
        CameraUpdate newLoc = CameraUpdateFactory.newLatLng(latLng);

        //Todo -> Se puede cambiar el zoom
        mGoogleMap.moveCamera(zoom);

        //Todo -> Se puede cambiar la posicion de la camara
        mGoogleMap.moveCamera(newLoc);


        //Todo -> ó con animación, en el tercer parametro se puede implementar la interfaz CancellableCallback
        // todo -> donde saber cuando termina la animacion
        mGoogleMap.animateCamera(zoom,3000, null);

    }

    public void verDatosTienda(){

        ref.child("tienda").child("datos").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Tienda tienda = dataSnapshot.getValue(Tienda.class);

                nombre.setText(tienda.getNombre());
                telefono.setText(tienda.getTelefono());
                direccion.setText(tienda.getDireccion());
                latitud.setText(""+tienda.getLatitud());
                longitud.setText(""+tienda.getLongitud());
                email.setText(tienda.getCorreo());

                latitud_valor=tienda.getLatitud();
                longitud_valor=tienda.getLongitud();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void guardarTienda(String latitud_valor, String longitud_valor){

                SharedPreferences credenciales = getActivity().getSharedPreferences("tienda", Context.MODE_PRIVATE);
                SharedPreferences.Editor obj_editor = credenciales.edit();

                obj_editor.putString("latitud",""+latitud_valor);
                obj_editor.putString("longitud",""+longitud_valor);
                obj_editor.commit();

    }

}
