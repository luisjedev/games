package com.example.loggin;

public class Reserva {

    private String id,nombre_producto,nombre_cliente,id_producto,id_cliente
            ,fecha;

    private int estado;
    private boolean estado_notificado;

    public Reserva(String id, String nombre_producto, String nombre_cliente, String id_producto, String id_cliente, String fecha) {
        this.id = id;
        this.nombre_producto = nombre_producto;
        this.nombre_cliente = nombre_cliente;
        this.id_producto = id_producto;
        this.id_cliente = id_cliente;
        this.fecha = fecha;
        this.estado = 0;
        this.estado_notificado = false;
    }
}
