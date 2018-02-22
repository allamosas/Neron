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
 * Clase que se encarga de lanzar un hilo que conecta con el servidor, elimina todos los ficheros
 * seleccionados y sobreescribe el espacio disponible en disco para que no se puedan recuperar.
 */
public class AsyncEliminar extends AsyncTask<Void, Void, Integer> {

    private Cliente cliente;
    private ProgressManager pm;
    private Context context;
    private TextView resultadoConexion;
    TextView detalleConexion;
    private CustomImageView loading;
    private String ip;
    private int puerto;
    private String pass;
    private int segundos;


    public AsyncEliminar(Context context, ProgressManager progressManager,
                         TextView resultadoConexion, TextView detalleConexion,
                         CustomImageView loading, String ip, int puerto, String pass) {
        this.context = context;
        this.pm = progressManager;
        this.resultadoConexion = resultadoConexion;
        this.detalleConexion = detalleConexion;
        this.loading = loading;
        this.ip = ip;
        this.puerto = puerto;
        this.pass = pass;
    }

    @Override
    protected void onPreExecute() {
        cliente = new Cliente(pass, pm.progressBar1, pm.progressBar2, pm.progressBar3, ip, puerto);
        resultadoConexion.setVisibility(View.GONE);
        detalleConexion.setVisibility(View.GONE);
        pm.setEliminando(true);
        loading.start();
    }

    @Override
    protected Integer doInBackground(Void... params) {
        int[] resultado = cliente.contactar(Var.ELIMINAR);
        if(resultado[0] == Var.CONN_SUCCESS) {
            cliente.eliminarFicheros();
        }
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
                resultadoConexion.setText("");
                detalleConexion.setText("");
                break;
            case 0:
                Toast.makeText(context, R.string.contrasenaIncorrecta, Toast.LENGTH_SHORT).show();
                resultadoConexion.setText(R.string.contrasenaIncorrecta);
                detalleConexion.setText("");
                pm.llenar();
                break;
            case -1:
                Toast.makeText(context, R.string.conexionFallida, Toast.LENGTH_LONG).show();
                resultadoConexion.setText(R.string.conexionFallida);
                detalleConexion.setText("");
                pm.llenar();
                break;
            case -2:
                Toast.makeText(context, R.string.toastMalaConexionODireccion, Toast.LENGTH_LONG).show();
                resultadoConexion.setText(R.string.conexionFallida);
                detalleConexion.setText(R.string.malaConexionODireccion);
                pm.llenar();
                break;
            case -3:
                Toast.makeText(context, R.string.toastConexionPerdida, Toast.LENGTH_LONG).show();
                resultadoConexion.setText(R.string.conexionFallida);
                detalleConexion.setText(R.string.conexionPerdida);
                pm.llenar();
                break;
            case -4:
                Toast.makeText(context, R.string.toastCorruptos, Toast.LENGTH_LONG).show();
                resultadoConexion.setText(R.string.conexionFallida);
                detalleConexion.setText(R.string.datosCorruptos);
                pm.llenar();
                break;
            case -5:
                Toast.makeText(context, R.string.toastTimeout, Toast.LENGTH_LONG).show();
                resultadoConexion.setText(R.string.conexionFallida);
                detalleConexion.setText(R.string.timeout);
                pm.llenar();
                break;
            case -6:
                String mensaje = context.getString(R.string.toastRefused) + " " + segundos +
                        " " + context.getString(R.string.segundos);
                Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show();
                resultadoConexion.setText(R.string.conexionFallida);
                detalleConexion.setText(R.string.refused);
                break;
        }
        pm.setEliminando(false);
        resultadoConexion.setVisibility(View.VISIBLE);
        detalleConexion.setVisibility(View.VISIBLE);
    }
}