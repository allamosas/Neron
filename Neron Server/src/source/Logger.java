/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package source;

import org.apache.log4j.LogManager;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author Andres LLamosas
 */
public class Logger {
    static org.apache.log4j.Logger loggerError = LogManager.getLogger("ERROR");
    
    public static void error(String clase, String metodo, String error, String ex) {   
        PropertyConfigurator.configure("log\\log4j.properties");     
        loggerError.error(error + " en " + metodo + " - (" + clase + ") " + ex);
    }
    
    public static void cierreInesperado() {
        PropertyConfigurator.configure("log\\log4j.properties");     
        loggerError.error("La aplicación no se cerró adecuadamente durante la anterior sesión");
    }
}
