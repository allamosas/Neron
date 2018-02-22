package server;

import interfaz.MenuPrincipal;
import java.awt.TrayIcon;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import static server.Server.intentos;
import static server.Server.listaNegra;
import source.Configuration;
import source.CryptManager;
import source.FileManager;
import source.PassManager;
import source.Var;

/**
 * Clase o demonio que se pone en escucha para atender las peticiones mientras
 * la aplicación está en ejecución y el usuario ha sido previamente validado.
 * @author Andres
 */
public class Server extends Thread {

    MenuPrincipal menu;
    private int puerto = 30500;
    private final int poolSize = 2;
    private ServerSocket serverSocket;
    private ScheduledExecutorService pool;
    
    public static Map<InetAddress, Integer> listaNegra;
    public static Map<InetAddress, Integer> intentos;

    public Server(MenuPrincipal menu) {
        this.menu = menu;
    }

    @Override
    public void run() {
        puerto = Configuration.getPropPuerto();
        try {
            listaNegra = new HashMap<>();
            intentos = new HashMap<>();
            serverSocket = new ServerSocket(puerto);
            pool = Executors.newScheduledThreadPool(poolSize);
            for (;;) {
                pool.execute(new Handler(serverSocket.accept(), menu));
            }
        } catch (IOException ex) {
            if(pool != null){
                pool.shutdown();
            }
            source.Logger.error("Servidor", "run()", 
                    "Error en la escucha del servidor", ex.getMessage());
        }
    }    
    
    static void bloquear(InetAddress ip) {
        //Se añade la ip bloqueada con el tiempo de espera asociado
        listaNegra.put(ip,(int)Configuration.getPropPenalizacion()[2]);        
        
        Timer timer = new Timer();
        TimerTaskPersonalizado timerTask = new TimerTaskPersonalizado(ip,timer);
        //se lanza un hilo que va haciendo una cuenta atrás para la ip especificada
        timer.scheduleAtFixedRate(timerTask, 0, 1000);          
    }
}

class TimerTaskPersonalizado extends TimerTask {
    private InetAddress ip;   
    private Timer timer;

    public TimerTaskPersonalizado(InetAddress ip, Timer timer) {
        this.ip = ip;
        this.timer = timer;
    }
    
    @Override
    public void run() {
        //Se resta un segundo
        listaNegra.replace(ip,(listaNegra.get(ip) - 1));
        if(listaNegra.get(ip) <= 0) {
            listaNegra.remove(ip);
            intentos.remove(ip);
            timer.cancel();
        }
    }
}

class Handler implements Runnable {

    private final MenuPrincipal menu;
    private final CryptManager cm = new CryptManager();
    private final Socket socket;
    private InputStream is;
    private OutputStream os;
    private DataInputStream dis;
    private DataOutputStream dos;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    
    String pass;

    Handler(Socket socket, MenuPrincipal menu) {
        this.socket = socket;
        this.menu = menu;
    }

    @Override
    public void run() {
        //Se comprueba si la ip esta en la lista negra
            Object[] ob = autenticar();
            if((boolean)ob[0]) { //Se valida si la contraseña es correcta o no
                switch((int)ob[1]){ //Se determina qué es lo que quiere hacer el cliente
                    case -1:
                        //Se manda a paseo al cliente en el caso de que 
                        //haya introducido muchas contraseñas incorrectas
                        break;
                    case 0:
                        probarConexion();
                        break;
                    case 1:
                        notificarEliminacion();
                        eliminarFicheros();
                        break;
                }
            } else {
                notificarFallo();
            }
        cerrarStreams();
    }

    /**
     * Se devuelve al cliente que la conexion es correcta
     */
    public void probarConexion() {
        try {
            dos = new DataOutputStream(socket.getOutputStream());
            dos.write(1);
        } catch (IOException ex) {
            source.Logger.error("Handler", "probarConexion()", 
                    "Error en el DataOutputStream", ex.getMessage());
        }
    }

    /**
     * Se ejecutan las tres funciones de eliminación de ficheros
     */
    public void eliminarFicheros() {
        eliminarFicheros(pass);
        sobreescribirEspacio();
        eliminarSobreescritura();
              
    }

