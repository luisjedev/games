package com.example.loggin;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class EditarMapa extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnCircleClickListener  {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public static final int CODE_LOC_PERMISSION = 0;
    public static final int PLACE_PICKER_REQUEST = 1;

    MapView mMapView;
    GoogleMap mGoogleMap;
    private ScrollView fondo;
    private Button modificar;


    private String mParam1;
    private String mParam2;

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

        fondo = (ScrollView) v.findViewById(R.id.fondo);
        modificar = (Button) v.findViewById(R.id.modificar_tienda);


        //Todo 1. Conectamos el MapView
        mMapView = (MapView) v.findViewById(R.id.mapView);

        //Todo 2. Tenemos que implementar el ciclo de vida propio del map.
        mMapView.onCreate(savedInstanceState);

        //Todo 3.  Implementamos la interfaz 'OnMapReadyCallback' y la asignamos a la vista
        mMapView.getMapAsync(this);


        comprobarNocheFragment();

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

        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(37.1881714, -3.6066699))
                .title("Marker"));

        applyMapStyle();

        applyUiSettings();

        drawCircle();

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
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

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

        Toast.makeText(getContext(), "Aqui deberiamos acceder al GPS...", Toast.LENGTH_SHORT).show();

        controlCamera(new LatLng(37.1881714,-3.6066699));

        return true;
    }

    //Todo Personalizacion 4. Se puede cambiar la configuracion de la cámara
    public void controlCamera(LatLng latLng){

        //Todo -> Para activar edificios 3D
        mGoogleMap.setBuildingsEnabled(true);

        //Todo -> Podemos cambiar los valores de la camara accediendo a diferentes detalles
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(17); // + valor + cerca

        //ejemplo new LatLng(36.5297800,-6.2946500)
        CameraUpdate newLoc = CameraUpdateFactory.newLatLng(latLng);

        //Todo -> Se puede cambiar el zoom
        //mGoogleMap.moveCamera(zoom);

        //Todo -> Se puede cambiar la posicion de la camara
        mGoogleMap.moveCamera(newLoc);


        //Todo -> ó con animación, en el tercer parametro se puede implementar la interfaz CancellableCallback
        // todo -> donde saber cuando termina la animacion
        mGoogleMap.animateCamera(zoom,5000, null);

    }

    //Todo Personalización 5. Se puede dibujar figuras geometricas como lineas o circulos
    public void drawCircle(){

        //Todo -> Se dibuja un circulo con un punto central y un radio
        CircleOptions circleOptions = new CircleOptions()
                .center(new LatLng(37.1881714,-3.6066699))
                .radius(1000)   // In meters
                .strokeColor(Color.GREEN);

        Circle circle = mGoogleMap.addCircle(circleOptions);

        //Todo -> Podemos habilitar clicks en los circulos
        circle.setClickable(true);

        mGoogleMap.setOnCircleClickListener(this);


    }


    @Override
    public void onCircleClick(Circle circle) {
        Toast.makeText(getContext(), "Click en circulo", Toast.LENGTH_SHORT).show();
    }


}
