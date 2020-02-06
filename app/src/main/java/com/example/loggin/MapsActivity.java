package com.example.loggin;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnCircleClickListener {
    public static final int CODE_LOC_PERMISSION = 0;
    public static final int PLACE_PICKER_REQUEST = 1;

    MapView mMapView;
    GoogleMap mGoogleMap;
    Button mBtnPlaces;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);



        //Todo 1. Conectamos el MapView
        mMapView = (MapView) findViewById(R.id.mapView);

        //Todo 2. Tenemos que implementar el ciclo de vida propio del map.
        mMapView.onCreate(savedInstanceState);

        //Todo 3.  Implementamos la interfaz 'OnMapReadyCallback' y la asignamos a la vista
        mMapView.getMapAsync(this);


        mBtnPlaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activateApiPlaces();
            }
        });

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mGoogleMap = googleMap;

        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(0, 0))
                .title("Marker"));

        applyMapStyle();

        applyUiSettings();

        drawCircle();

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

        Toast.makeText(this, "Aqui deberiamos acceder al GPS...", Toast.LENGTH_SHORT).show();

        controlCamera(new LatLng(36.5297800,-6.2946500));

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
                .center(new LatLng(36.5297800, -6.2946500))
                .radius(1000)   // In meters
                .strokeColor(Color.GREEN);

        Circle circle = mGoogleMap.addCircle(circleOptions);

        //Todo -> Podemos habilitar clicks en los circulos
        circle.setClickable(true);

        mGoogleMap.setOnCircleClickListener(this);


    }


    @Override
    public void onCircleClick(Circle circle) {
        Toast.makeText(this, "Click en circulo", Toast.LENGTH_SHORT).show();
    }


    //TODO API PLACE 1. Debemos insertar 'com.google.android.gms:play-services-places' a gradle.
    //todo -> Creamos un intentbuilder para comunicarnos con la api de google y que sea a traves de
    // todo -> google maps que busquemos lugares.
    public void activateApiPlaces(){

        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);

        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    //TODO API PLACES 2. Recogemos el resultado de la respuesta de la busqueda del lugar
    //todo -> que podemos manejarla a traves del intent
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                String toastMsg = String.format("Place: %s", place.getName());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();

                mGoogleMap.addMarker(new MarkerOptions().position(place.getLatLng()));
                controlCamera(place.getLatLng());

            }
        }
    }


}
