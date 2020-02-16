package com.example.loggin.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.loggin.Adaptadores.AdaptadorReservas;
import com.example.loggin.OnFragmentInteractionListener;
import com.example.loggin.ProductGridItemDecoration;
import com.example.loggin.R;
import com.example.loggin.Objetos.Reserva;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;


public class FragmentReservas extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private StorageReference sto;
    private DatabaseReference ref;
    private ArrayList<Reserva> items;
    private FrameLayout fondo;
    private RecyclerView lista_reservas;
    private AdaptadorReservas adapter;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public FragmentReservas() {
    }


    // TODO: Rename and change types and number of parameters
    public static FragmentReservas newInstance(String param1, String param2) {
        FragmentReservas fragment = new FragmentReservas();
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
        View v = inflater.inflate(R.layout.fragment_fragment_reservas, container, false);

        SharedPreferences credenciales = getActivity().getSharedPreferences("datos_usuario", Context.MODE_PRIVATE);
        final String dueño_actual = credenciales.getString("id_usuario","");
        final Boolean admin= credenciales.getBoolean("admin",false);
        fondo=(FrameLayout) v.findViewById(R.id.fondofrag);
        lista_reservas = (RecyclerView) v.findViewById(R.id.lista_reservas);
        items=new ArrayList<>();

        ref= FirebaseDatabase.getInstance().getReference();
        sto= FirebaseStorage.getInstance().getReference();


        if (admin){
            ref.child("tienda").child("reservas").orderByChild("nombre_producto").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    items.clear();
                    for(DataSnapshot hijo:dataSnapshot.getChildren()) {
                        final Reserva reserva = hijo.getValue(Reserva.class);
                        reserva.setId(hijo.getKey());
                        items.add(reserva);

                    }
                    for(final Reserva reserva:items){
                        sto.child("tienda")
                                .child("productos")
                                .child("imagenes")
                                .child(reserva.getId_producto())
                                .getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        reserva.setFoto_url(uri);
                                        adapter.notifyDataSetChanged();
                                    }
                                });
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }else{

            ref.child("tienda").child("reservas").orderByChild("id_cliente").equalTo(dueño_actual).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    items.clear();
                    for(DataSnapshot hijo:dataSnapshot.getChildren()) {
                        final Reserva reserva = hijo.getValue(Reserva.class);
                        reserva.setId(hijo.getKey());
                        items.add(reserva);
                    }
                    for(final Reserva reserva:items){
                        sto.child("tienda")
                                .child("productos")
                                .child("imagenes")
                                .child(reserva.getId_producto())
                                .getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        reserva.setFoto_url(uri);
                                        adapter.notifyDataSetChanged();
                                    }
                                });
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        adapter=new AdaptadorReservas(items);

        lista_reservas.setHasFixedSize(true);
        lista_reservas.setLayoutManager(new GridLayoutManager(getContext(), 3, GridLayoutManager.VERTICAL, false));
        lista_reservas.setAdapter(adapter);
        int largePadding = 16;
        int smallPadding = 16;
        lista_reservas.addItemDecoration(new ProductGridItemDecoration(largePadding, smallPadding));

        comprobarNocheFragment();



        return v;
    }

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
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            fondo.setBackgroundResource(R.drawable.fondo_claro_fragment);

        }
    }


}
