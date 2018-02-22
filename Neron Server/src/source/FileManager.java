/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package source;

import interfaz.MenuPrincipal;
import interfaz.Perdidos;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.JOptionPane;

/**
 * Clase encargada de gestionar la creación, lectura y escritura de la
 * aplicación
 *
 * @author Andres
 */
public class FileManager {

    CryptManager cm = new CryptManager();

    /**
     * Comprueba si hay una contraseña almacenada
     *
     * @return hay o no hay una contraseña almacenada
     */
    public boolean primerInicio() {
        File file = new File("./user/auth");
        return !(file.exists() && !file.isDirectory());
    }

    /**
     * Devuelve la contraseña almacenada
     *
     * @return hash de la contraseña guardado
     */
    public String getContrasena() {
        String resumen = "";
        try {
            BufferedReader br = new BufferedReader(new FileReader("./user/auth"));
            resumen = br.readLine();
        } catch (FileNotFoundException ex) {
            source.Logger.error("FileManager", "getContrasena()", "No se ha encontrado el fichero", ex.getMessage());
        } catch (IOException ex) {
            source.Logger.error("FileManager", "getContrasena()", "Error al recuperar la contraseña", ex.getMessage());
        }
        return resumen;
    }

    /**
     * Almacena el Hash de la contraseña especificada
     *
     * @param pass contraseña introducida
     */
    public void establecerContrasena(String pass) {
        try {
            File file = new File("./user");
            file.mkdir();
            Path path = Paths.get("./user/auth");
            String cifrado = cm.hash(pass);
            List<String> lines = Arrays.asList(cifrado);
            Files.write(path, lines, Charset.forName("UTF-8"));
        } catch (IOException ex) {
            source.Logger.error("FileManager", "getContrasena()", 
                    "Error al establecer la contraseña", ex.getMessage());
        }
    }

    /**
     * Devuelve un objeto con la información del fichero introducido
     *
     * @param id id de fila
     * @param file fichero
     * @return objeto fila
     */
    public Object[] insertarFicheroDirectorio(int id, File file) {
        String ruta = file.getPath();
        String nombre = file.getName();
        String extension = "Directorio";
        if (file.isFile()) {
            nombre = this.getExtension(file.getName())[0];
            extension = this.getExtension(file.getName())[1].toUpperCase();
        }
        Object[] object = {id, ruta, nombre, extension};
        return object;
    }

    /**
     * Devuelve la extensión del fichero introducido
     *
     * @param nombre
     * @return
     */
    private String[] getExtension(String nombre) {
        int separador = nombre.lastIndexOf(".");
        if (separador != 0) {
            String extension = nombre.substring(separador);
            nombre = nombre.substring(0, separador);
            String[] nomEx = {nombre, extension};
            return nomEx;
        } else {
            String[] nomEx = {nombre, "file"};
            return nomEx;
        }
    }

    /**
     * Introduce la información de la tabla cifrada en un fichero
     *
     * @param datos informacion de la tabla
     * @param pass contraseña introducida
     */
    public void guardarArchivos(String datos, String pass) {
        cm.cifrar(datos, pass);
    }

    /**
     * Devuelve un "churro" con la información de la tabla a partir del fichero
     * almacenado
     *
     * @param pass contraseña introducida
     * @return informacion de la tabla
     */
    public String cargarArchivos(String pass) {
        String saving = "";
        File file = new File("./data/data");
        if (file.exists() && !file.isDirectory()) {
            saving = cm.descifrar(pass);
        }
        return saving;
    }

    /**
     * Devuelve una lista de arrays de objetos con los que crear una tabla
     *
     * @param pass contraseña introducida
     * @return array de objetos de tipo fila 
     */
    public List<Object[]> cargarTabla(String pass) {
        List<Object[]> lista = new ArrayList<>();
        List<File> ficheros = cargarFicheros(pass);
        for (int i = 0; i < ficheros.size(); i++) {
            lista.add(insertarFicheroDirectorio(i + 1, ficheros.get(i)));
        }
        return lista;
    }

    /**
     * Crea una lista de ficheros a partir del fichero en el que se guardan los
     * datos
     *
     * @param pass contraseña introducida
     * @return lista de ficheros
     */
    public List<File> cargarFicheros(String pass) {
        List<String> paths = new ArrayList<>();
        String saving = cargarArchivos(pass);
        String[] filas = saving.split("\\]\\[|[\\[\\]]");
        for (int i = 1; i < filas.length; i++) {
            String[] celda = filas[i].split("\\|");
            paths.add(celda[0]);
        }
        List<File> ficheros = parseFiles(paths);
        return ficheros;
    }

    /**
     * Crea objetos fichero a partir de su ruta comprobando si existe o no
     *
     * @param rutas lista de rutas
     * @return lista de ficheros
     */
    public List<File> parseFiles(List<String> rutas) {
        List<File> ficheros = new ArrayList<>();
        int ficherosDesaparecidos = 0;
        Perdidos perdidos = new Perdidos();        
        for (String path : rutas) {
            File file = new File(path);
            if (file.exists()) {
                ficheros.add(file);
            } else {
                perdidos.editText.setText(
                        perdidos.editText.getText() + path + "\n");
                ficherosDesaparecidos++;
            }
        }
        
        if (ficherosDesaparecidos > 0 && (boolean) Configuration.getPropNotificacion()[0]) {
            JOptionPane.showMessageDialog(null,
                    ResourceBundle.getBundle("messages.messages")
                            .getString("FICHEROS_DESAPARECIDOS") + "("
                    + ficherosDesaparecidos + ResourceBundle
                            .getBundle("messages.messages")
                            .getString("PERDIDOS") + ")",
                    ResourceBundle.getBundle("messages.messages")
                            .getString("NERON"),
                    JOptionPane.WARNING_MESSAGE);
            perdidos.setVisible(true);
        }
        return ficheros;
    }

    /**
     * Actualiza la lista de ficheros almacenados en el fichero cuando se
     * ejecuta la opcion de eliminar todos los archivos seleccionados
     *
     * @param noEliminados lista de ficheros no eliminados
     * @param pass contraseña introducida
     */
    public void actualizarDatos(List<File> noEliminados, String pass) {
        String datos = "";
        if (!noEliminados.isEmpty()) {
            for (File fichero : noEliminados) {
                Object[] ob = insertarFicheroDirectorio(0, fichero);
                for (int j = 1; j < 4; j++) {
                    datos = j == 1 ? datos : datos + "|";
                    datos = datos + ob[j];
                }
            }
        }
        guardarArchivos(datos, pass);
    }
}