    /**
     * Se autentica al cliente. Se devuelve un objeto compuesto por un booleano
     * que indica si el usuario se ha validado correctamente o no, y un int que
     * dice si el cliente quiere eliminar datos o probar conexion
     * @return 
     */
    private Object[] autenticar() {
        Object[] respuestas = {false, 0};        
            try {
                dis = new DataInputStream(socket.getInputStream());
                int mensaje = dis.read();
                respuestas[1] = mensaje;
                PublicKey publica = cm.generarClaves();

                oos = new ObjectOutputStream(socket.getOutputStream());
                oos.writeObject(publica);

                dis = new DataInputStream(socket.getInputStream());
                int length = dis.readInt();
                if (length > 0) {
                    byte[] message = new byte[length];
                    dis.readFully(message); // read the message

                    pass = cm.descifrarPri(message);

                    PassManager pm = new PassManager();
                    respuestas[0] = pm.validarAutenticidad(pass);
                    oos = new ObjectOutputStream(socket.getOutputStream());
                    int[] solucion = {Var.CONNECTION_FAILED,0};
                    if(!Server.listaNegra.containsKey(socket.getInetAddress())){
                        if((boolean)respuestas[0]) {
                            solucion[0] = Var.CONN_SUCCESS;
                            oos.writeObject(solucion);
                        } else {
                            solucion[0] = Var.WRONG_PASS;
                            oos.writeObject(solucion);
                        }
                    } else {
                        solucion[0] = Var.CONNECTION_REFUSED;
                        solucion[1] = Server.listaNegra.get(socket.getInetAddress());
                        oos.writeObject(solucion);
                    }
                }
            } catch (IOException ex) {
                source.Logger.error("Handler", "autenticar()", 
                        "Error en la autenticación", ex.getMessage());
            }
        return respuestas;
    }
    
    /**
     * Se cierran todos los streams abiertos
     */
    private void cerrarStreams(){
        pass = "";
        try {
            if(is != null) {
                is.close();
            }            
            if(os != null) {
                os.close();
            }           
            if(dis != null) {
                dis.close();
            }           
            if(dos != null) {
                dos.close();
            }           
            if(ois != null) {
                ois.close();
            }           
            if(oos != null) {
                oos.close();
            }
        } catch (IOException ex) {
            source.Logger.error("Handler", "cerrarStreams()", 
                    "Error al cerrar los streams", ex.getMessage());
        }
    }
    
    /**
     * Se eliminan todos los ficheros que se hayan seleccionado y se va
     * informando al cliente del proceso
     * @param pass 
     */
    public void eliminarFicheros(String pass) {
        FileManager fm = new FileManager();
        List<File> ficheros = fm.cargarFicheros(pass);
        if(!ficheros.isEmpty()) {
            List<File> total = new ArrayList<>();
            //Se guardan todos los ficheros y directorios de mayor a menor profundidad en una lista
            ficheros.stream().forEach((f) -> {
                calcularFicheros(f, total);
            });
            float size = total.size();
            List<File> noEliminados = new ArrayList<>();
            //Se calcula de cuanto en cuanto va a aumentar el for
            float centesima = 100/size;
            int anteriorPorcentaje = 0;
            int iterador = 0;
            for(float i = 0; i+centesima <= 100; i = i + centesima) {
                //Si el porcentaje ha subido un 1% o más, se notifica al cliente
                if(anteriorPorcentaje != Math.floor(i)) {
                    anteriorPorcentaje = (int)Math.floor(i);
                    enviarPorcentaje(0,anteriorPorcentaje);
                }
                //Si el fichero existe, se elimina
                if(total.get(iterador).exists()) {
                    total.get(iterador).delete();
                } if(total.get(iterador).exists()) {
                    //Si sigue existiendo, se guarda como no eliminado
                    noEliminados.add(total.get(iterador));
                }
                iterador++;
            }
            //Parche para los casos en los que no se elimina el último directorio
            if(total.get(total.size()-1).exists()) {
                total.get(total.size()-1).delete();
            } if(total.get(total.size()-1).exists()) {
                    noEliminados.add(total.get(total.size()-1));
            }
            //Se actualiza el fichero data
            fm.actualizarDatos(noEliminados, pass);
            notificarNoEliminados(noEliminados.size());
            enviarPorcentaje(0,100);
        }
    }
    
    /**
     * Carga la lista recibida con todos los subdirectorios y ficheros del file 
     * recibido de mayor a menor profundidad
     * @param f
     * @param total 
     */
    private void calcularFicheros(File f, List<File> total) {
        if (f.isDirectory()) {
            for (File c : f.listFiles()) {
                calcularFicheros(c, total);                
            }
        }
        total.add(f);    
    }
    
