package com.example.loggin;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;


public class AgregarProductos extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private EditText nombre,precio,descripcion;
    private Spinner categoria;
    private Button añadir;
    private CheckBox estado_producto;
    private boolean disponible;
    private FrameLayout fondo;

    private OnFragmentInteractionListener mListener;

    public AgregarProductos() {
        // Required empty public constructor
    }

    public static FragmentProductos newInstance(String param1, String param2) {
        FragmentProductos fragment = new FragmentProductos();
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
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_agregar_productos, container, false);

        fondo=(FrameLayout) v.findViewById(R.id.fondofrag);
        añadir = (Button) v.findViewById(R.id.button);

        comprobarNocheFragment();

        nombre = (EditText) v.findViewById(R.id.nombre_prod);
        categoria = (Spinner) v.findViewById(R.id.categoria);
        descripcion = (EditText) v.findViewById(R.id.descripcion);
        precio = (EditText) v.findViewById(R.id.precio);
        estado_producto = (CheckBox) v.findViewById(R.id.estado);






        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String TAG, Object data) {
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
            añadir.setBackgroundResource(R.drawable.boton_redondo);
        }else{
            fondo.setBackgroundResource(R.drawable.fondo_claro_fragment);
            añadir.setBackgroundResource(R.drawable.boton_dia_naranja);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}
