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

import com.example.loggin.Adaptadores.AdaptadorUsuarios;
import com.example.loggin.Objetos.Cliente;
import com.example.loggin.OnFragmentInteractionListener;
import com.example.loggin.ProductGridItemDecoration;
import com.example.loggin.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;


public class FragmentUsuarios extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private StorageReference sto;
    private DatabaseReference ref;
    private ArrayList<Cliente> items;
    private FrameLayout fondo;
    private RecyclerView lista_usuarios;
    private AdaptadorUsuarios adapter;

    private OnFragmentInteractionListener mListener;

    public FragmentUsuarios() {

    }

    public static FragmentUsuarios newInstance(String param1, String param2) {
        FragmentUsuarios fragment = new FragmentUsuarios();
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
        View v = inflater.inflate(R.layout.fragment_fragment_usuarios, container, false);

        fondo=(FrameLayout) v.findViewById(R.id.fondofrag);
        lista_usuarios = (RecyclerView) v.findViewById(R.id.lista_usuarios);
        items=new ArrayList<>();

        ref= FirebaseDatabase.getInstance().getReference();
        sto= FirebaseStorage.getInstance().getReference();

        ref.child("tienda").child("clientes").orderByChild("nombre").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                items.clear();
                for(DataSnapshot hijo:dataSnapshot.getChildren()) {
                    final Cliente cliente = hijo.getValue(Cliente.class);
                    cliente.setId(hijo.getKey());
                    items.add(cliente);
                }
                for(final Cliente cliente:items){
                    sto.child("tienda")
                            .child("clientes")
                            .child("imagenes")
                            .child(cliente.getId())
                            .getDownloadUrl()
                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    cliente.setUrl_foto(uri);
                                    adapter.notifyDataSetChanged();
                                }
                            });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        adapter=new AdaptadorUsuarios(items);

        lista_usuarios.setHasFixedSize(true);
        lista_usuarios.setLayoutManager(new GridLayoutManager(getContext(), 1, GridLayoutManager.VERTICAL, false));
        lista_usuarios.setAdapter(adapter);
        int largePadding = 16;
        int smallPadding = 16;
        lista_usuarios.addItemDecoration(new ProductGridItemDecoration(largePadding, smallPadding));

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
