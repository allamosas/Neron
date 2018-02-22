/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package source;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 * Clase encargada de gestionar la configuración de la aplicación guardada por
 * el usuario mediante ficheros .properties almacenados
 *
 * @author Andres LLamosas
 */
public class Configuration {

    /**
     * Se crean todos los ficheros .properties necesarios por la aplicación. Si
     * éstos ya existen se reestablecen a sus valores por defecto.
     */
    public static void crearProperties() {
        crearPropIdioma(null);
        crearPropTema(null);
        crearPropPuerto(null);
        crearPropCierre(true, false);
        crearPropNotificaciones(null);
        crearPropPenalizacion(null);
    }

    /**
     * Se crea el directorio padre conf si es que no existe
     */
    private static void crearConf() {
        File conf = new File("./conf");
        conf.mkdir();
    }

    /**
     * Crea el fichero .properties relacionado con el idioma almacenando el
     * idioma recibido. En caso de no recibir nada pone inglés por defecto
     *
     * @param idioma locale del idioma seleccionado
     */
    public static void crearPropIdioma(String idioma) {
        OutputStream out = null;
        crearConf();
        try {
            idioma = idioma == null ? "en" : idioma;
            Properties props = new Properties();
            props.setProperty("Language", idioma);
            File f = new File("conf/language.properties");
            out = new FileOutputStream(f);
            props.store(out, "Nerón 2016 - Andres Llamosas");
        } catch (FileNotFoundException ex) {
            source.Logger.error("Configuration", "crarPropIdioma()", 
                    "No se encuentra language.properties", ex.getMessage());
        } catch (IOException ex) {
            source.Logger.error("Configuration", "crarPropIdioma()", 
                    "Error al crear language.properties", ex.getMessage());
        } finally {
            try {
                out.close();
            } catch (IOException ex) {
                source.Logger.error("Configuration", "crarPropIdioma()", 
                        "Error al crear language.properties", ex.getMessage());
            }
        }
    }

    /**
     * Crea el fichero .properties relacionado con el tema de la aplicacióne
     * almacenando el nombre del tema recibido. En caso de no recibir nada pone
     * el tema por defecto
     *
     * @param tema nombre del tema usado
     */
    public static void crearPropTema(String tema) {
        OutputStream out = null;
        crearConf();
        try {
            tema = tema == null ? "Default" : tema;
            Properties props = new Properties();
            props.setProperty("Theme", tema);
            File f = new File("conf/theme.properties");
            out = new FileOutputStream(f);
            props.store(out, "Nerón 2016 - Andres Llamosas");
        } catch (FileNotFoundException ex) {
            source.Logger.error("Configuration", "crearPropTema()", 
                    "No se encuentra theme.properties", ex.getMessage());
        } catch (IOException ex) {
            source.Logger.error("Configuration", "crearPropTema()", 
                    "Error al crear theme.properties", ex.getMessage());
        } finally {
            try {
                out.close();
            } catch (IOException ex) {
                source.Logger.error("Configuration", "crearPropTema()", 
                        "Error al crear theme.properties", ex.getMessage());
            }
        }
    }

    /**
     * Crea el fichero .properties relacionado con el puerto de escucha del
     * servidor. almacenando el número de puerto recibido. Si no recibe nada se
     * establece el 30500 por defecto.
     *
     * @param puerto numero de puerto
     */
    public static void crearPropPuerto(String puerto) {
        OutputStream out = null;
        crearConf();
        try {
            puerto = puerto == null ? "30500" : puerto;
            Properties props = new Properties();
            props.setProperty("Port", puerto);
            File f = new File("conf/port.properties");
            out = new FileOutputStream(f);
            props.store(out, "Nerón 2016 - Andres Llamosas");
        } catch (FileNotFoundException ex) {
            source.Logger.error("Configuration", "crearPropPuerto()", 
                    "No se encuentra port.properties", ex.getMessage());
        } catch (IOException ex) {
            source.Logger.error("Configuration", "crearPropPuerto()", 
                    "Error al crear port.properties", ex.getMessage());
        } finally {
            try {
                out.close();
            } catch (IOException ex) {
                source.Logger.error("Configuration", "crearPropPuerto()", 
                        "Error al crear port.properties", ex.getMessage());
            }
        }
    }
    
    /**
     * Crea el fichero closing.properties en el que se almacena
     * el comportamiento al cerrar la aplicación
     * 
     * @param minimizar minimizar o no minimizar al salir
     * @param inicio ejecutar en inicio
     */
    public static void crearPropCierre(boolean minimizar, boolean inicio) {
        OutputStream out = null;
        crearConf();
        try {
            Properties props = new Properties();
            props.setProperty("Minimize", Boolean.toString(minimizar));
            props.setProperty("Startup", Boolean.toString(inicio));
            File f = new File("conf/closing.properties");
            out = new FileOutputStream(f);
            props.store(out, "Nerón 2016 - Andres Llamosas");
        } catch (FileNotFoundException ex) {
            source.Logger.error("Configuration", "crearPropStartup()", 
                    "No se encuentra startup.properties", ex.getMessage());
        } catch (IOException ex) {
            source.Logger.error("Configuration", "crearPropStartup()", 
                    "Error al crear startup.properties", ex.getMessage());
        } finally {
            try {
                out.close();
            } catch (IOException ex) {
                source.Logger.error("Configuration", "crearPropStartup()", 
                        "Error al crear startup.properties", ex.getMessage());
            }
        }
    }    

