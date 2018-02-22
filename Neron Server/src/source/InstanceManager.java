package source;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.JOptionPane;

/**
 * Clase que se encarga de que no haya dos instancias del programa siendo
 * ejecutadas al mismo tiempo
 *
 * @author Andres
 */
public class InstanceManager {

    private static ScheduledExecutorService scheduler;
    private static final File fichero = new File("./tmp.tmp");
    //tiempo en que se actualiza el fichero TMP
    private static final int segundos = 5;
    
    public InstanceManager() {
    }

    /**
     * Comprueba si existe el fichero tmp.De no ser así lo crea y escribe el
     * tiempo
     *
     * @return fichero existe o no
     */
    public boolean comprobar() {
        if (fichero.exists()) {
            long tiempo = leer();
            long res = restarTiempo(tiempo);
            if (res < segundos) {
                JOptionPane.showMessageDialog(
                        null, ResourceBundle.getBundle("messages.messages")
                                .getString("MAS_DE_UNA_INSTANCIA"),
                        ResourceBundle.getBundle("messages.messages")
                                .getString("NERON"), 
                        JOptionPane.ERROR_MESSAGE);
                return false;
            } else {
                programarTarea();
                Logger.cierreInesperado();
                return true;
            }
        } else {
            crearTMP();
            programarTarea();
            return true;
        }
    }

    /**
     * Lee el archivo .tmp y devuelve el valor almacenado
     *
     * @return valor almacenado en el tmp
     */
    public long leer() {
        String linea = "0";
        BufferedReader bufferedReader;
        try {
            bufferedReader = new BufferedReader(new FileReader(fichero));
            while (bufferedReader.ready()) {
                linea = bufferedReader.readLine();
            }
            bufferedReader.close();
        } catch (IOException ex) {
            source.Logger.error("InstanceManager", "leer()", 
                    "Error al leer el .tmp", ex.getMessage());
        }
        return Long.parseLong(linea);
    }

    /**
     * Crea el archivo .tmp y actualiza su valor
     */
    public static void programarTarea() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            crearTMP();
        }, 1000, segundos * 1000, TimeUnit.MILLISECONDS); //comienza dentro de 1 segundo y luego se repite cada N segundos
    }

    /**
     * Crea un archivo tmp con el tiempo en milisegundos
     */
    public static void crearTMP() {
        Date fecha = new Date();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fichero))) {
            writer.write(String.valueOf(fecha.getTime()));
            writer.flush();
        } catch (IOException ex) {
            source.Logger.error("InstanceManager", "crearTMP()", 
                    "Error al crear el .tmp", ex.getMessage());
        }
    }
    
    public static void finalizarInstance(){
        scheduler.shutdown();
        if (fichero.exists()) {
            fichero.delete();
        }        
    }

    /**
     * Resta el tiempo actual al almacenado en el .tmp y devuelve el valor
     *
     * @param tiempoActual tiempo actual en milisegundos
     * @return tiempo transcurrido
     */
    public long restarTiempo(long tiempoActual) {
        Date date = new Date();
        long tiempoTMP = date.getTime();
        long tiempo = tiempoTMP - tiempoActual;
        tiempo = tiempo / 1000;
        return tiempo;
    }

    /**
     * Elimina el archivo .tmp si es que existe y finaliza la ejecución de la
     * aplicación
     */
    public void cerrarApp() {
        if (fichero.exists()) {
            fichero.delete();
        }
        System.exit(0);
    }
}
