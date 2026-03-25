package com.uDistrital.avanzada.tallerTres.Servidor.Control;

import com.uDistrital.avanzada.tallerTres.Servidor.Modelo.ConexionServidor;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador de conexiones del servidor.
 * Espera clientes entrantes, parsea los datos recibidos y delega al {@link ControlGeneral}.
 * No manipula streams directamente; usa {@link ConexionServidor} como modelo de red.
 */
public class ControlConexion {

    /** Modelo de conexión que gestiona el ServerSocket y los streams. */
    private ConexionServidor conexion;

    /** Referencia al controlador general para delegar eventos de conexión. */
    private ControlGeneral controlGeneral;

    /** Número máximo de clientes permitidos simultáneamente. */
    private int MAX_CLIENTES = 6;

    /** Contador de clientes conectados hasta el momento. */
    private int contadorClientes = 0;

    /** Lista de sockets de clientes conectados. */
    private List<Socket> clientes = new ArrayList<>();

    /** Mapa de sockets por rikishi para envío individual. */
    private Map<ControlRikishi, Socket> socketsPorRikishi = new HashMap<>();

    /**
     * Constructor que inicializa el servidor de conexiones y comienza a esperar clientes.
     *
     * @param controlGeneral Referencia al controlador general
     */
    public ControlConexion(ControlGeneral controlGeneral) {
        this.controlGeneral = controlGeneral;
        this.conexion = new ConexionServidor();
        try {
            conexion.iniciarServidor(5000);
            controlGeneral.registrarLog("Servidor escuchando en puerto 5000");
            esperarClientes();
        } catch (IOException e) {
            controlGeneral.registrarLog("Error iniciando servidor: " + e.getMessage());
        }
    }

    /**
     * Lanza un hilo que espera conexiones. Por cada cliente aceptado crea un hilo
     * para manejarlo, liberando el loop para aceptar el siguiente.
     * Dos puntos de creación de hilos:
     * 1. El loop de aceptación
     * 2. El hilo de manejo por cliente (que ejecuta toda la lógica del rikishi)
     */
    public void esperarClientes() {
        new Thread(() -> {
            while (contadorClientes < MAX_CLIENTES) {
                try {
                    Socket cliente = conexion.aceptarCliente();
                    contadorClientes++;
                    controlGeneral.registrarLog("Rikishi conectado #" + contadorClientes);
                    synchronized (this) {
                        clientes.add(cliente);
                    }
                    // Crear hilo por cliente que ejecutará toda la lógica del rikishi
                    new Thread(() -> manejarCliente(cliente)).start();
                } catch (IOException e) {
                    controlGeneral.registrarLog("Error aceptando cliente: " + e.getMessage());
                }
            }
        }).start();
    }

    /**
     * Lee los datos del luchador enviados por el cliente, los registra en la BD,
     * y ejecuta la lógica del rikishi en este mismo hilo.
     * Este hilo se convierte en el hilo del rikishi después de leer los datos.
     *
     * @param cliente Socket del cliente conectado
     */
    public void manejarCliente(Socket cliente) {
        try {
            int cantidadDatos = conexion.leerInt(cliente);
            String[] datos = new String[cantidadDatos];
            for (int i = 0; i < cantidadDatos; i++) {
                datos[i] = conexion.leerTexto(cliente);
            }

            int cantidadTecnicas = conexion.leerInt(cliente);
            String[] tecnicasArr = new String[cantidadTecnicas];
            for (int i = 0; i < cantidadTecnicas; i++) {
                tecnicasArr[i] = conexion.leerTexto(cliente);
            }

            String nombre = datos[0];
            int peso = Integer.parseInt(datos[1]);
            int altura = Integer.parseInt(datos[2]);
            int victorias = Integer.parseInt(datos[3]);
            List<String> tecnicas = Arrays.asList(tecnicasArr);

            // Registrar en la base de datos
            controlGeneral.registrarRikishiEnDB(nombre, peso, altura, victorias, tecnicas);

            // Crear el ControlRikishi
            ControlRikishi rikishi = controlGeneral.conectarRikishi(nombre, peso, altura, victorias, tecnicas);
            if (rikishi != null) {
                synchronized (this) {
                    socketsPorRikishi.put(rikishi, cliente);
                }
                // Este hilo ahora ejecuta la lógica del rikishi
                // No se crea un nuevo hilo - el hilo actual se convierte en el hilo del rikishi
                rikishi.run();
            }

        } catch (IOException e) {
            controlGeneral.registrarLog("Error leyendo datos del cliente: " + e.getMessage());
        }
    }

    /**
     * Envía un mensaje de texto a todos los clientes conectados.
     *
     * @param mensaje Mensaje a enviar
     */
    public void enviarResultadoATodos(String mensaje) {
        for (Socket cliente : clientes) {
            try {
                conexion.enviarTexto(cliente, mensaje);
            } catch (IOException e) {
                controlGeneral.registrarLog("Error enviando resultado: " + e.getMessage());
            }
        }
    }

    /**
     * Envía un mensaje a un rikishi específico.
     * Maneja silenciosamente errores de conexión cerrada.
     *
     * @param rikishi Rikishi destinatario
     * @param mensaje Mensaje a enviar
     */
    public void enviarMensajeA(ControlRikishi rikishi, String mensaje) {
        Socket cliente = socketsPorRikishi.get(rikishi);
        if (cliente != null && !cliente.isClosed()) {
            try {
                conexion.enviarTexto(cliente, mensaje);
            } catch (IOException e) {
                // Ignorar errores de conexión cerrada (es esperado cuando se elimina un jugador)
                String errorMsg = e.getMessage();
                if (errorMsg != null && 
                    !errorMsg.contains("conexión establecida") && 
                    !errorMsg.contains("Connection reset") &&
                    !errorMsg.contains("Broken pipe") &&
                    !errorMsg.contains("Software caused connection abort")) {
                    controlGeneral.registrarLog("Error enviando mensaje a " + rikishi.ObtenerNombre() + ": " + errorMsg);
                }
            }
        }
    }

    /**
     * Cierra la conexión de un rikishi eliminado.
     *
     * @param rikishi Rikishi cuya conexión se cerrará
     */
    public void cerrarConexion(ControlRikishi rikishi) {
        Socket cliente = socketsPorRikishi.get(rikishi);
        if (cliente != null) {
            try {
                if (!cliente.isClosed()) {
                    cliente.close();
                }
                synchronized (this) {
                    clientes.remove(cliente);
                    socketsPorRikishi.remove(rikishi);
                }
            } catch (IOException e) {
                // Ignorar errores al cerrar
            }
        }
    }
}