package com.example.andres.botonpanico.source;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.andres.botonpanico.R;
import com.example.andres.botonpanico.client.Cliente;
import com.example.andres.botonpanico.customViews.CustomImageView;

/**
 * Created by Andres on 30/04/2016.
 * Clase que se encarga de lanzar un hilo que conecta con el servidor y comprueba que tanto
 * la conexión con este como la contraseña introducida son correctos.
 */
public class AsyncProbar extends AsyncTask<Void, Void, Integer> {

    Cliente cliente;
    Context context;
    TextView resultadoConexion;
    TextView detalleConexion;
    CustomImageView loading;
    String ip;
    int puerto;
    String pass;
    private int segundos;

    public AsyncProbar(Context context, TextView resultadoConexion, TextView detalleConexion,
                       CustomImageView loading, String ip, int puerto, String pass) {
        this.pass = pass;
        this.resultadoConexion = resultadoConexion;
        this.detalleConexion = detalleConexion;
        this.context = context;
        this.ip = ip;
        this.puerto = puerto;
        this.loading = loading;
    }

    @Override
    protected void onPreExecute() {
        cliente = new Cliente(pass, ip, puerto);
        resultadoConexion.setVisibility(View.GONE);
        detalleConexion.setVisibility(View.GONE);
        loading.start();
    }

    @Override
    protected Integer doInBackground(Void... params) {
        int[] resultado = cliente.contactar(Var.PROBAR);
        if(resultado[0] == Var.CONNECTION_REFUSED) {
            segundos = resultado[1];
        }
        cliente.cerrarStreams();
        return resultado[0];
    }

    protected void onPostExecute(Integer resultado) {
        loading.stop();
        switch (resultado) {
            case 1:
                resultadoConexion.setText(R.string.conexionCorrecta);
                detalleConexion.setText("");
                break;
            case 0:
                Toast.makeText(context, R.string.contrasenaIncorrecta, Toast.LENGTH_SHORT).show();
                resultadoConexion.setText(R.string.contrasenaIncorrecta);
                detalleConexion.setText("");
                break;
            case -1:
                Toast.makeText(context, R.string.conexionFallida, Toast.LENGTH_LONG).show();
                resultadoConexion.setText(R.string.conexionFallida);
                detalleConexion.setText("");
                break;
            case -2:
                Toast.makeText(context, R.string.toastMalaConexionODireccion, Toast.LENGTH_LONG).show();
                resultadoConexion.setText(R.string.conexionFallida);
                detalleConexion.setText(R.string.malaConexionODireccion);
                break;
            case -3:
                Toast.makeText(context, R.string.toastConexionPerdida, Toast.LENGTH_LONG).show();
                resultadoConexion.setText(R.string.conexionFallida);
                detalleConexion.setText(R.string.conexionPerdida);
                break;
            case -4:
                Toast.makeText(context, R.string.toastCorruptos, Toast.LENGTH_LONG).show();
                resultadoConexion.setText(R.string.conexionFallida);
                detalleConexion.setText(R.string.datosCorruptos);
                break;
            case -5:
                Toast.makeText(context, R.string.toastTimeout, Toast.LENGTH_LONG).show();
                resultadoConexion.setText(R.string.conexionFallida);
                detalleConexion.setText(R.string.timeout);
                break;
            case -6:
                String mensaje = context.getString(R.string.toastRefused) + " " + segundos +
                        " " + context.getString(R.string.segundos);
                Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show();
                resultadoConexion.setText(R.string.conexionFallida);
                detalleConexion.setText(R.string.refused);
                break;
        }
        resultadoConexion.setVisibility(View.VISIBLE);
        detalleConexion.setVisibility(View.VISIBLE);
    }
}
