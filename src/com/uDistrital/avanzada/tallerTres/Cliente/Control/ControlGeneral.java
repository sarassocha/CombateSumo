package com.uDistrital.avanzada.tallerTres.Cliente.Control;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import com.uDistrital.avanzada.tallerTres.Cliente.Modelo.ConexionCliente;

/**
 * Control principal del cliente.
 */
public class ControlGeneral {

    private ControlVista cVista;
    private List<String> listaKimarites;
    private ControlProperties cProps;
    private ConexionCliente conexion;
    private int puerto;
    private String hostname;

    public ControlGeneral() {
        this.cProps = new ControlProperties(this);
        this.cVista = new ControlVista(this);
        this.listaKimarites = new ArrayList<>();
    }

    public void cargarProperties(File archivo) {
        try {
            cProps.cargarDesde(archivo);
            this.listaKimarites = cProps.extraerKimarites();

            if (listaKimarites.isEmpty()) {
                cVista.notificarError("No se encontraron técnicas en el archivo de propiedades.");
                return;
            }

            int puerto = cProps.extraerPuerto();
            this.puerto = puerto;
            this.hostname = cProps.extraerHostname();
            cVista.notificarCargaExitosa();
            cVista.cargarDatosEnVista(listaKimarites, puerto, hostname);

        } catch (IOException ex) {
            cVista.notificarError("Error al leer el archivo: " + ex.getMessage());
        }
    }

    /**
     * Envía los datos del luchador al servidor.
     */
    public void enviarAlServidor(String nombre, int peso, int altura, int victorias) {
        try {
            conexion = new ConexionCliente(hostname, puerto);

            String[] datos = {
                nombre,
                String.valueOf(peso),
                String.valueOf(altura),
                String.valueOf(victorias)
            };

            List<String> tecnicasSeleccionadas = cVista.ObtenerTecnicasSeleccionadas();
            String[] tecnicas = tecnicasSeleccionadas.toArray(new String[0]);

            conexion.enviarDatos(datos, tecnicas);

            new Thread(() -> {
                try {
                    while (true) {
                        String mensaje = conexion.recibirResultado();

                        if (mensaje.equals("Combate en desarrollo")) {
                            cVista.notificarCombateEnDesarrollo();

                        } else if (mensaje.contains("gana con")) {
                            String miNombre = cVista.ObtenerNombre();
                            String nombreGanador = mensaje.split(" ")[0].replace("¡", "");
                            boolean gane = miNombre.equals(nombreGanador);
                            cVista.notificarResultadoFinal(mensaje, gane);
                            conexion.cerrar();
                            break;

                        } else {
                            cVista.notificarResultado(mensaje);
                        }
                    }
                } catch (Exception e) {
                    cVista.notificarError("Error recibiendo datos: " + e.getMessage());
                }
            }).start();

        } catch (Exception e) {
            cVista.notificarError("Error de conexión: " + e.getMessage());
        }
    }
}