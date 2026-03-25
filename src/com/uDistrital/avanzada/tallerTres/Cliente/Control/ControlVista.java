package com.uDistrital.avanzada.tallerTres.Cliente.Control;

import com.uDistrital.avanzada.tallerTres.Cliente.Vista.VentanaPrincipal;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

/**
 * Controlador de la vista del cliente.
 */
public class ControlVista implements ActionListener {

    private VentanaPrincipal vista;
    private ControlGeneral controlGeneral;

    public ControlVista(ControlGeneral general) {
        this.controlGeneral = general;
        this.vista = new VentanaPrincipal();

        this.vista.getBtnCargar().addActionListener(this);
        this.vista.getBtnIniciar().addActionListener(this);
        this.vista.getBtnSalir().addActionListener(this);

        // Solo mostrar la ventana, no cargar archivo
        this.vista.setVisible(true);
    }

    public void solicitarCargarArchivo() {
        File archivo = vista.solicitarArchivoPropiedades();

        if (archivo == null) {
            vista.mostrarMensaje("Selección cancelada");
            return;
        }

        if (!archivo.getName().toLowerCase().endsWith(".properties")) {
            vista.mostrarError("Archivo no válido");
            return;
        }

        controlGeneral.cargarProperties(archivo);
    }

    public void notificarError(String mensaje) {
        vista.mostrarError(mensaje);
    }

    public void notificarCargaExitosa() {
        vista.mostrarMensaje("Archivo cargado exitosamente.");
    }

    public void notificarCombateEnDesarrollo() {
        vista.mostrarMensaje("Combate en desarrollo. Esperando resultado...");
    }

    public void notificarResultado(String mensaje) {
        vista.mostrarMensaje(mensaje);
    }

    public void notificarResultadoFinal(String mensaje, boolean gane) {
        if (gane) {
            vista.mostrarMensaje("VICTORIA: " + mensaje);
        } else {
            vista.mostrarMensaje("DERROTA: " + mensaje);
        }
    }

    public void cargarDatosEnVista(List<String> tecnicas, int puerto, String hostname) {
        vista.cargarTecnicas(tecnicas);
        vista.setPuerto(String.valueOf(puerto));
        vista.setHostname(hostname);
    }

    public String ObtenerNombre() {
        return vista.getNombre();
    }

    public int ObtenerPeso() {
        return Integer.parseInt(vista.getPeso());
    }

    public int ObtenerAltura() {
        return Integer.parseInt(vista.getAltura());
    }

    public int ObtenerVictorias() {
        return Integer.parseInt(vista.getVictorias());
    }

    public List<String> ObtenerTecnicasSeleccionadas() {
        return vista.getTecnicasSeleccionadas();
    }

    public boolean validarCampos() {
        return vista.validarCampos();
    }

    public boolean validarSeleccionTecnicas() {
        List<String> seleccionadas = vista.getTecnicasSeleccionadas();

        if (seleccionadas.isEmpty()) {
            vista.mostrarError("Debes seleccionar al menos una técnica.");
            return false;
        }
        return true;
    }

    public void salirAplicacion() {
        System.exit(0);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object a = e.getSource();

        if (a == vista.getBtnCargar()) {
            solicitarCargarArchivo();

        } else if (a == vista.getBtnSalir()) {
            salirAplicacion();

        } else if (a == vista.getBtnIniciar()) {

            if (!validarCampos()) return;
            if (!validarSeleccionTecnicas()) return;

            controlGeneral.enviarAlServidor(
                    ObtenerNombre(),
                    ObtenerPeso(),
                    ObtenerAltura(),
                    ObtenerVictorias()
            );
        }
    }
}