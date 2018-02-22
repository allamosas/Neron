package com.example.andres.botonpanico.client;

import android.widget.ProgressBar;

import com.example.andres.botonpanico.source.CryptManager;
import com.example.andres.botonpanico.source.Var;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.OutputStream;
import java.io.StreamCorruptedException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.security.PublicKey;

/**
 * Created by Andres on 22/04/2016.
 */
public class Cliente extends Thread {

    private Socket socket;

    private DataOutputStream dos;
    private ObjectInputStream ois;

    private ProgressBar progressBar1;
    private ProgressBar progressBar2;
    private ProgressBar progressBar3;

    String ip;
    int puerto;
    String pass;

    /**
     * Constructor para la prueba de conexión
     * @param pass
     * @param ip
     * @param puerto
     */
    public Cliente(String pass, String ip, int puerto) {
        this.pass = pass;
        this.ip = ip;
        this.puerto = puerto;
    }

    /**
     * Constructor para la eliminación de archivos del servidor
     * @param pass
     * @param pb1
     * @param pb2
     * @param pb3
     * @param ip
     * @param puerto
     */
    public Cliente(String pass, ProgressBar pb1, ProgressBar pb2, ProgressBar pb3, String ip, int puerto) {
        this.pass = pass;
        this.progressBar1 = pb1;
        this.progressBar2 = pb2;
        this.progressBar3 = pb3;
        this.ip = ip;
        this.puerto = puerto;
    }

    /**
     * Abre un socket con el servidor y le envía la operación que se quiere realizar junto a la
     * contraseña para la validación y descifrado.
     * El socket tiene un tiempo de respuesta del OK de 5 segundos y otros 5 segundos de timeout.
     * @param opcion
     * @return
     */
    public int[] contactar(int opcion) {
        int[] resultado = {Var.CONNECTION_FAILED,0};
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(ip, puerto), 5000);
            socket.setSoTimeout(5000);
            dos = new DataOutputStream(socket.getOutputStream());
            dos.write(opcion);

            ois = new ObjectInputStream(socket.getInputStream());
            PublicKey publica = (PublicKey) ois.readObject();
            byte[] ciphertext = CryptManager.cifrarPub(publica, pass);
            dos = new DataOutputStream(socket.getOutputStream());
            dos.writeInt(ciphertext.length);
            dos.write(ciphertext);

            ois = new ObjectInputStream(socket.getInputStream());
            resultado = (int[])ois.readObject();
        } catch (ClassNotFoundException ex) {
            resultado[0] = Var.CONNECTION_FAILED;
        } catch (SocketException ex) {
            //Ningún tipo de conexión a Internet (ni wifi ni datos)
            //No se encuentra ningún dispositivo con esa ip
            resultado[0] = Var.BAD_CONNECTION_OR_ADDRESS;
        } catch (OptionalDataException ex) {
            //Pérdida de conexión momentánea
            resultado[0] = Var.CONNECTION_LOST;
        } catch (StreamCorruptedException ex) {
            //El servidor lanza muchos objetos y el cliente empieza a confundirlos
            //(No se debería dar en ningún caso)
            resultado[0] = Var.DATA_CORRUPTION;
        } catch (IOException ex) {
            //Dirección mal formada (conectando a ip: Evaristo)
            //Salta el timeout (Después de la espera de 5 segundos)
            resultado[0] = Var.CONNECTION_TIMEOUT;
        }
        return resultado;
    }

    /**
     * Se recibe del servidor el progreso de eliminación de
     * ficheros y se van actualizando las barras de progreso
     */
    public int eliminarFicheros() {
        int resultado = Var.CONNECTION_FAILED;
        try {
            ois = new ObjectInputStream(socket.getInputStream());
            int[] progreso = (int[]) ois.readObject();
            while(progreso[0] != 2 || progreso[1] != 100) {
                ois = new ObjectInputStream(socket.getInputStream());
                progreso = (int[]) ois.readObject();
                switch(progreso[0]) {
                    case 0:
                        if(progreso[1] > progressBar1.getProgress()) {
                            progressBar1.setProgress(progreso[1]);
                        }
                        break;
                    case 1:
                        progressBar1.setProgress(100);
                        if(progreso[1] > progressBar2.getProgress()) {
                            progressBar2.setProgress(progreso[1]);
                        }
                        break;
                    case 2:
                        progressBar2.setProgress(100);
                        if(progreso[1] > progressBar3.getProgress()) {
                            progressBar3.setProgress(progreso[1]);
                        }
                        break;
                }
            }
            progressBar3.setProgress(100);
            resultado = Var.CONN_SUCCESS;
        } catch (ClassNotFoundException ex) {
            resultado = Var.CONNECTION_FAILED;
        } catch (SocketException ex) {
            //Ningún tipo de conexión a Internet (ni wifi ni datos)
            //No se encuentra ningún dispositivo con esa ip
            resultado = Var.BAD_CONNECTION_OR_ADDRESS;
        } catch (OptionalDataException ex) {
            //Pérdida de conexión momentánea
            resultado = Var.CONNECTION_LOST;
        } catch (StreamCorruptedException ex) {
            //El servidor lanza muchos objetos y el cliente empieza a confundirlos
            //(No se debería dar en ningún caso)
            resultado = Var.DATA_CORRUPTION;
        } catch (IOException ex) {
            //Dirección mal formada (conectando a ip: Evaristo)
            //Salta el timeout (Después de la espera de 5 segundos)
            resultado = Var.CONNECTION_TIMEOUT;
        }
        return resultado;
    }

    public void cerrarStreams(){
        pass = "";
        try {
            if(dos != null) {
                dos.close();
            }
            if(ois != null) {
                ois.close();
            }
            socket.close();
        } catch (IOException ex) {}
    }
}

