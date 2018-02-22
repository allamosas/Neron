/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package source;

import interfaz.MenuPrincipal;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TooManyListenersException;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class DragNDropPane extends JPanel {

    private TableManager tm;
    private MenuPrincipal principal;
    private DropTarget dropTarget;
    private DropTargetHandler dropTargetHandler;
    private Point dragPoint;
    private boolean dragOver = false;
    private BufferedImage target;
    
    public DragNDropPane() {
        setLayout(new GridBagLayout());
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(800, 400);
    }

    protected DropTarget getMyDropTarget() {
        if (dropTarget == null) {
            dropTarget = new DropTarget(this, 
                    DnDConstants.ACTION_COPY_OR_MOVE, null);
        }
        return dropTarget;
    }

    protected DropTargetHandler getDropTargetHandler() {
        if (dropTargetHandler == null) {
            dropTargetHandler = new DropTargetHandler();
        }
        return dropTargetHandler;
    }

    @Override
    public void addNotify() {
        super.addNotify();
        try {
            getMyDropTarget().addDropTargetListener(getDropTargetHandler());
        } catch (TooManyListenersException ex) {
            source.Logger.error("DragNDropPane", "addNotify()", 
                    "Demasiados Listeners", ex.getMessage());
        }
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        getMyDropTarget().removeDropTargetListener(getDropTargetHandler());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (dragOver) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setColor(new Color(191, 245, 255, 120));
            g2d.fill(new Rectangle(getWidth(), getHeight()));
            if (dragPoint != null && target != null) {
                int x = dragPoint.x;
                int y = dragPoint.y;
                
                g2d.drawImage(target, x, y, this);
            }
            g2d.dispose();
        }
    }

    protected void importFiles(final List lista) {
        Runnable run = () -> {
            File[] files = parseFiles(lista);
            if (tm.insertarFila(files)) {
                principal.cambio();
            }
        };
        SwingUtilities.invokeLater(run);
    }

    private File[] parseFiles(List list) {
        List<File> fileList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            File file = (File) list.get(i);
            fileList.add(file);
        }
        File[] files = fileList.toArray(new File[fileList.size()]);
        return files;
    }

    public void setPrincipal(MenuPrincipal principal) {
        this.principal = principal;
    }

    protected class DropTargetHandler implements DropTargetListener {

        protected void processDrag(DropTargetDragEvent dtde) {
            if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                dtde.acceptDrag(DnDConstants.ACTION_COPY);
            } else {
                dtde.rejectDrag();
            }
        }

        @Override
        public void dragEnter(DropTargetDragEvent dtde) {
            processDrag(dtde);
            SwingUtilities.invokeLater(new DragUpdate(true, dtde.getLocation()));
            repaint();
        }

        @Override
        public void dragOver(DropTargetDragEvent dtde) {
            processDrag(dtde);
            SwingUtilities.invokeLater(new DragUpdate(true, dtde.getLocation()));
            repaint();
        }

        @Override
        public void dropActionChanged(DropTargetDragEvent dtde) {
        }

        @Override
        public void dragExit(DropTargetEvent dte) {
            SwingUtilities.invokeLater(new DragUpdate(false, null));
            repaint();
        }

        @Override
        public void drop(DropTargetDropEvent dtde) {
            SwingUtilities.invokeLater(new DragUpdate(false, null));
            Transferable transferable = dtde.getTransferable();
            if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                dtde.acceptDrop(dtde.getDropAction());
                try {
                    List transferData = (List) transferable
                            .getTransferData(DataFlavor.javaFileListFlavor);
                    if (transferData != null && transferData.size() > 0) {
                        importFiles(transferData);
                        dtde.dropComplete(true);
                    }

                } catch (UnsupportedFlavorException | IOException ex) {
                    source.Logger.error("DragNDropPane", "drop()", 
                            "Error en la transferencia", ex.getMessage());
                }
            } else {
                dtde.rejectDrop();
            }
        }
    }

    public class DragUpdate implements Runnable {

        private final boolean dragOver;
        private final Point dragPoint;

        public DragUpdate(boolean dragOver, Point dragPoint) {
            this.dragOver = dragOver;
            this.dragPoint = dragPoint;
        }

        @Override
        public void run() {
            DragNDropPane.this.dragOver = dragOver;
            DragNDropPane.this.dragPoint = dragPoint;
            DragNDropPane.this.repaint();
        }
    }

    public void setTableManager(TableManager tm) {
        this.tm = tm;
    }

    public TableManager getTableManager() {
        return tm;
    }
}
