package com.example.loggin.Objetos;

import android.net.Uri;

public class Producto {

    private String id,nombre,categoria,descripcion,precio;
    private Uri foto_url;
    private boolean disponible;

    public Producto() {

        this.id = "";
        this.foto_url = null;
        this.nombre = "";
        this.categoria = "";
        this.descripcion = "";
        this.precio = "";
        this.disponible = true;
    }

    public Producto(String nombre, String categoria, String descripcion, String precio,Boolean disponible) {
        this.id = "";
        this.foto_url = null;
        this.nombre = nombre;
        this.categoria = categoria;
        this.descripcion = descripcion;
        this.precio = precio;
        this.disponible = disponible;
    }

    public String getId() {
        return id;
    }

    public Uri getFoto_url() {
        return foto_url;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getPrecio() {
        return precio;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setFoto_url(Uri foto_url) {
        this.foto_url = foto_url;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }
}
