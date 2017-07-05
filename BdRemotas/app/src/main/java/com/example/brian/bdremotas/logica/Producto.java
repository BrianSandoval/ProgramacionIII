package com.example.brian.bdremotas.logica;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by brian on 22/06/2017.
 */

public class Producto {
    private int codigoProducto;
    private String nombre;
    private double precioVenta;
    private String foto;
    private int canditad;

    public int getCanditad() {
        return canditad;
    }

    public void setCanditad(int canditad) {
        this.canditad = canditad;
    }

    public static ArrayList <Producto> listaProducto = new ArrayList<Producto>();

    public int getCodigoProducto() {
        return codigoProducto;
    }

    public void setCodigoProducto(int codigoProducto) {
        this.codigoProducto = codigoProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(double precioVenta) {
        this.precioVenta = precioVenta;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public JSONObject getJSONItemDetalle(){
        JSONObject objItemDetalle = new JSONObject();
        try {
            // se coloca tal y como esta en el json
            objItemDetalle.put("codigo_producto", this.getCodigoProducto());
            objItemDetalle.put("cantidad", this.getCanditad());
        }catch (JSONException e){
            Log.e("Error", e.getMessage());
        }
        return objItemDetalle;
    }

}
