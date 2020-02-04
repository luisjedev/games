package com.example.loggin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.regex.Pattern;

public class Preferencias extends AppCompatActivity {

    private TextView editar_nombre, editar_contraseña, editar_email,editar_apellidos,editar_direccion,editar_moneda;
    private ImageView editar_foto;
    private String id;
    private Uri foto_url;
    private DatabaseReference ref;
    private Button button,modoNoche;
    private StorageReference sto;
    private Spinner moneda;
    private ConstraintLayout fondo;
    private final static int SELECCIONAR_FOTO=1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferencias);

        button=(Button)findViewById(R.id.button);
        modoNoche=(Button)findViewById(R.id.modoNoche);
        fondo = (ConstraintLayout) findViewById(R.id.fondoopciones);
        moneda = (Spinner) findViewById(R.id.moneda);
        if (comprobarNoche()){

            button.setBackgroundResource(R.drawable.boton_redondo);
            modoNoche.setBackgroundResource(R.drawable.boton_redondo);
            fondo.setBackgroundResource(R.drawable.fondo_oscuro_fragment);
        }

        foto_url=null;
        editar_email = (TextView) findViewById(R.id.email);
        editar_nombre = (TextView) findViewById(R.id.nombre);
        editar_foto = (ImageView) findViewById(R.id.imagen_usuario);
        editar_contraseña = (TextView) findViewById(R.id.contraseña);
        editar_apellidos = (TextView) findViewById(R.id.apellidos);
        editar_direccion = (TextView) findViewById(R.id.direccion);

        String [] opciones = {"Euro", "Dolar", "Libra"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, opciones);

        moneda.setAdapter(adapter);

        SharedPreferences credenciales = getSharedPreferences("datos_usuario", Context.MODE_PRIVATE);
        String id_usuario = credenciales.getString("id_usuario","");

        id=id_usuario;

        ref= FirebaseDatabase.getInstance().getReference();
        sto= FirebaseStorage.getInstance().getReference();

        ref.child("tienda")
                .child("clientes")
                .child(id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        Cliente resultado=dataSnapshot.getValue(Cliente.class);

                        int tipo_moneda = resultado.getMoneda();

                        editar_email.setText(resultado.getEmail());
                        editar_contraseña.setText(resultado.getContraseña());
                        editar_nombre.setText(resultado.getNombre());
                        editar_apellidos.setText(resultado.getApellidos());
                        editar_direccion.setText(resultado.getDireccion());
                        moneda.setSelection(resultado.getMoneda());


                        sto.child("tienda").child("clientes").child("imagenes").child(id).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Glide.with(getApplicationContext()).load(uri).into(editar_foto);
                                foto_url=uri;
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



    }
    public void modificarUsuario(View v){

        final String valor_email=editar_email.getText().toString();
        final String valor_contraseña=editar_contraseña.getText().toString();
        final String valor_nombre=editar_nombre.getText().toString();
        final String valor_apellidos=editar_apellidos.getText().toString();
        final String valor_direccion=editar_direccion.getText().toString();
        final int valor_moneda= moneda.getSelectedItemPosition();

        final Handler mWaitHandler = new Handler();

        if (valor_email.equals("") || valor_contraseña.equals("") || valor_nombre.equals("") || valor_apellidos.equals("") || valor_direccion.equals("")) {
            Toast.makeText(this, "Completa los campos necesarios", Toast.LENGTH_LONG).show();
        } else {

            ref.child("tienda")
                    .child("clientes")
                    .orderByChild("email")
                    .equalTo(valor_email)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (dataSnapshot.hasChildren()) {

                                DataSnapshot hijo=dataSnapshot.getChildren().iterator().next();


                                    if (hijo.getKey().equals(id)){

                                        if (foto_url != null) {
                                            if (validarEmail(valor_email)) {

                                                Cliente nuevo_cliente = new Cliente(valor_email, valor_contraseña, valor_nombre, valor_apellidos,valor_direccion,valor_moneda);
                                                ref.child("tienda").child("clientes").child(id).setValue(nuevo_cliente);
                                                sto.child("tienda").child("clientes").child("imagenes").child(id).putFile(foto_url);

                                                Toast.makeText(getApplicationContext(), "Cliente editado con éxito", Toast.LENGTH_LONG).show();

                                                mWaitHandler.postDelayed(new Runnable() {

                                                    @Override
                                                    public void run() {

                                                        try {
                                                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                            startActivity(intent);
                                                        } catch (Exception ignored) {
                                                            ignored.printStackTrace();
                                                        }
                                                    }
                                                }, 1000);  // Give a 5 seconds delay.
                                            }else{
                                                Toast.makeText(Preferencias.this, "Email inválido", Toast.LENGTH_SHORT).show();
                                            }

                                        } else{

                                            Toast.makeText(Preferencias.this, "No se ha seleccionado una imagen", Toast.LENGTH_LONG).show();
                                        }
                                    }else{

                                        Toast.makeText(getApplicationContext(), "El cliente ya existe", Toast.LENGTH_LONG).show();

                                    }

                            } else {

                                if (foto_url != null) {

                                    if (validarEmail(valor_email)) {

                                        Cliente nuevo_cliente = new Cliente(valor_email, valor_contraseña, valor_nombre, valor_apellidos,valor_direccion,valor_moneda);

                                        ref.child("tienda").child("clientes").child(id).setValue(nuevo_cliente);
                                        sto.child("tienda").child("clientes").child("imagenes").child(id).putFile(foto_url);

                                        Toast.makeText(getApplicationContext(), "Cliente editado con éxito", Toast.LENGTH_LONG).show();

                                        mWaitHandler.postDelayed(new Runnable() {

                                            @Override
                                            public void run() {

                                                try {
                                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                    startActivity(intent);
                                                } catch (Exception ignored) {
                                                    ignored.printStackTrace();
                                                }
                                            }
                                        }, 1000);  // Give a 5 seconds delay.

                                    }else{
                                        Toast.makeText(Preferencias.this, "Email inválido", Toast.LENGTH_SHORT).show();
                                    }

                                } else{

                                    Toast.makeText(Preferencias.this, "No se ha seleccionado una imagen", Toast.LENGTH_LONG).show();
                                }


                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }

    }

    public void seleccionarFotoUsuario(View v){

        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,SELECCIONAR_FOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==SELECCIONAR_FOTO && resultCode==RESULT_OK){
            foto_url=data.getData();
            editar_foto.setImageURI(foto_url);
            Toast.makeText(getApplicationContext(),"Imagen seleccionada",Toast.LENGTH_LONG).show();

        }else{
            Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_LONG).show();
        }
    }

    private boolean validarEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }

    public void cambiarColor(View view){

        if (AppCompatDelegate.getDefaultNightMode()==AppCompatDelegate.MODE_NIGHT_NO){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

            Boolean dayNight=true;

            guardarPreferencias(dayNight);
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

            Boolean dayNight=false;

            guardarPreferencias(dayNight);
        }

    }

    public void guardarPreferencias(Boolean dayNight){
        SharedPreferences modonoche = getSharedPreferences("noche", Context.MODE_PRIVATE);
        SharedPreferences.Editor obj_editor = modonoche.edit();
        if (dayNight==true){
            obj_editor.putBoolean("noche",true);
            obj_editor.commit();
        }else{
            obj_editor.putBoolean("noche",false);
            obj_editor.commit();
        }

    }

    public boolean comprobarNoche(){

        boolean res;

        SharedPreferences modonoche = getSharedPreferences("noche", Context.MODE_PRIVATE);
        Boolean modo_actual= modonoche.getBoolean("noche",false);

        if (modo_actual==true){
            res=true;
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else{
            res=false;
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        return res;
    }

}