    /**
     * Crea un fichero .properties con la configuración de notificaciones
     * recibida del usuario. Si no se recibe nada, se establece una por defecto.
     *
     * @param notificaciones valores de las noritificaciones
     */
    public static void crearPropNotificaciones(String[] notificaciones) {
        String[] defecto = {"true", "true", "10", "false", "false", "false", "false"};
        OutputStream out = null;
        crearConf();
        try {
            notificaciones = notificaciones == null ? defecto : notificaciones;
            Properties props = new Properties();
            props.setProperty("MissingStartUp", notificaciones[0]);
            props.setProperty("MissingRunning", notificaciones[1]);
            props.setProperty("Frequency", notificaciones[2]);
            props.setProperty("DeleteNotification", notificaciones[3]);
            props.setProperty("DeletedFiles", notificaciones[4]);
            props.setProperty("UndeletedFiles", notificaciones[5]);
            props.setProperty("ConnectingDevice", notificaciones[6]);
            File f = new File("conf/notification.properties");
            out = new FileOutputStream(f);
            props.store(out, "Nerón 2016 - Andres Llamosas");
        } catch (FileNotFoundException ex) {
            source.Logger.error("Configuration", "crearPropNotificaciones()", 
                    "No se encuentra notification.properties", ex.getMessage());
        } catch (IOException ex) {
            source.Logger.error("Configuration", "crearPropNotificaciones()", 
                    "Error al crear notification.properties", ex.getMessage());
        } finally {
            try {
                out.close();
            } catch (IOException ex) {
                source.Logger.error("Configuration", "crearPropNotificaciones()", 
                        "Error al crear notification.properties", ex.getMessage());
            }
        }
    }

    /**
     * Crea un fichero .properties con la configuración de notificaciones
     * recibida del usuario. Si no se recibe nada, se establece una por defecto.
     *
     * @param penalizacion valores de las notificaciones
     */
    public static void crearPropPenalizacion(String[] penalizacion) {            
        String[] defecto = {"true", "4", "25"};
        OutputStream out = null;
        crearConf();
        try {
            penalizacion = penalizacion == null ? defecto : penalizacion;
            Properties props = new Properties();
            props.setProperty("PenaltyEnabled", penalizacion[0]);
            props.setProperty("TriesNumber", penalizacion[1]);
            props.setProperty("TimePenalty", penalizacion[2]);
            File f = new File("conf/penalty.properties");
            out = new FileOutputStream(f);
            props.store(out, "Nerón 2016 - Andres Llamosas");
        } catch (FileNotFoundException ex) {
            source.Logger.error("Configuration", "crearPropPenalizacion()", 
                    "No se encuentra penalty.properties", ex.getMessage());
        } catch (IOException ex) {
            source.Logger.error("Configuration", "crearPropPenalizacion()", 
                    "Error al crear penalty.properties", ex.getMessage());
        } finally {
            try {
                out.close();
            } catch (IOException ex) {
                source.Logger.error("Configuration", "crearPropPenalizacion()", 
                        "Error al crear penalty.properties", ex.getMessage());
            }
        }
    }

    /**
     * Devuelve el idioma almacenado en .properties
     *
     * @return lang
     */
    public static String getPropIdioma() {
        String lang = "";
        try {
            Properties props = new Properties();
            File f = new File("./conf/language.properties");
            InputStream is = new FileInputStream(f);
            props.load(is);
            lang = props.getProperty("Language", "en");
        } catch (FileNotFoundException ex) {
            source.Logger.error("Configuration", "getPropIdioma()", 
                    "No se encuentra language.properties", ex.getMessage());
        } catch (IOException ex) {
            source.Logger.error("Configuration", "getPropIdioma()", 
                    "Error al cargar language.properties", ex.getMessage());
        }
        return lang;
    }

    /**
     * Devuelve el tema almacenado en .properties
     *
     * @return theme
     */
    public static String getPropTema() {
        String theme = "";
        try {
            Properties props = new Properties();
            File f = new File("./conf/theme.properties");
            InputStream is = new FileInputStream(f);
            props.load(is);
            theme = props.getProperty("Theme", "Default");
        } catch (FileNotFoundException ex) {
            source.Logger.error("Configuration", "getPropTema()", 
                    "No se encuentra theme.properties", ex.getMessage());
        } catch (IOException ex) {
            source.Logger.error("Configuration", "getPropTema()", 
                    "Error al cargar theme.properties", ex.getMessage());
        }
        return theme;
    }

