package com.example.loggin.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.loggin.Objetos.Producto;
import com.example.loggin.OnFragmentInteractionListener;
import com.example.loggin.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.Activity.RESULT_OK;


public class FragmentEditarProducto extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;


    private ImageView foto_prod;
    private String currentPhotoPath;
    private Button volver;
    private Boolean camara;
    private EditText nombre, precio, descripcion;
    private Spinner categoria;
    private Button modificar,tomarfoto;
    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private final static int SELECCIONAR_FOTO = 1;
    private Uri foto_url;
    private String[] lista_categorias;
    private DatabaseReference ref;
    private TextView texto_categoria;
    private StorageReference sto;
    private CheckBox estado_producto;
    private FrameLayout fondo;

    private OnFragmentInteractionListener mListener;

    public FragmentEditarProducto() {
    }

    public static FragmentEditarProducto newInstance(String param1, String param2) {
        FragmentEditarProducto fragment = new FragmentEditarProducto();
        Bundle args = new Bundle();
        System.out.println(param1);

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

        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},1000);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_fragment_editar_producto, container, false);
        fondo = (FrameLayout) v.findViewById(R.id.fondofrag);

        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},1000);
        }

        SharedPreferences credenciales = getActivity().getSharedPreferences("datos_usuario", Context.MODE_PRIVATE);
        final Boolean admin= credenciales.getBoolean("admin",false);

        nombre = (EditText) v.findViewById(R.id.nombre_editar);
        categoria = (Spinner) v.findViewById(R.id.categoria_editar);
        descripcion = (EditText) v.findViewById(R.id.descripcion_editar);
        precio = (EditText) v.findViewById(R.id.precio_editar);
        estado_producto = (CheckBox) v.findViewById(R.id.disponible_editar);
        foto_prod = (ImageView) v.findViewById(R.id.foto_editar);
        modificar = (Button) v.findViewById(R.id.modificar);
        tomarfoto = (Button) v.findViewById(R.id.tomarfoto);
        volver = (Button) v.findViewById(R.id.volver);
        texto_categoria = (TextView) v.findViewById(R.id.texto_categoria);

        ref = FirebaseDatabase.getInstance().getReference();
        sto = FirebaseStorage.getInstance().getReference();
        foto_url = null;

        if (!admin){

            nombre.setFocusable(false);
            descripcion.setFocusable(false);
            categoria.setVisibility(View.GONE);
            precio.setFocusable(false);
            estado_producto.setClickable(false);
            foto_prod.setClickable(false);
            modificar.setVisibility(View.GONE);
            tomarfoto.setVisibility(View.GONE);
        }

        comprobarNocheFragment();
        cargarCategorias();



        volver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                FragmentProductos frag = new FragmentProductos();
                AppCompatActivity activity= (AppCompatActivity) getContext();

                if (admin){

                    activity.getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                            .replace(R.id.fragments_admin, frag)
                            .addToBackStack(null)
                            .commit();

                }else {

                    activity.getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                            .replace(R.id.frame_fragments, frag)
                            .addToBackStack(null)
                            .commit();
                }

            }
        });


        if (admin){

            foto_prod.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    camara=false;
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent, SELECCIONAR_FOTO);
                }
            });
        }




        tomarfoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camara=true;
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Ensure that there's a camera activity to handle the intent
                if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(getActivity(),
                                "com.example.loggin.fileprovider",
                                photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                        foto_url = photoURI;
                        System.out.println(foto_url);

                    }
                }
            }
        });



        //CARGAR AQUI
        ref.child("tienda").child("productos").child(mParam1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Producto producto = dataSnapshot.getValue(Producto.class);

                nombre.setText(producto.getNombre());
                descripcion.setText(producto.getDescripcion());
                precio.setText(producto.getPrecio());

                categoria.setSelection(comprobarCategoria(producto.getCategoria()));


                if (producto.isDisponible()){
                    estado_producto.setChecked(true);
                }

                if (!admin){
                    texto_categoria.setText(producto.getCategoria());
                }


                sto.child("tienda").child("productos").child("imagenes").child(mParam1).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(getContext()).load(uri).into(foto_prod);
                        foto_url=uri;

                    }
                });



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        modificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String valor_nombre = nombre.getText().toString();
                final String valor_descripcion = descripcion.getText().toString();
                final String valor_precio = precio.getText().toString();
                final String valor_categoria = categoria.getSelectedItem().toString();
                final boolean valor_estado = estado_producto.isChecked();

                if (valor_nombre.equals("") || valor_descripcion.equals("") || valor_precio.equals("") || valor_categoria.equals("")) {

                    Toast.makeText(getContext(), "Completa los campos necesarios", Toast.LENGTH_LONG).show();

                } else {

                    ref.child("tienda").
                            child("productos").
                            orderByChild("nombre")
                            .equalTo(valor_nombre)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChildren()) {
                                        Toast.makeText(getContext(), "El producto ya existe", Toast.LENGTH_LONG).show();
                                    } else {

                                        if (foto_url != null) {

                                            Producto nuevo_producto = new Producto(valor_nombre,valor_categoria,valor_descripcion,valor_precio,valor_estado);
                                            String clave = ref.child("tienda").child("productos").push().getKey();

                                            ref.child("tienda").child("productos").child(clave).setValue(nuevo_producto);
                                            sto.child("tienda").child("productos").child("imagenes").child(clave).putFile(foto_url);
                                            Toast.makeText(getContext(), "Producto registrado con éxito", Toast.LENGTH_LONG).show();


                                        } else {
                                            Toast.makeText(getContext(), "No se ha seleccionado una imagen", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                }
            }
        });



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

    public void comprobarNocheFragment() {
        SharedPreferences modonoche = getActivity().getSharedPreferences("noche", Context.MODE_PRIVATE);
        Boolean modo_actual = modonoche.getBoolean("noche", false);
        if (modo_actual == true) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            fondo.setBackgroundResource(R.drawable.fondo_oscuro_fragment);
            modificar.setBackgroundResource(R.drawable.boton_redondo);
        } else {
            fondo.setBackgroundResource(R.drawable.fondo_claro_fragment);
            modificar.setBackgroundResource(R.drawable.boton_dia_naranja);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
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


                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, lista_categorias);
                    categoria.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    public int comprobarCategoria(String categoria){
        int res=0;
        for (int i=0; i<lista_categorias.length;i++){

            if (categoria.equals(lista_categorias[i])){
                res=i;
            }
        }
        return res;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECCIONAR_FOTO && resultCode == RESULT_OK && !camara ) {
            foto_url = data.getData();
            foto_prod.setImageURI(foto_url);
            Toast.makeText(getContext(), "Imagen seleccionada", Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(getContext(), "Error", Toast.LENGTH_LONG).show();
        }

        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && camara){
            Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);
            foto_prod.setImageBitmap(bitmap);
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }





}