    /**
     * Sobreescribe todo el espacio disponible en el sistema con 100 ficheros de
     * tamaño variable
     */
    public void sobreescribirEspacio() {   
        //Se crea la carpeta en la que se crearán todos los ficheros de sobreescritura
        File SE = new File("./OW");
        SE.mkdir();
        //Se calcula el espacio disponible en disco y cual sería la centesima parte
        File C = new File("/");
        long libre = C.getFreeSpace();
        long cien = 100;
        long centesima = libre/cien;             
        //Se crean 99 ficheros con la centesima parte del espacio total
        for(int i = 0; i < 99; i++){
            crearFichero(i, centesima);
            enviarPorcentaje(1,i+1);
        }   
        //Se crea un ultimo fichero con el espacio restante en el disco
        //para que no queden bytes libres como resultado de la división
        crearFichero(99, C.getFreeSpace());
        enviarPorcentaje(1,100);
    }
    
    /**
     * Crea un fichero del tamaño en b especificado
     * @param mb 
     */
    public void crearFichero(int id, long b){        
        try (RandomAccessFile f = new RandomAccessFile("./OW/OW" + id, "rw")) {
            f.setLength(b);
        } catch (Exception ex) {
            source.Logger.error("Handler", "crearFichero()", 
                    "Error al crear el fichero de sobreescritura", ex.getMessage());
        }
    }
    
    /**
     * Elimina los 100 ficheros SE de sobreescritura creados anteriormente
     */
    public void eliminarSobreescritura() {        
        for(int i = 0; i < 100; i++){
            File file = new File("./OW/OW" + i);
            if(file.exists()) {
                file.delete();
            }
            enviarPorcentaje(2,i+1);
        }
        File f = new File("./OW");
        f.delete();
    }
    
    /**
     * Se envía el porcentaje de progreso recibido para la barra de progreso
     * recibida.
     * @param barra
     * @param porcentaje 
     */
    public void enviarPorcentaje(int barra, int porcentaje) {        
        try {
            int[] progreso = {barra,porcentaje};
            oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(progreso);
        } catch (IOException ex) {
            source.Logger.error("Handler", "enviarPorcentaje()", 
                    "Error al enviar mediante el ObjectOutputStream", ex.getMessage());
        }
    }

    /**
     * Se notifica al usuario de que el cliente conectado ha lanzado una eliminación
     */
    private void notificarEliminacion() {  
        if((boolean) Configuration.getPropNotificacion()[4]) {
            String info = ResourceBundle.getBundle("messages.messages")
                    .getString("ELIMINACION_EN_CURSO") + "\n" + 
                    socket.getInetAddress() + ":" + socket.getPort();
            menu.getTray().MensajeTrayIcon(info, TrayIcon.MessageType.INFO);            
        }    
    }
    
    /**
     * Se notifica al usuario de que ciertos ficheros previamente seleccionados
     * no han podido ser eliminados, mostrando la cifra exacta de éstos.
     * @param noEliminados 
     */
    private void notificarNoEliminados(int noEliminados) {
        if((boolean) Configuration.getPropNotificacion()[5] && noEliminados > 0) {
            String info = ResourceBundle.getBundle("messages.messages")
                    .getString("NO_ELIMINADOS_COM") + "\n(" + noEliminados + 
                    ResourceBundle.getBundle("messages.messages")
                    .getString("NO_ELIMINADOS");
            menu.getTray().MensajeTrayIcon(info, TrayIcon.MessageType.INFO);            
        }
    }

    /**
     * Se notifica al usuario de que el cliente que ha intentado conectarse al
     * servidor ha introducido una contraseña incorrecta
     */
    private void notificarFallo() {    
        if((boolean) Configuration.getPropNotificacion()[6]) {
            String info = ResourceBundle.getBundle("messages.messages")
                    .getString("CONEXION_FALLIDA") + "\n" + 
                    socket.getInetAddress() + ":" + socket.getPort();
            menu.getTray().MensajeTrayIcon(info, TrayIcon.MessageType.INFO);            
        }
        if((boolean) Configuration.getPropPenalizacion()[0]) {
            InetAddress ip = socket.getInetAddress();
            if(Server.intentos.containsKey(ip)) {
                Server.intentos.replace(ip, Server.intentos.get(ip) + 1);    
            } else {
                Server.intentos.put(ip, 1);    
            }
            if(Server.intentos.get(ip) == (int) Configuration.getPropPenalizacion()[1]) {
                Server.bloquear(ip);
            }
        }
    }
}
