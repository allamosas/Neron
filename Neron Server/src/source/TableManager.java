/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package source;

import interfaz.MenuPrincipal;
import java.awt.TrayIcon;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * Clase encargada de gestionar la tabla de la interfaz mediante un DataModel
 *
 * @author Andres
 */
public class TableManager {

    MenuPrincipal menu;
    FileManager fm = new FileManager();
    DefaultTableModel model;
    JTable table;
    String pass;
    TrayNotification tray;
    static Timer timer = new Timer();

    /**
     * Timer que controla cada X segundos si hay ficheros de la tabla
     * desaparecidos
     */
    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            comprobarCambios();
        }
    };

    public TableManager(DefaultTableModel model, MenuPrincipal menu) {
        this.model = model;
        this.menu = menu;
        lanzarTimer();
    }

    /**
     * Si el usuario quiere habilitar la utilidad, se comprueba cada X segundos
     * si algun fichero o directorio de la tabla ha cambiado de lugar o ha sido
     * eliminado
     */
    private void lanzarTimer() {
        Object[] timerParams = Configuration.getPropNotificacion();
        if ((boolean) timerParams[1]) {
            int frecuencia = (int) timerParams[2] * 1000;
            timer.scheduleAtFixedRate(timerTask, frecuencia, frecuencia);
        }
    }
    
    public static void pararTimer(){
        timer.cancel();
    }

    /**
     * Se inserta un nuevo registro en la fila si no se encuentra ya insertado
     *
     * @param files ficheros a insertar
     * @return cambios
     */
    public boolean insertarFila(File[] files) {
        boolean cambios = false;
        for (File file : files) {
            if (!existe(file.getPath())) {
                model.addRow(fm.insertarFicheroDirectorio(
                        (model.getRowCount() + 1), file));
                cambios = true;
            }
        }
        return cambios;
    }

    /**
     * Elimina las filas seleccionadas
     *
     * @param ids id de filas a eliminar
     */
    public void eliminarFilas(List<Integer> ids) {
        for (int i = 0; i < ids.size(); i++) {
            model.removeRow(ids.get(i) - i);
        }
        this.actualizarIds();
    }

    /**
     * Actualiza todas las ids de los registros
     */
    public void actualizarIds() {
        for (int i = 0; i < model.getRowCount(); i++) {
            model.setValueAt(i + 1, i, 0);
        }
    }

    /**
     * Almacena la información de la tabla en un fichero cifrado
     */
    public void guardarTabla() {
        String saving = "";
        for (int i = 0; i < model.getRowCount(); i++) {
            saving = saving + "[";
            for (int j = 1; j < model.getColumnCount(); j++) {
                saving = j == 1 ? saving : saving + "|";
                saving = saving + model.getValueAt(i, j);
            }
            saving = saving + "]";
        }
        fm.guardarArchivos(saving, pass);
    }

    /**
     * Carga la tabla a partir de un fichero cifrado
     */
    public void cargarTabla() {
        vaciar();
        fm.cargarTabla(pass).stream().forEach((ob) -> {
            model.addRow(ob);
        });
        guardarTabla();
    }

    /**
     * Comprueba si hay un registro ya insertado en la tabla
     *
     * @param path ruta
     * @return existe o no
     */
    public boolean existe(String path) {
        for (int i = 0; i < model.getRowCount(); i++) {
            if (model.getValueAt(i, 1).equals(path)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Vacía todo el contenido de la tabla
     */
    public void vaciar() {
        List<Integer> ids = new ArrayList<>();
        for (int i = 0; i < model.getRowCount(); i++) {
            ids.add(i);
        }
        eliminarFilas(ids);
    }

    /**
     * Comprueba si hay alguna fila de la tabla seleccionada
     *
     * @param table tabla principal
     * @return hay o no hay filas
     */
    public boolean isAnyRowSelected(JTable table) {
        for (int i = 0; i < table.getRowCount(); i++) {
            if (table.isRowSelected(i)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Comprueba si hay cambios en la tabla. Si los hay, elimina los registros
     * de la tabla y notifica al usuario
     */
    public void comprobarCambios() {
        List<Integer> posiciones = ficherosDesaparecidos();
        if (!posiciones.isEmpty()) {
            eliminarFilas(posiciones);
            String info = ResourceBundle.getBundle("messages.messages").getString("FICHEROS_DESAPARECIDOS") + "\n(" + posiciones.size() + ResourceBundle.getBundle("messages.messages").getString("PERDIDOS") + ")";
            menu.getTray().MensajeTrayIcon(info, TrayIcon.MessageType.INFO);
        }
    }

    /**
     * Devuelve una lista con la posición en la tabla en la que hay ficheros
     * eliminados o con rutas incorrectas
     *
     * @return ficheros desaparecidos o mal escritos
     */
    public List<Integer> ficherosDesaparecidos() {
        List<Integer> posiciones = new ArrayList<>();
        List<String> paths = new ArrayList<>();
        for (int i = 0; i < model.getRowCount(); i++) {
            paths.add(model.getValueAt(i, 1).toString());
        }
        for (int i = 0; i < paths.size(); i++) {
            File f = new File(paths.get(i));
            if (!f.exists()) {
                posiciones.add(i);
            }
        }
        return posiciones;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

}
