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
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Pattern;

public class Registro extends AppCompatActivity {

    private ImageView foto_usuario;
    private TextInputEditText apellidos,nombre,direccion,contraseña,email;
    private Spinner moneda;
    private Uri foto_url;
    private final static int SELECCIONAR_FOTO = 1;
    private DatabaseReference ref;
    private StorageReference sto;
    private Button button;
    private ConstraintLayout fondoRegistro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        button = (Button) findViewById(R.id.button);
        fondoRegistro = (ConstraintLayout) findViewById(R.id.fondoRegistro);

        if (comprobarNoche()){
            button.setBackgroundResource(R.drawable.boton_redondo);
            fondoRegistro.setBackgroundResource(R.drawable.fondonoche);
        }

        nombre = (TextInputEditText) findViewById(R.id.nombre);
        contraseña = (TextInputEditText) findViewById(R.id.contraseña);
        email = (TextInputEditText) findViewById(R.id.email);
        direccion=(TextInputEditText)findViewById(R.id.direccion);
        apellidos= (TextInputEditText) findViewById(R.id.apellidos);
        moneda = (Spinner) findViewById(R.id.moneda);

        foto_usuario = (ImageView) findViewById(R.id.foto_usuario);

        String [] opciones = {"Euro", "Dolar", "Libra"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, opciones);

        moneda.setAdapter(adapter);
        moneda.setSelection(0);

        ref = FirebaseDatabase.getInstance().getReference();
        sto = FirebaseStorage.getInstance().getReference();
        foto_url = null;

    }


    public void crearCuenta(View v) {

        final String valor_email = email.getText().toString();
        final String valor_contraseña = contraseña.getText().toString();
        final String valor_nombre = nombre.getText().toString();
        final String valor_apellidos = apellidos.getText().toString();
        final String valor_direccion = direccion.getText().toString();
        final String valor_moneda = this.moneda.getSelectedItem().toString();

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
                                Toast.makeText(getApplicationContext(), "El email ya existe", Toast.LENGTH_LONG).show();
                            } else {
                                if (foto_url != null) {
                                    if(validarEmail(valor_email)) {

//                                        Calendar calendar = Calendar.getInstance();
//                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
//                                        String fecha_creacion = sdf.format(calendar.getTime());

                                        int tipo_moneda=0;

                                        switch (valor_moneda){
                                            case "Euro":
                                                tipo_moneda=0;
                                                break;
                                            case "Dolar":
                                                tipo_moneda=1;
                                                break;
                                            case "Libra":
                                                tipo_moneda=2;
                                                break;

                                            default:
                                                tipo_moneda=0;
                                        }


                                        Cliente nuevo_cliente = new Cliente(valor_email, valor_contraseña, valor_nombre, valor_apellidos,valor_direccion,tipo_moneda);

                                        String clave = ref.child("tienda").child("clientes").push().getKey();

                                        ref.child("tienda").child("clientes").child(clave).setValue(nuevo_cliente);
                                        sto.child("tienda").child("clientes").child("imagenes").child(clave).putFile(foto_url);
                                        Toast.makeText(Registro.this, "Cliente registrado con éxito", Toast.LENGTH_LONG).show();

                                        Intent i = new Intent(Registro.this, MainActivity.class);
                                        startActivity(i);

                                    }else{
                                        Toast.makeText(Registro.this, "Email inválido", Toast.LENGTH_SHORT).show();
                                    }

                                } else {
                                    Toast.makeText(Registro.this, "No se ha seleccionado una imagen", Toast.LENGTH_LONG).show();
                                }

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }
    }

    public void seleccionarFoto (View v){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, SELECCIONAR_FOTO);
    }

    @Override
    protected void onActivityResult ( int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECCIONAR_FOTO && resultCode == RESULT_OK) {
            foto_url = data.getData();
            foto_usuario.setImageURI(foto_url);
            Toast.makeText(getApplicationContext(), "Imagen seleccionada", Toast.LENGTH_LONG).show();

        } else {
            Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
        }
    }

    private boolean validarEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
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
