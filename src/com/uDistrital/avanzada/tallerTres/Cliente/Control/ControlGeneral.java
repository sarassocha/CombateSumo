package com.uDistrital.avanzada.tallerTres.Cliente.Control;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import javax.swing.SwingWorker;

import com.uDistrital.avanzada.tallerTres.Cliente.Modelo.ConexionCliente;

/**
 * Control principal del cliente.
 */
public class ControlGeneral {

    private List<String> listaKimarites;
    private ControlProperties cProps;
    private ConexionCliente conexion;
    private int puerto;
    private String hostname;
    private ControlVista cVista;

    public ControlGeneral() {
        this.cProps = new ControlProperties(this);
        this.listaKimarites = new ArrayList<>();
        this.cVista = new ControlVista(this);
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
     * Envía los datos del luchador al servidor y escucha resultados.
     * Usa SwingWorker para no bloquear el EDT y recibir múltiples mensajes.
     * Toda la comunicación se hace a través de ConexionCliente.
     */
    public void enviarAlServidor(String nombre, int peso, int altura, int victorias) {
        List<String> tecnicasSeleccionadas = cVista.ObtenerTecnicasSeleccionadas();
        
        SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    conexion = new ConexionCliente(hostname, puerto);

                    String[] datos = {
                        nombre,
                        String.valueOf(peso),
                        String.valueOf(altura),
                        String.valueOf(victorias)
                    };
                    String[] tecnicas = tecnicasSeleccionadas.toArray(new String[0]);

                    // Enviar datos a través de ConexionCliente
                    conexion.enviarDatos(datos, tecnicas);

                    // Escuchar mensajes hasta que la conexión se cierre o recibamos mensaje final
                    while (true) {
                        try {
                            // Recibir mensaje a través de ConexionCliente
                            String mensaje = conexion.recibirResultado();
                            publish(mensaje);

                            // Si recibimos mensaje de eliminación o victoria del torneo, terminar
                            if (mensaje.contains("eliminado del torneo") || 
                                mensaje.contains("Ganaste el torneo")) {
                                break;
                            }
                        } catch (IOException e) {
                            // Conexión cerrada por el servidor
                            break;
                        }
                    }
                    
                    conexion.cerrar();
                } catch (Exception e) {
                    publish("ERROR:" + e.getMessage());
                }
                return null;
            }

            @Override
            protected void process(List<String> mensajes) {
                for (String mensaje : mensajes) {
                    if (mensaje.startsWith("ERROR:")) {
                        cVista.notificarError("Error de conexión: " + mensaje.substring(6));
                    } else if (mensaje.contains("Ganaste") && mensaje.contains("Victorias totales")) {
                        // Mensaje de victoria en ronda
                        cVista.notificarResultado("✓ " + mensaje);
                    } else if (mensaje.contains("eliminado del torneo")) {
                        // Mensaje de derrota final
                        cVista.notificarResultadoFinal(mensaje, false);
                    } else if (mensaje.contains("Ganaste el torneo")) {
                        // Mensaje de victoria del torneo completo
                        cVista.notificarResultadoFinal(mensaje, true);
                    } else {
                        // Otros mensajes (combate en desarrollo, etc.)
                        cVista.notificarResultado(mensaje);
                    }
                }
            }
        };
        
        worker.execute();
    }
}