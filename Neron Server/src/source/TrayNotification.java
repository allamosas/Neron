package source;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

/**
 * Clase encargada de gestionar la iconización de la aplicación en el apartado
 * de notificaciones
 *
 * @author Andres
 */
public class TrayNotification {

    private JFrame Login;
    private static JFrame principal;
    private PopupMenu popup = new PopupMenu();
    private Image imagen = new ImageIcon("media/square-icon.png").getImage();
    private final TrayIcon trayIcon = new TrayIcon(imagen, ResourceBundle.getBundle("messages.messages").getString("NERON"), popup);
    /**
     * Constructor de la clase que crea la iconización, los eventos asociados,
     * el menú desplegable y las acciones de cada uno de los botones
     *
     * @param frame menu de la invocacion
     */
    public TrayNotification(JFrame frame) {
        this.Login = frame;
        //comprueba si SystemTray es soportado en el sistema
        if (SystemTray.isSupported()) {
            //obtiene instancia SystemTray
            SystemTray systemtray = SystemTray.getSystemTray();
            //acciones del raton sobre el icono en la barra de tareas
            MouseListener mouseListener = new MouseListener() {

                @Override
                public void mouseClicked(MouseEvent evt) {
                }

                @Override
                public void mouseEntered(MouseEvent evt) {
                }

                @Override
                public void mouseExited(MouseEvent evt) {
                }

                @Override
                public void mousePressed(MouseEvent evt) {
                }

                @Override
                public void mouseReleased(MouseEvent evt) {
                }
            };

            //ACCIONES DEL MENU POPUP
            //Salir de aplicacion
            ActionListener salirListener = new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    new InstanceManager().cerrarApp();
                }
            };

            //Restaurar aplicacion
            ActionListener restaurarListener = (ActionEvent e) -> {
                if (!principal.isVisible()) {
                    Login.setVisible(true);
                    Login.setExtendedState(JFrame.NORMAL);
                    Login.repaint();
                }
            };

            //Se crean los Items del menu PopUp y se añaden
            MenuItem ItemRestaurar = new MenuItem(ResourceBundle
                    .getBundle("messages.messages").getString("ABRIR_INTERFAZ"));
            MenuItem SalirItem = new MenuItem(ResourceBundle
                    .getBundle("messages.messages").getString("SALIR"));

            ItemRestaurar.addActionListener(restaurarListener);
            SalirItem.addActionListener(salirListener);

            popup.add(ItemRestaurar);
            popup.add(SalirItem);

            trayIcon.setImageAutoSize(true);
            trayIcon.addMouseListener(mouseListener);

            //Añade el TrayIcon al SystemTray
            try {
                systemtray.add(trayIcon);
            } catch (AWTException e) {
                System.err.println("Error:" + e.getMessage());
            }
        } else {
            Logger.error("TrayNotification", "TrayNotification", "No se soporta TrayIcon", null);
        }
    }

    public void setPrincipal(JFrame principal) {
        TrayNotification.principal = principal;
    }

    public JFrame getPrincipal() {
        return TrayNotification.principal;
    }

    /**
     * Notifica al usuario con la información especificada
     *
     * @param texto texto a mostrar
     * @param tipo tipo de mensaje
     */
    public void MensajeTrayIcon(String texto, MessageType tipo) {
        trayIcon.displayMessage(ResourceBundle.getBundle("messages.messages").getString("NERON"), texto, tipo);
    }
}
