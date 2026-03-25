package com.uDistrital.avanzada.tallerTres.Servidor.Control;

import com.uDistrital.avanzada.tallerTres.Servidor.Modelo.ConexionServidor;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    private int MAX_CLIENTES = 2;

    /** Contador de clientes conectados hasta el momento. */
    private int contadorClientes = 0;

    /** Lista de sockets de clientes conectados. */
    private List<Socket> clientes = new ArrayList<>();

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
     * Lanza un hilo que espera conexiones entrantes hasta alcanzar el máximo de clientes.
     * Por cada cliente aceptado, lanza un hilo para manejar su comunicación.
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
                    new Thread(() -> manejarCliente(cliente)).start();
                } catch (IOException e) {
                    controlGeneral.registrarLog("Error aceptando cliente: " + e.getMessage());
                }
            }
        }).start();
    }

    /**
     * Lee los datos del luchador enviados por el cliente y los delega al {@link ControlGeneral}.
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

            controlGeneral.conectarRikishi(nombre, peso, altura, victorias, tecnicas);

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
}