/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package source;

import java.awt.Color;
import java.util.Arrays;
import javax.swing.JPasswordField;

/**
 * Clase encargada de gestionar las contraseñas escritas en la interfaz y las
 * recibidas por el fileManager
 *
 * @author Andres
 */
public class PassManager {

    FileManager fm = new FileManager();
    CryptManager cm = new CryptManager();
    String regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
    //^      # principio de string
    //(?=.*[0-9])      # debe haber por lo menos un digito
    //(?=.*[a-z])      # debe haber por lo menos una minuscula
    //(?=.*[A-Z])      # debe haber por lo menos una mayuscula
    //(?=.*[!@#$%/^&+=,;:\\.])      # debe haber por lo menos un caracter especial
    //(?=\S+$)      # no debe haber espacios en blanco
    //.{8,}      # debe tener como minimo 8 caracteres de longitud
    //$      # final de string

    /**
     * Comprueba que la complejidad de las contraseñas del primer inicio de la
     * aplicación cumplen el mínimo y éstas son iguales
     *
     * @param nueva primera contraseña
     * @param repeticion segunda contraseña
     * @return resultado de la validacion
     */
    public int validarComplejidad(JPasswordField nueva, JPasswordField repeticion) {
        int resultado = -1;
        nueva.setBackground(Color.decode("#FFABAB"));
        repeticion.setBackground(Color.decode("#FFABAB"));
        if (nueva.getText().equals(repeticion.getText())) {
            resultado = 6;
            if (nueva.getText().matches("^(?=.*[0-9]).+$")) {
                resultado = 5;
                if (nueva.getText().matches("^(?=.*[a-z]).+$")) {
                    resultado = 4;
                    if (nueva.getText().matches("^(?=.*[A-Z]).+$")) {
                        resultado = 3;
                        if (nueva.getText().matches("^(?=.*[!@#$%/^&+=,;:\\.]).+$")) {
                            resultado = 2;
                            if (nueva.getText().matches("^(?=\\S+$).+$")) {
                                resultado = 1;
                                if (nueva.getText().matches("^.{8,}$")) {
                                    resultado = 0;
                                    nueva.setBackground(Color.white);
                                    repeticion.setBackground(Color.white);
                                }
                            }
                        }
                    }
                }
            }
        }
        return resultado;
    }

    /**
     * Comprueba que el hash de la contraseña introducida es igual al hash
     * almacenado de la anterior contraseña
     *
     * @param pass contraseña introducida
     * @return validez
     */
    public boolean validarAutenticidad(String pass) {
        return Arrays.equals(cm.hash(pass).toCharArray(), fm.getContrasena().toCharArray());
    }

    /**
     * Comprueba si es la primera vez que se accede a la aplicación
     *
     * @return primer inicio o no
     */
    public boolean primerInicio() {
        return fm.primerInicio();
    }

    /**
     * Establece una nueva contraseña
     *
     * @param jPass contraseña introducida
     */
    public void establecerContrasena(JPasswordField jPass) {
        fm.establecerContrasena(jPass.getText());
    }
}
