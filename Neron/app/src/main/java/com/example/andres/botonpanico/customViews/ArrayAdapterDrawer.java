package com.example.andres.botonpanico.customViews;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.andres.botonpanico.R;
import com.example.andres.botonpanico.customViews.DrawerElement;

import java.util.List;

/**
 * Created by Andres on 30/04/2016.
 * Adaptador personalizado que consta de un primer elemento Cabecera y varios Item
 */

public class ArrayAdapterDrawer extends ArrayAdapter<DrawerElement> {
    private LayoutInflater layoutInflater;

    public ArrayAdapterDrawer(Context context, List<DrawerElement> objects) {
        super(context, 0, objects);
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        HolderElemento he = null;
        if (convertView == null) {
            he = new HolderElemento();
            if (position == 0) {
                convertView = layoutInflater.inflate(R.layout.drawer_list_header, null);
            } else {
                convertView = layoutInflater.inflate(R.layout.drawer_list_item, null);
            }
            he.setImage((ImageView) convertView.findViewById(R.id.imagen));
            he.setText((TextView) convertView.findViewById(R.id.nombre));

            convertView.setTag(he);
        } else {
            he = (HolderElemento) convertView.getTag();
        }

        DrawerElement e = getItem(position);
        he.getImage().setImageResource(e.getImageDrawable());
        he.getText().setText(e.getNombre());
        return convertView;
    }

    static class HolderElemento {
        ImageView icono;
        TextView nombre;


        public ImageView getImage() {
            return icono;
        }

        public void setImage(ImageView icono) {
            this.icono = icono;
        }

        public TextView getText() {
            return nombre;
        }

        public void setText(TextView nombre) {
            this.nombre = nombre;
        }
    }
}