package com.example.andres.botonpanico.source;

import android.widget.ProgressBar;

/**
 * Created by andre on 09/04/2016.
 * Gestor de las animaciones de las barras de progreso
 */
public class ProgressManager {
    public ProgressBar progressBar1;
    public ProgressBar progressBar2;
    public ProgressBar progressBar3;

    Thread vaciar = new Thread();
    Thread llenar = new Thread();

    private boolean eliminando = false;

    public ProgressManager(ProgressBar progress1, ProgressBar progress2, ProgressBar progress3) {
        this.progressBar1 = progress1;
        this.progressBar2 = progress2;
        this.progressBar3 = progress3;
    }

    /**
     * Crea una animación de vaciado de las tres barras de progreso.
     */
    public void vaciar() {
        vaciar = new Thread() {
            public void run() {
                try {
                    if (llenar.isAlive()) {
                        llenar.interrupt();
                    }
                    for (int i = progressBar1.getProgress(); i >= 0; i--) {
                        progressBar1.setProgress(i);
                        progressBar2.setProgress(i);
                        progressBar3.setProgress(i);
                        Thread.sleep(7);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        vaciar.start();
    }

    /**
     * Crea una animación de llenado de las tres barras de progreso
     */
    public void llenar() {
        llenar = new Thread() {
            public void run() {
                try {
                    if (vaciar.isAlive()) {
                        vaciar.interrupt();
                    }
                    for (int i = progressBar1.getProgress(); i <= 100; i++) {
                        progressBar1.setProgress(i);
                        progressBar2.setProgress(i);
                        progressBar3.setProgress(i);
                        Thread.sleep(7);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        llenar.start();
    }

    public void setEliminando(boolean b) {
        this.eliminando = b;
    }

    public boolean isEliminando() {
        return this.eliminando;
    }
}