    /**
     * Devuelve el puerto almacenado en .properties
     *
     * @return port
     */
    public static int getPropPuerto() {
        int port = 30500;
        try {
            Properties props = new Properties();
            File f = new File("./conf/port.properties");
            InputStream is = new FileInputStream(f);
            props.load(is);
            port = Integer.parseInt(props.getProperty("Port", "3500"));
        } catch (FileNotFoundException ex) {
            source.Logger.error("Configuration", "getPropPuerto()", 
                    "No se encuentra port.properties", ex.getMessage());
        } catch (IOException ex) {
            source.Logger.error("Configuration", "getPropPuerto()", 
                    "Error al cargar port.properties", ex.getMessage());
        }
        return port;
    }
    
    /**
     * Devuelve el modo de inicio almacenado en .properties
     *
     * @return minimize
     */
    public static boolean[] getPropCierre() {
        boolean[] cierre = {true,false};
        try {
            Properties props = new Properties();
            File f = new File("./conf/closing.properties");
            InputStream is = new FileInputStream(f);
            props.load(is);
            cierre[0] = Boolean.parseBoolean(props.getProperty("Minimize", "true"));
            cierre[1] = Boolean.parseBoolean(props.getProperty("Startup", "false"));
        } catch (FileNotFoundException ex) {
            source.Logger.error("Configuration", "getPropCierre()", 
                    "No se encuentra startup.properties", ex.getMessage());
        } catch (IOException ex) {
            source.Logger.error("Configuration", "getPropCierre()", 
                    "Error al cargar startup.properties", ex.getMessage());
        }
        return cierre;
    }

    /**
     * Devuelve la configuración de notificaciones almacenada en .properties
     *
     * @return not
     */
    public static Object[] getPropNotificacion() {
        Object[] not = {true, true, 10, false, false, false, false};
        try {
            Properties props = new Properties();
            File f = new File("./conf/notification.properties");
            InputStream is = new FileInputStream(f);
            props.load(is);
            not[0] = Boolean.parseBoolean(props.getProperty("MissingStartUp", "true"));
            not[1] = Boolean.parseBoolean(props.getProperty("MissingRunning", "true"));
            not[2] = Integer.parseInt(props.getProperty("Frequency", "10"));
            not[3] = Boolean.parseBoolean(props.getProperty("DeleteNotification", "false"));
            not[4] = Boolean.parseBoolean(props.getProperty("DeletedFiles", "false"));
            not[5] = Boolean.parseBoolean(props.getProperty("UndeletedFiles", "false"));
            not[6] = Boolean.parseBoolean(props.getProperty("ConnectingDevice", "false"));
        } catch (FileNotFoundException ex) {
            source.Logger.error("Configuration", "getPropNotificacion()", 
                    "No se encuentra notification.properties", ex.getMessage());
        } catch (IOException ex) {
            source.Logger.error("Configuration", "getPropNotificacion()", 
                    "Error al cargar notification.properties", ex.getMessage());
        }
        return not;
    }
    
    /**
     * Devuelve la configuración de penalizaciones almacenada en .properties
     *
     * @return not
     */
    public static Object[] getPropPenalizacion() {
        Object[] not = {true, 4, 25};
        try {
            Properties props = new Properties();
            File f = new File("./conf/penalty.properties");
            InputStream is = new FileInputStream(f);
            props.load(is);
            not[0] = Boolean.parseBoolean(props.getProperty("PenaltyEnabled", "true"));
            not[1] = Integer.parseInt(props.getProperty("TriesNumber", "4"));
            not[2] = Integer.parseInt(props.getProperty("TimePenalty", "25"));
        } catch (FileNotFoundException ex) {
            source.Logger.error("Configuration", "getPropPenalizacion()", 
                    "No se encuentra penalty.properties", ex.getMessage());
        } catch (IOException ex) {
            source.Logger.error("Configuration", "getPropPenalizacion()", 
                    "Error al cargar penalty.properties", ex.getMessage());
        }
        return not;
    }

    /**
     * Recupera el javahelp asociado al idioma seleccionado
     * @return ruta ruta al javahelp adecuado
     */
    public static String getJavaHelp() {
        String ruta = "help/english/help_set.hs";
        switch(getPropIdioma()) {
            case "zh":
                ruta = "help/chinese/help_set.hs";
                break;
            case "en":
                ruta = "help/english/help_set.hs";
                break;
            case "fr":
                ruta = "help/french/help_set.hs";
                break;
            case "de":
                ruta = "help/german/help_set.hs";
                break;
            case "it":
                ruta = "help/italian/help_set.hs";
                break;
            case "es":
                ruta = "help/spanish/help_set.hs";
                break;
            case "ru":
                ruta = "help/russian/help_set.hs";
                break;
            case "tg":
                ruta = "help/tagik/help_set.hs";
                break;
            case "zu":
                ruta = "help/zulu/help_set.hs";
                break;
        }
        return ruta;
    }
    
    
}
