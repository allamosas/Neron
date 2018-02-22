/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package source;

import de.javasoft.plaf.synthetica.SyntheticaAluOxideLookAndFeel;
import de.javasoft.plaf.synthetica.SyntheticaBlackEyeLookAndFeel;
import de.javasoft.plaf.synthetica.SyntheticaBlackMoonLookAndFeel;
import de.javasoft.plaf.synthetica.SyntheticaBlackStarLookAndFeel;
import de.javasoft.plaf.synthetica.SyntheticaClassyLookAndFeel;
import de.javasoft.plaf.synthetica.SyntheticaOrangeMetallicLookAndFeel;
import de.javasoft.plaf.synthetica.SyntheticaWhiteVisionLookAndFeel;
import interfaz.MenuPrincipal;
import java.text.ParseException;
import javax.swing.ImageIcon;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Clase encargada de la gestión de temas de la aplicacón y los colores de los
 * iconos correspondientes al tema
 *
 * @author Andres LLamosas
 */
public class LookNFeel {

    static ImageIcon lightNeron = new ImageIcon("media/lightNeron.png");
    static ImageIcon lightNeronZh = new ImageIcon("media/lightNeronChino.png");
    static ImageIcon lightAdd = new ImageIcon("media/lightAdd.png");
    static ImageIcon lightSave = new ImageIcon("media/lightSave.png");
    static ImageIcon lightSaved = new ImageIcon("media/lightSaved.png");
    static ImageIcon lightDelete = new ImageIcon("media/lightDelete.png");
    static ImageIcon lightDeleteFocus = new ImageIcon("media/lightDeleteFocus.png");
    static ImageIcon lightSettings = new ImageIcon("media/lightSettings.png");

    static ImageIcon darkNeron = new ImageIcon("media/darkNeron.png");
    static ImageIcon darkNeronZh = new ImageIcon("media/darkNeronChino.png");
    static ImageIcon darkAdd = new ImageIcon("media/darkAdd.png");
    static ImageIcon darkSave = new ImageIcon("media/darkSave.png");
    static ImageIcon darkSaved = new ImageIcon("media/darkSaved.png");
    static ImageIcon darkDelete = new ImageIcon("media/darkDelete.png");
    static ImageIcon darkDeleteFocus = new ImageIcon("media/darkDeleteFocus.png");
    static ImageIcon darkSettings = new ImageIcon("media/darkSettings.png");

    /**
     * Se lee el tema seleccionado en el fichero temas de configuración y se
     * establece en la aplicación. Si el fichero no existe se establece el tema
     * por defecto del sistema operativo.
     */
    public static void setLookAndFeel() {
        String tema = Configuration.getPropTema();
        switch (tema) {
            case "AluOxide":
                setAluOxideLookAndFeel();
                break;
            case "BlackEye":
                setBlackEyeLookAndFeel();
                break;
            case "BlackMoon":
                setBlackMoonLookAndFeel();
                break;
            case "BlackStar":
                setBlackStarLookAndFeel();
                break;
            case "Classy":
                setClassyLookAndFeel();
                break;
            case "OrangeMetallic":
                setOrangeMetallicLookAndFeel();
                break;
            case "WhiteVision":
                setWhiteVisionLookAndFeel();
                break;
            default:
                break;
        }
    }

    /**
     * Establece el look and feel de AluOxide
     */
    private static void setAluOxideLookAndFeel() {
        try {
            javax.swing.UIManager.setLookAndFeel(new SyntheticaAluOxideLookAndFeel());
        } catch (UnsupportedLookAndFeelException ex) {
            source.Logger.error("LookNFeel", "setAluOxideLookAndFeel()", "No se soporta este LookAndFeel", ex.getMessage());
        } catch (ParseException ex) {
            source.Logger.error("LookNFeel", "setAluOxideLookAndFeel()", "Error al parsear el LookAndFeel", ex.getMessage());
        }
    }

    /**
     * Establece el look and feel de BlackEye
     */
    private static void setBlackEyeLookAndFeel() {
        try {
            javax.swing.UIManager.setLookAndFeel(new SyntheticaBlackEyeLookAndFeel());
        } catch (UnsupportedLookAndFeelException ex) {
            source.Logger.error("LookNFeel", "setBlackEyeLookAndFeel()", "No se soporta este LookAndFeel", ex.getMessage());
        } catch (ParseException ex) {
            source.Logger.error("LookNFeel", "setBlackEyeLookAndFeel()", "Error al parsear el LookAndFeel", ex.getMessage());
        }
    }

    /**
     * Establece el look and feel de BlackMoon
     */
    private static void setBlackMoonLookAndFeel() {
        try {
            javax.swing.UIManager.setLookAndFeel(new SyntheticaBlackMoonLookAndFeel());
        } catch (UnsupportedLookAndFeelException ex) {
            source.Logger.error("LookNFeel", "setBlackMoonLookAndFeel()", "No se soporta este LookAndFeel", ex.getMessage());
        } catch (ParseException ex) {
            source.Logger.error("LookNFeel", "setBlackMoonLookAndFeel()", "Error al parsear el LookAndFeel", ex.getMessage());
        }
    }

