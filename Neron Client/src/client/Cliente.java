package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.PublicKey;
import java.util.logging.Level;
import java.util.logging.Logger;
import source.CryptManager;

/**
 * 
 * @author Andres
*/
public class Cliente extends Thread {
    
    private CryptManager cm = new CryptManager();
    private Socket socket;
    private InputStream is;
    private OutputStream os;
    private DataInputStream dis;
    private DataOutputStream dos;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    
    String ip = "192.168.1.4";
    int puerto = 30500;
    String pass;
    
    public Cliente(String nombre) {
        this.pass = nombre;
    }
    
    /**
     * Método que implementa el comportamiento del hilo
    */
    @Override
    public void run() {
        switch(contactar(1)) {
            case 0:
                System.out.println("El resultado ha sido 0");
                break;
            case 1:
//        probarConexion();
                System.out.println("El resultado ha sido 1");
                eliminarFicheros();
                break;
            default:
                System.out.println("excepcion al canto");
                break;
        }
        System.out.println("Se cierran los streams");
        cerrarStreams();
    }
    
    public int contactar(int opcion) {     
        int resultado = -1;
        try {
            socket = new Socket(ip, puerto);
            dos = new DataOutputStream(socket.getOutputStream());
            dos.write(opcion);
            
            ois = new ObjectInputStream(socket.getInputStream());
            PublicKey publica = (PublicKey) ois.readObject();
            CryptManager cm = new CryptManager();
            byte[] ciphertext = cm.cifrarPub(publica, pass); 
            dos = new DataOutputStream(socket.getOutputStream());
            dos.writeInt(ciphertext.length);
            dos.write(ciphertext);
            
            dis = new DataInputStream(socket.getInputStream());
//            System.out.println("\t Cliente " + " - Resultado recibido del Servidor: " + dis.read() + " (0 = mal | 1 = bien)");
            resultado = dis.read();         
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } catch (ClassNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
        return resultado;
    }
    
    /**
     * Método que implementa el envío del mensaje al servidor, la recepción del mensaje de dicho
     * servidor y la muestra del mensaje recibido por consola
     * @param socketCliente Socket que se usa para realizar la comunicación con el servidor
    */
    public void eliminarFicheros () {        
        try {
            ois = new ObjectInputStream(socket.getInputStream());
            int[] progreso = (int[]) ois.readObject();
            while(progreso[0] != 2 || progreso[1] != 100) {                
                ois = new ObjectInputStream(socket.getInputStream());
                progreso = (int[]) ois.readObject();
                switch(progreso[0]) {
                    case 0:                        
                        System.out.println("Eliminando ficheros... " + progreso[1] + "%\r");
                        break;                        
                    case 1:
                        System.out.println("Sobreescribiendo espacio... " + progreso[1] + "%\n");
                        break;                        
                    case 2:
                        System.out.println("Eliminando sobreescritura... " + progreso[1] + "%\n");
                        break;                        
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            System.out.println("El servidor ha finalizado la conexion" + ex);
        }
    }

    public void probarConexion() {      
        try {
            socket = new Socket(ip, puerto);
            dos = new DataOutputStream(socket.getOutputStream());
            dos.write(0);
            
            ois = new ObjectInputStream(socket.getInputStream());
            PublicKey publica = (PublicKey) ois.readObject();
            CryptManager cm = new CryptManager();
            byte[] ciphertext = cm.cifrarPub(publica, pass); 
            dos = new DataOutputStream(socket.getOutputStream());
            dos.writeInt(ciphertext.length);
            dos.write(ciphertext);
            
            dis = new DataInputStream(socket.getInputStream());
            System.out.println("\t Cliente " + " - Resultado recibido del Servidor: " + dis.read() + " (0 = mal | 1 = bien)");
           } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void cerrarStreams(){
        pass = "";
        try {
//            is.close();
//            os.close();
            dis.close();
            dos.close();
            ois.close();
//            oos.close();
            socket.close();
        } catch (IOException ex) {
            System.out.println("Error al cerrar los streams: " + ex);
        }
    }

}
