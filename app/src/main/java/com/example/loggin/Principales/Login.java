package com.example.loggin.Principales;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.loggin.Objetos.Cliente;
import com.example.loggin.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;

import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class Login extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private TextInputEditText email,contraseña;
    private DatabaseReference ref;
    private StorageReference sto;
    private Button entrar;
    private GoogleApiClient googleApiClient;
    private SignInButton boton_google;
    public static final int SIGN_IN_CODE = 777;
    private ConstraintLayout fondo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences credenciales = getSharedPreferences("datos_usuario", Context.MODE_PRIVATE);
        final SharedPreferences.Editor obj_editor = credenciales.edit();

        obj_editor.putString("id_usuario","");
        obj_editor.putBoolean("admin",false);
        obj_editor.commit();



        fondo = (ConstraintLayout) findViewById(R.id.fondoLogin);
        entrar = (Button) findViewById(R.id.entrar);

        comprobarNoche();
        if (comprobarNoche()){
            entrar.setBackgroundResource(R.drawable.boton_redondo);
            fondo.setBackgroundResource(R.drawable.fondonoche);

        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        boton_google = (SignInButton) findViewById(R.id.boton_google);
        boton_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(intent, SIGN_IN_CODE);
            }
        });



        email=(TextInputEditText) findViewById(R.id.email);
        contraseña=(TextInputEditText) findViewById(R.id.contraseña);


        ref = FirebaseDatabase.getInstance().getReference();
        sto = FirebaseStorage.getInstance().getReference();


        boton_google.setSize(SignInButton.SIZE_WIDE);
        boton_google.setColorScheme(SignInButton.COLOR_AUTO);
        boton_google.setVisibility(View.GONE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SIGN_IN_CODE){
           GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
           handleSignInResult(result);
        }
    }

    private  void handleSignInResult(GoogleSignInResult result){
        System.out.println(result.isSuccess());
            if(result.isSuccess()){
                goMainScreen();
            }else{
                Toast.makeText(this, "No se pudo iniciar sesion", Toast.LENGTH_SHORT).show();
            }
    }

    private void goMainScreen() {
        Intent intent = new Intent(this, MenuGlobal.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_NEW_TASK);

        SharedPreferences credenciales = getSharedPreferences("datos_usuario", Context.MODE_PRIVATE);
        SharedPreferences.Editor obj_editor = credenciales.edit();

        obj_editor.putBoolean("google",true);
        obj_editor.commit();

        startActivity(intent);
    }

    public void registro(View view){
        Intent i = new Intent(this,Registro.class);
        startActivity(i);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public void menuPrincipal(View view){
        SharedPreferences credenciales = getSharedPreferences("datos_usuario", Context.MODE_PRIVATE);
        final SharedPreferences.Editor obj_editor = credenciales.edit();

        final String valor_email = email.getText().toString();
        final String valor_contraseña = contraseña.getText().toString();

        if (valor_email.equals("") || valor_contraseña.equals("")) {
            Toast.makeText(this, "Completa los campos necesarios", Toast.LENGTH_LONG).show();
        } else {

            ref.child("tienda").child("administrador").child("contraseña").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String resultado= dataSnapshot.getValue(String.class);

                    if (resultado.equals(valor_contraseña)){
                        obj_editor.putBoolean("admin",true);
                        obj_editor.commit();
                        Intent i = new Intent(Login.this,MenuAdministrador.class);
                        startActivity(i);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        Toast.makeText(getApplicationContext(), "Bienvenido señor", Toast.LENGTH_LONG).show();

                    }else{

                        ref.child("tienda")
                                .child("clientes")
                                .orderByChild("email")
                                .equalTo(valor_email)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.hasChildren()) {

                                            System.out.println("EXISTE");
                                            DataSnapshot hijo=dataSnapshot.getChildren().iterator().next();
                                            String contraseña_verdadera = hijo.getValue(Cliente.class).getContraseña();
                                            String nombre_usuario = hijo.getValue(Cliente.class).getNombre();
                                            int valor_moneda = hijo.getValue(Cliente.class).getMoneda();
                                            String id_cliente = hijo.getKey();


                                            if (contraseña_verdadera.equals(valor_contraseña)){

                                                Intent i = new Intent(Login.this,MenuGlobal.class);

                                                obj_editor.putBoolean("google",false);
                                                obj_editor.commit();
                                                guardarPreferencias(id_cliente,valor_moneda,nombre_usuario);
                                                System.out.println(id_cliente+"   "+valor_moneda);
                                                startActivity(i);
                                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                                Toast.makeText(getApplicationContext(), "Bienvenido", Toast.LENGTH_LONG).show();
                                            }else{
                                                contraseña.setError("Usuario o Contraseña no coinciden");
                                            }
                                        } else {
                                            System.out.println("NO EXISTE");
                                            Toast.makeText(getApplicationContext(), "El Usuario o Contraseña no coinciden", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });


                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {




    }
    public void guardarPreferencias(String id,int moneda,String nombre){
        SharedPreferences credenciales = getSharedPreferences("datos_usuario", Context.MODE_PRIVATE);
        SharedPreferences.Editor obj_editor = credenciales.edit();
        obj_editor.putString("id_usuario",id);
        obj_editor.putString("nombre_usuario",nombre);
        obj_editor.putInt("moneda",moneda);
        obj_editor.putBoolean("admin",false);
        obj_editor.commit();

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
