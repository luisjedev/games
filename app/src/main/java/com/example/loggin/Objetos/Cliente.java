package com.example.loggin.Objetos;

import android.net.Uri;

public class Cliente {

    private String id, email,contraseña,nombre,apellidos, direccion,favoritos;
    private Uri url_foto;
    private int moneda;


    public Cliente() {
        this.id ="";
        this.email = "";
        this.contraseña = "";
        this.nombre = "";
        this.favoritos="";
        this.apellidos = "";
        this.direccion = "";
        this.moneda = 0;
        this.url_foto = null;

    }

    public Cliente(String correo, String contraseña, String nombre, String apellidos, String direccion, int moneda) {
        this.id ="";
        this.email = correo;
        this.contraseña = contraseña;
        this.nombre = nombre;
        this.favoritos="";
        this.apellidos = apellidos;
        this.direccion = direccion;
        this.moneda = moneda;
        this.url_foto = null;
    }

    public Uri getUrl_foto() {
        return url_foto;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getContraseña() {
        return contraseña;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public String getDireccion() {
        return direccion;
    }

    public int getMoneda() {
        return moneda;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUrl_foto(Uri url_foto) {
        this.url_foto = url_foto;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public void setMoneda(int moneda) {
        this.moneda = moneda;
    }
}
