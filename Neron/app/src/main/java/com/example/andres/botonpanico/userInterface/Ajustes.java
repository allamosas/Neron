package com.example.andres.botonpanico.userInterface;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.andres.botonpanico.R;
import com.example.andres.botonpanico.customViews.DrawerElement;
import com.example.andres.botonpanico.customViews.ArrayAdapterDrawer;

import java.util.ArrayList;

public class Ajustes extends AppCompatActivity {
    Context context = this;

    private ListView drawerList;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private boolean estadoSlider = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajustes);

        drawerLayout();
        cargarPreferencias();
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
                switch (position) {
                    case 0:
                        finish();
                        break;
                    case 2:
                        startActivity(new Intent(context, Ayuda.class));
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
        //Cabecera
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

    /**
     * Carga las preferencias almacenadas por el usuario y las introduce en los elementos
     * de ajustes
     */
    public void cargarPreferencias(){
        SharedPreferences prefs = getSharedPreferences("settings", Context.MODE_PRIVATE);
        TextView txtIp = (TextView) findViewById(R.id.txtIp);
        TextView txtPuerto = (TextView) findViewById(R.id.txtPuerto);
        SeekBar sldPrioridad = (SeekBar) findViewById(R.id.sldPrioridad);
        txtIp.setText(prefs.getString("ip", "192.168.1.1"));
        txtPuerto.setText(Integer.toString(prefs.getInt("puerto", 30500)));
        sldPrioridad.setProgress(prefs.getInt("prioridad", 0));
    }

    /**
     * No se guardan los cambios realizados en las preferencias y cierra la vista
     * @param view
     */
    public void cancelarAjustes(View view) {
        finish();
    }

    /**
     * Guarda los cambios realizados en las preferencias, recarga la notificación persistente
     * y cierra la vista
     * @param view
     */
    public void guardarAjustes(View view) {
        TextView txtIp = (TextView) findViewById(R.id.txtIp);
        TextView txtPuerto = (TextView) findViewById(R.id.txtPuerto);
        SeekBar sldPrioridad = (SeekBar) findViewById(R.id.sldPrioridad);
        if(comprobarDireccion(txtIp.getText().toString())) {
            guardarSharedPreferences(txtIp.getText().toString(),
                    Integer.parseInt(txtPuerto.getText().toString()),
                    sldPrioridad.getProgress());
            recargarNotificacion(sldPrioridad.getProgress());
            Toast.makeText(context, R.string.ajustesguardados, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * Almacena los cambios realizados en las preferencias
     * @param ip
     * @param puerto
     * @param prioridad
     */
    private void guardarSharedPreferences(String ip, int puerto, int prioridad){
        SharedPreferences prefs = getSharedPreferences("settings",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("ip", ip);
        editor.putInt("puerto", puerto);
        editor.putInt("prioridad", prioridad);
        editor.commit();
    }

    /**
     * Recarga la notificación con la prioridad que ha establecido el usuario.
     * Si la prioridad de la notificación es 0, simplemente la elimina.
     * @param progreso
     */
    private void recargarNotificacion(int progreso) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(001);
        if(progreso != 0) {
            progreso = progreso<20&&progreso>15?20:progreso;
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.white_ic)
                    .setContentTitle(getString(R.string.neron))
                    .setContentText(getString(R.string.detalleNotificacion))
                    .setOngoing(true)
                    .setPriority(progreso - 20);

            Intent resultIntent = new Intent(this, Principal.class);
            PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);

            int notificationId = 001;
            notificationManager.notify(notificationId, mBuilder.build());
        }
    }

    /**
     * Comprueba que la dirección recibida como parámetro es una url o una ip válida
     * @param direccion
     * @return
     */
    private boolean comprobarDireccion(String direccion) {
        String regexIP = "^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.)" +
                "{3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
        String regexURL = "^((ftp|http(s)?)?:\\/\\/)?([a-z]+\\.)+" +
                "(aero|asia|biz|cat|com|coop|edu|gov|info|int|jobs|mil|mobi|museum|name|net|org|" +
                "pro|tel|travel|ac|ad|ae|af|ag|ai|al|am|an|ao|aq|ar|as|at|au|aw|ax|az|ba|bb|bd|" +
                "be|bf|bg|bh|bi|bj|bm|bn|bo|br|bs|bt|bv|bw|by|bz|ca|cc|cd|cf|cg|ch|ci|ck|cl|cm|" +
                "cn|co|cr|cu|cv|cx|cy|cz|cz|de|dj|dk|dm|do|dz|ec|ee|eg|er|es|et|eu|fi|fj|fk|fm|" +
                "fo|fr|ga|gb|gd|ge|gf|gg|gh|gi|gl|gm|gn|gp|gq|gr|gs|gt|gu|gw|gy|hk|hm|hn|hr|ht|" +
                "hu|id|ie|il|im|in|io|iq|ir|is|it|je|jm|jo|jp|ke|kg|kh|ki|km|kn|kp|kr|kw|ky|kz|" +
                "la|lb|lc|li|lk|lr|ls|lt|lu|lv|ly|ma|mc|md|me|mg|mh|mk|ml|mn|mn|mo|mp|mr|ms|mt|" +
                "mu|mv|mw|mx|my|mz|na|nc|ne|nf|ng|ni|nl|no|np|nr|nu|nz|nom|pa|pe|pf|pg|ph|pk|pl|" +
                "pm|pn|pr|ps|pt|pw|py|qa|re|ra|rs|ru|rw|sa|sb|sc|sd|se|sg|sh|si|sj|sj|sk|sl|sm|" +
                "sn|so|sr|st|su|sv|sy|sz|tc|td|tf|tg|th|tj|tk|tl|tm|tn|to|tp|tr|tt|tv|tw|tz|ua|" +
                "ug|uk|us|uy|uz|va|vc|ve|vg|vi|vn|vu|wf|ws|ye|yt|yu|za|zm|zw|arpa)$";
        if(direccion.matches(regexIP) || direccion.matches(regexURL)) {
            return true;
        } else {
            Toast toast = Toast.makeText(getApplicationContext(),
                    R.string.direccionIncorrecta, Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER,0,-100);
            toast.show();
            return false;
        }
    }
}
