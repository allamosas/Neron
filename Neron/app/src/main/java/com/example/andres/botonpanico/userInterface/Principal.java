package com.example.andres.botonpanico.userInterface;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.SharedPreferences;
import android.graphics.Color;

import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;

import android.support.v4.app.NotificationCompat;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.andres.botonpanico.customViews.DrawerElement;
import com.example.andres.botonpanico.customViews.ArrayAdapterDrawer;
import com.example.andres.botonpanico.source.AsyncEliminar;
import com.example.andres.botonpanico.source.AsyncProbar;
import com.example.andres.botonpanico.source.ProgressManager;
import com.example.andres.botonpanico.R;
import com.example.andres.botonpanico.customViews.CustomImageView;

import java.util.ArrayList;
import java.util.Locale;


public class Principal extends AppCompatActivity {
    Context context = this;
    ProgressManager progressManager;

    private ListView drawerList;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private boolean estadoSlider = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        drawerLayout();
        lanzarNotificacion();
        progressManager = new ProgressManager(
                (ProgressBar) findViewById(R.id.progresoEliminarFicheros),
                (ProgressBar) findViewById(R.id.progresoSobreescribirEspacio),
                (ProgressBar) findViewById(R.id.progresoEliminarSobreescritura));
    }

    /**
     * Función que muestra el pop up previo a la eliminación
     * @param view
     */
    public void showPopupEliminar(View view) {
        if(!progressManager.isEliminando()) {
            progressManager.vaciar();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();

            final boolean[] clicked = {false};
            builder.setView(inflater.inflate(R.layout.popup, null));
            builder.setPositiveButton(R.string.eliminar, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    clicked[0] = true;
                    Dialog aux = (Dialog) dialog;
                    eliminar(aux);
                }
            });
            builder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    clicked[0] = true;
                    progressManager.llenar();
                }
            });
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if(!clicked[0]) {
                        progressManager.llenar();
                    }
                }
            });

            Window window = this.getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
            AlertDialog dialog = builder.create();
            dialog.show();
            Button eliminar = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
            eliminar.setBackgroundColor(Color.parseColor("#ce3b29"));
            eliminar.setTextColor(Color.parseColor("#ffffff"));
        }
    }

    /**
     * Lanza el hilo de eliminación pasándole la contraseña introducida por el usuario en el pop up
     * y la ip y puerto guardado en las preferencias.
     * @param auxiliar
     */
    private void eliminar(Dialog auxiliar) {
        EditText editPass = (EditText) auxiliar.findViewById(R.id.editText2);
        String pass = editPass.getText().toString();

        SharedPreferences prefs = getSharedPreferences("settings",Context.MODE_PRIVATE);
        String ip = prefs.getString("ip", "192.168.1.1");
        int puerto = prefs.getInt("puerto", 30500);

        AsyncEliminar eliminar = new AsyncEliminar(context, progressManager,
                (TextView) findViewById(R.id.resultadoConexion),
                (TextView) findViewById(R.id.detalleConexion),
                (CustomImageView) findViewById(R.id.loadingView), ip, puerto, pass);
        eliminar.execute();
    }

    /**
     * Muestra el pop up previo a la comprobación de contraseña y conexión con el servidor
     * @param view
     */
    public void showPopupProbar(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.popup, null));
        builder.setPositiveButton(R.string.probar, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                SharedPreferences prefs = getSharedPreferences("settings",Context.MODE_PRIVATE);
                String ip = prefs.getString("ip", "192.168.1.1");
                int puerto = prefs.getInt("puerto", 30500);
                Dialog auxiliar = (Dialog) dialog;
                EditText editPass = (EditText) auxiliar.findViewById(R.id.editText2);
                String pass = editPass.getText().toString();
                AsyncProbar probar = new AsyncProbar(context,
                        (TextView) findViewById(R.id.resultadoConexion),
                        (TextView) findViewById(R.id.detalleConexion),
                        (CustomImageView) findViewById(R.id.loadingView), ip, puerto, pass);
                probar.execute();
            }
        });
        builder.setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Lanza la notificación persistente con la que se puede acceder rápidamente a la aplicación,
     * si es que el usuario así lo desea.
     */
    private void lanzarNotificacion() {
        SharedPreferences prefs = getSharedPreferences("settings",Context.MODE_PRIVATE);
        if(prefs.getInt("prioridad", -1) != 0) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.white_ic)
                    .setContentTitle(getString(R.string.neron))
                    .setContentText(getString(R.string.detalleNotificacion))
                    .setOngoing(true)
                    .setPriority(prefs.getInt("prioridad", -1) - 20)
                    .setShowWhen(false);

            Intent resultIntent = new Intent(this, Principal.class);
            PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(resultPendingIntent);

            int notificationId = 001;
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(notificationId, builder.build());
        }
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
                    case 1:
                        startActivity(new Intent(context, Ajustes.class));
                        break;
                    case 2:
                        startActivity(new Intent(context, Ayuda.class));
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
        DrawerElement de = (Locale.getDefault().getLanguage().equals("zh"))
            ? new DrawerElement(R.mipmap.logo_white_zh, "") : new DrawerElement(R.mipmap.logo_white, "");

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
}
