package com.example.andres.botonpanico.userInterface;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ScrollView;

import com.example.andres.botonpanico.R;
import com.example.andres.botonpanico.customViews.ArrayAdapterDrawer;
import com.example.andres.botonpanico.customViews.DrawerElement;

import java.util.ArrayList;

public class Ayuda extends AppCompatActivity {
    Context context = this;

    private ListView drawerList;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private boolean estadoSlider = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ayuda);

        drawerLayout();
        scrollListener();
    }

    /**
     * Función que crea el drawer desplegable de la izquierda.
     * Éste puede ser abierto tocando en el margen izquierdo de la pantalla.
     */
    private void drawerLayout() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ListView) findViewById(R.id.listView);

        ArrayAdapterDrawer adapter = new ArrayAdapterDrawer(this, cargarAjustes());
        // Set the adapter for the list view
        drawerList.setAdapter(adapter);
        // Set the list's click listener
        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                drawerLayout.closeDrawer(Gravity.LEFT);
                switch(position) {
                    case 0:
                        finish();
                        break;
                    case 1:
                        startActivity(new Intent(context, Ajustes.class));
                        finish();
                        break;
                }
                estadoSlider = false;
            }
        });

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                estadoSlider = false;
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                estadoSlider = true;
            }
        };

        // Set the drawer toggle as the DrawerListener
        drawerLayout.setDrawerListener(drawerToggle);
    }

    /**
     * Se cargan la cabecera y el resto de elementos del menú desplegable
     * del drawer
     * @return
     */
    private ArrayList<DrawerElement> cargarAjustes() {
        //Cabecera Neron
        ArrayList<DrawerElement> arrayDrawer = new ArrayList<>();
        DrawerElement de = new DrawerElement(R.mipmap.logo_white, "");
        arrayDrawer.add(de);

        //1-Ajustes
        de = new DrawerElement(R.drawable.settings, getString(R.string.ajustes));
        arrayDrawer.add(de);

        //2-Ayuda
        de = new DrawerElement(R.drawable.help, getString(R.string.ayuda));
        arrayDrawer.add(de);

        return arrayDrawer;
    }

    /**
     * Función que abre y cierra el drawer desplegable mediante un botón de la interfaz.
     * @param view
     */
    public void abrirCerrarDrawer(View view) {
        if (estadoSlider) {
            drawerLayout.closeDrawer(Gravity.RIGHT);
            estadoSlider = false;
        } else {
            drawerLayout.openDrawer(Gravity.LEFT);
            estadoSlider = true;
        }
    }

    private void scrollListener() {
        final ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView2);
        scrollView.getViewTreeObserver().addOnScrollChangedListener(
                new ViewTreeObserver.OnScrollChangedListener() {

            @Override
            public void onScrollChanged() {
                if(scrollView.getScrollY() < 80) {
                    findViewById(R.id.shadow).setVisibility(View.GONE);
                } else {
                    findViewById(R.id.shadow).setVisibility(View.VISIBLE);
                }

            }
        });
    }
}
