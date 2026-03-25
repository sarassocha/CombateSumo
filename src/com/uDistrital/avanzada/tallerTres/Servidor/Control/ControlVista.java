package com.uDistrital.avanzada.tallerTres.Servidor.Control;

import com.uDistrital.avanzada.tallerTres.Servidor.Vista.VentanaPrincipal;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Controlador de la vista del servidor.
 * Intermediario entre {@link ControlGeneral} y {@link VentanaPrincipal}.
 *
 * @author Sara
 */
public class ControlVista implements ActionListener {

    private VentanaPrincipal vista;
    private ControlGeneral controlGeneral;

    /**
     * Constructor que recibe el control general
     *
     * @param general Control General
     */
    public ControlVista(ControlGeneral general) {
    this.controlGeneral = general;
    this.vista = new VentanaPrincipal();

    this.vista.getBtnIniciar().addActionListener(this);
    this.vista.getBtnSalir().addActionListener(this);
}
    /**
     * Inicia el flujo inicial de la vista, despues de haber cargado el archivo
     *
     * 
     */
    public void iniciarFlujoInicial() {
        File archivo = vista.iniciarConArchivo();

        if (archivo == null) {
            System.exit(0);
        }

        procesarArchivoInicial(archivo);
    }
    /**
     * Procesa el archivo enviado por el JfileChooser
     *
     * 
     */
    private void procesarArchivoInicial(File archivo) {

        if (!archivo.exists()) {
            vista.mostrarError("El archivo seleccionado no existe.");
            System.exit(0);
        }

        if (!archivo.isFile()) {
            vista.mostrarError("El archivo seleccionado no es válido.");
            System.exit(0);
        }

        if (!archivo.getName().toLowerCase().endsWith(".properties")) {
            vista.mostrarError("El archivo seleccionado no es un archivo .properties válido.");
            System.exit(0);
        }

        controlGeneral.cargarProperties(archivo);
    }


    /**
     * Mensaje de error a la vista
     *
     * @param mensaje Mensaje de error
     */
    public void notificarError(String mensaje) {
        vista.mostrarError(mensaje);
    }

    public void registrarLog(String mensaje) {
        vista.agregarLog(mensaje);
    }

    public void mostrarMensaje(String mensaje) {
        vista.mostrarMensaje(mensaje);
    }

    public void mostrarError(String mensaje) {
        vista.mostrarError(mensaje);
    }
    
    public void notificarCargaExitosa() {
        vista.mostrarMensaje("Archivo cargado exitosamente.");
    }

    public void mostrarFondoDohyo(String ruta) {
        vista.setFondoDohyo(ruta);
    }

    public void mostrarGifJugador1(String ruta) {
        vista.setGifJugador1(ruta);
    }

    public void mostrarGifJugador2(String ruta) {
        vista.setGifJugador2(ruta);
    }

    public void mostrarNombreJugador1(String nombre) {
        vista.setNombreJugador1(nombre);
    }

    public void mostrarNombreJugador2(String nombre) {
        vista.setNombreJugador2(nombre);
    }

    private void salirAplicacion() {
        System.exit(0);
    }
    
    /**
     * Maneja los eventos de acción generados por los distintos botones de la
     * interfaz gráfica.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        Object a = e.getSource();
        if (a == vista.getBtnIniciar()) {
            controlGeneral.verificarIniciarCombate();
        } else if (a == vista.getBtnSalir()) {
            salirAplicacion();
        }
    }
}