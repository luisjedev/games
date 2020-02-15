package com.example.loggin;

import android.net.Uri;

public class Reserva {

    private String id,nombre_producto,nombre_cliente,id_producto,id_cliente,fecha;

    private int estado;
    private boolean estado_notificado;
    private Uri foto_url;

    public Reserva() {
    }

    public Reserva(String nombre_producto, String nombre_cliente, String id_producto, String id_cliente, String fechao) {
        this.id = "";
        this.nombre_producto = nombre_producto;
        this.nombre_cliente = nombre_cliente;
        this.id_producto = id_producto;
        this.id_cliente = id_cliente;
        this.foto_url=null;
        this.fecha = fecha;
        this.estado = 0;
        this.estado_notificado = false;
    }

    public String getId() {
        return id;
    }

    public String getNombre_producto() {
        return nombre_producto;
    }

    public String getNombre_cliente() {
        return nombre_cliente;
    }

    public String getId_producto() {
        return id_producto;
    }

    public String getId_cliente() {
        return id_cliente;
    }

    public String getFecha() {
        return fecha;
    }

    public Uri getFoto_url() {
        return foto_url;
    }

    public int getEstado() {
        return estado;
    }

    public boolean isEstado_notificado() {
        return estado_notificado;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setNombre_producto(String nombre_producto) {
        this.nombre_producto = nombre_producto;
    }

    public void setNombre_cliente(String nombre_cliente) {
        this.nombre_cliente = nombre_cliente;
    }

    public void setId_producto(String id_producto) {
        this.id_producto = id_producto;
    }

    public void setId_cliente(String id_cliente) {
        this.id_cliente = id_cliente;
    }

    public void setFoto_url(Uri foto_url) {
        this.foto_url = foto_url;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public void setEstado_notificado(boolean estado_notificado) {
        this.estado_notificado = estado_notificado;
    }
}
