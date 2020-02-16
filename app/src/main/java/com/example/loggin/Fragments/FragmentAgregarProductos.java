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
import android.widget.Toast;

import com.example.loggin.Fragments.FragmentProductos;
import com.example.loggin.Objetos.Producto;
import com.example.loggin.OnFragmentInteractionListener;
import com.example.loggin.R;
import com.example.loggin.dialogoCategoria;
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


public class FragmentAgregarProductos extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ImageView foto_prod;
    private String currentPhotoPath;
    private ImageButton agregarCategoria;
    private Boolean camara;
    private EditText nombre, precio, descripcion;
    private Spinner categoria;
    private Button añadir,tomarfoto;
    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private final static int SELECCIONAR_FOTO = 1;
    private Uri foto_url;
    private String[] lista_categorias;
    private DatabaseReference ref;
    private StorageReference sto;
    private CheckBox estado_producto;
    private FrameLayout fondo;

    private OnFragmentInteractionListener mListener;

    public FragmentAgregarProductos() {
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


        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},1000);
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_agregar_productos, container, false);

        fondo = (FrameLayout) v.findViewById(R.id.fondofrag);
        añadir = (Button) v.findViewById(R.id.button);

        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},1000);
        }

        comprobarNocheFragment();

        nombre = (EditText) v.findViewById(R.id.nombre_prod);
        categoria = (Spinner) v.findViewById(R.id.categoria);
        descripcion = (EditText) v.findViewById(R.id.descripcion);
        precio = (EditText) v.findViewById(R.id.precio);
        estado_producto = (CheckBox) v.findViewById(R.id.estado);
        foto_prod = (ImageView) v.findViewById(R.id.disponible);
        añadir = (Button) v.findViewById(R.id.button);
        tomarfoto = (Button) v.findViewById(R.id.tomarfoto);
        agregarCategoria = (ImageButton) v.findViewById(R.id.agregar_categoria);

        ref = FirebaseDatabase.getInstance().getReference();
        sto = FirebaseStorage.getInstance().getReference();
        foto_url = null;

        cargarCategorias();

        agregarCategoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new dialogoCategoria().show(getFragmentManager(),"añadir");
            }
        });

        foto_prod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                camara=false;
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, SELECCIONAR_FOTO);
            }
        });

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

        añadir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                System.out.println(foto_url);
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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(String TAG, Object data) {
        if (mListener != null) {
            mListener.onFragmentMessage(TAG, data);
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
            añadir.setBackgroundResource(R.drawable.boton_redondo);
        } else {
            fondo.setBackgroundResource(R.drawable.fondo_claro_fragment);
            añadir.setBackgroundResource(R.drawable.boton_dia_naranja);
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