    /**
     * Establece el look and feel de BlackStar
     */
    private static void setBlackStarLookAndFeel() {
        try {
            javax.swing.UIManager.setLookAndFeel(new SyntheticaBlackStarLookAndFeel());
        } catch (UnsupportedLookAndFeelException ex) {
            source.Logger.error("LookNFeel", "setBlackStarLookAndFeel()", "No se soporta este LookAndFeel", ex.getMessage());
        } catch (ParseException ex) {
            source.Logger.error("LookNFeel", "setBlackStarLookAndFeel()", "Error al parsear el LookAndFeel", ex.getMessage());
        }
    }

    /**
     * Establece el look and feel de Classy
     */
    private static void setClassyLookAndFeel() {
        try {
            javax.swing.UIManager.setLookAndFeel(new SyntheticaClassyLookAndFeel());
        } catch (UnsupportedLookAndFeelException ex) {
            source.Logger.error("LookNFeel", "setClassyLookAndFeel()", "No se soporta este LookAndFeel", ex.getMessage());
        } catch (ParseException ex) {
            source.Logger.error("LookNFeel", "setClassyLookAndFeel()", "Error al parsear el LookAndFeel", ex.getMessage());
        }
    }

    /**
     * Establece el look and feel de OrangeMetallic
     */
    private static void setOrangeMetallicLookAndFeel() {
        try {
            javax.swing.UIManager.setLookAndFeel(new SyntheticaOrangeMetallicLookAndFeel());
        } catch (UnsupportedLookAndFeelException ex) {
            source.Logger.error("LookNFeel", "setOrangeMetallicLookAndFeel()", "No se soporta este LookAndFeel", ex.getMessage());
        } catch (ParseException ex) {
            source.Logger.error("LookNFeel", "setOrangeMetallicLookAndFeel()", "Error al parsear el LookAndFeel", ex.getMessage());
        }
    }

    /**
     * Establece el look and feel de WhiteVision
     */
    private static void setWhiteVisionLookAndFeel() {
        try {
            javax.swing.UIManager.setLookAndFeel(new SyntheticaWhiteVisionLookAndFeel());
        } catch (UnsupportedLookAndFeelException ex) {
            source.Logger.error("LookNFeel", "setWhiteVisionLookAndFeel()", "No se soporta este LookAndFeel", ex.getMessage());
        } catch (ParseException ex) {
            source.Logger.error("LookNFeel", "setWhiteVisionLookAndFeel()", "Error al parsear el LookAndFeel", ex.getMessage());
        }
    }

    //Los métodos get devuelven el color de icono correspondiente al tema elegido
    public static ImageIcon getNeron() {
        String tema = Configuration.getPropTema();
        if (tema.equals("BlackEye")) {
            if(Configuration.getPropIdioma().equals("zh")) {
                return darkNeronZh;                
            } else {
                return darkNeron;
            }
        } else {
            if(Configuration.getPropIdioma().equals("zh")) {
                return lightNeronZh;                
            } else {
                return lightNeron;
            }
        }
    }
    
    public static ImageIcon getAdd() {
        if (isDarkTheme()) {
            return darkDelete;
        } else {
            return lightAdd;
        }
    }
    
    public static ImageIcon getConfig() {
        if (isDarkTheme()) {
            return darkSettings;
        } else {
            return lightSettings;
        }
    }

    public static ImageIcon getDelete() {
        if (isDarkTheme()) {
            return darkDelete;
        } else {
            return lightDelete;
        }
    }

    public static ImageIcon getDeleteFocus() {
        if (isDarkTheme()) {
            return darkDeleteFocus;
        } else {
            return lightDeleteFocus;
        }
    }

    public static ImageIcon getSave() {
        if (isDarkTheme()) {
            return darkSave;
        } else {
            return lightSave;
        }
    }

    public static ImageIcon getSaved() {
        if (isDarkTheme()) {
            return darkSaved;
        } else {
            return lightSaved;
        }
    }

    public static void setIcons(MenuPrincipal menu) {
        if (isDarkTheme()) {
            menu.btnNuevo.setIcon(darkAdd);
            menu.btnEliminar.setIcon(darkDelete);
            menu.btnGuardar.setIcon(darkSaved);
            menu.btnOpciones.setIcon(darkSettings);
        } else {
            menu.btnNuevo.setIcon(lightAdd);
            menu.btnEliminar.setIcon(lightDelete);
            menu.btnGuardar.setIcon(lightSaved);
            menu.btnOpciones.setIcon(lightSettings);
        }
    }
    
    

    /**
     * Se comprueba si el tema seleccionado es un tema oscuro o uno claro
     *
     * @return
     */
    private static boolean isDarkTheme() {
        String tema = Configuration.getPropTema();
        return tema.equals("AluOxide") || tema.equals("BlackEye")
                || tema.equals("BlackMoon") || tema.equals("BlackStar")
                || tema.equals("Classy");
    }
}
