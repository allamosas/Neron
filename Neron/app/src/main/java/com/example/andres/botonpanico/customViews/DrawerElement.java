package com.example.andres.botonpanico.customViews;

import android.graphics.drawable.Drawable;

/**
 * Created by Andres on 30/04/2016.
 * Clase para cada item del menú desplegable o Drawer
 */
public class DrawerElement {
    int icono;
    String nombre;

    public DrawerElement() {
    }

    public DrawerElement(int icono, String nombre) {
        this.icono = icono;
        this.nombre = nombre;
    }

    public int getImageDrawable() {
        return icono;
    }

    public void setImageDrawable(int icono) { this.icono = icono; }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
