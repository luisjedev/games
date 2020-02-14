package com.example.loggin;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;


public class FragmentProductos extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private StorageReference sto;
    private DatabaseReference ref;
    private ArrayList<Producto> items;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FrameLayout fondo;
    private RecyclerView lista_productos;
    private AdaptadorProductos adapter;

    private OnFragmentInteractionListener mListener;

    public FragmentProductos() {
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
        View v = inflater.inflate(R.layout.fragment_fragment_productos, container, false);

        fondo=(FrameLayout) v.findViewById(R.id.fondofrag);
        lista_productos = (RecyclerView) v.findViewById(R.id.lista_productos);
        items=new ArrayList<>();

        ref= FirebaseDatabase.getInstance().getReference();
        sto= FirebaseStorage.getInstance().getReference();


        ref.child("tienda").child("productos").orderByChild("nombre").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                items.clear();
                for(DataSnapshot hijo:dataSnapshot.getChildren()) {
                    final Producto producto = hijo.getValue(Producto.class);
                    producto.setId(hijo.getKey());
                    if (producto.isDisponible()){
                        items.add(producto);
                    }
                }

                for(final Producto producto:items){
                    sto.child("tienda")
                            .child("productos")
                            .child("imagenes")
                            .child(producto.getId())
                            .getDownloadUrl()
                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    producto.setFoto_url(uri);
                                    adapter.notifyDataSetChanged();
                                }
                            });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        adapter=new AdaptadorProductos(items);

        lista_productos.setHasFixedSize(true);
        lista_productos.setLayoutManager(new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false));
        lista_productos.setAdapter(adapter);
        int largePadding = 16;
        int smallPadding = 16;
        lista_productos.addItemDecoration(new ProductGridItemDecoration(largePadding, smallPadding));

       comprobarNocheFragment();
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
        }else{
            fondo.setBackgroundResource(R.drawable.fondo_oscuro_fragment);
        }
    }

}
