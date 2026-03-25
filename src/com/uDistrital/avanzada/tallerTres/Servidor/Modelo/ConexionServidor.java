package com.uDistrital.avanzada.tallerTres.Servidor.Modelo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * Modelo de conexión del servidor.
 * Gestiona el {@link ServerSocket} y los streams de entrada/salida de cada cliente.
 * Expone métodos de lectura y escritura sin filtrar los streams hacia el exterior.
 */
public class ConexionServidor {

    /** Socket del servidor que escucha conexiones entrantes. */
    private ServerSocket serverSocket;

    /** Mapa de streams de entrada por cliente. */
    private Map<Socket, DataInputStream> entradas = new HashMap<>();

    /** Mapa de streams de salida por cliente. */
    private Map<Socket, DataOutputStream> salidas = new HashMap<>();

    /**
     * Inicia el servidor en el puerto indicado.
     *
     * @param puerto Puerto en el que escuchará el servidor
     * @throws IOException si no se puede abrir el puerto
     */
    public void iniciarServidor(int puerto) throws IOException {
        serverSocket = new ServerSocket(puerto);
    }

    /**
     * Espera y acepta una conexión entrante de un cliente.
     * Registra sus streams de entrada y salida internamente.
     *
     * @return Socket del cliente conectado
     * @throws IOException si ocurre un error al aceptar la conexión
     */
    public Socket aceptarCliente() throws IOException {
        Socket cliente = serverSocket.accept();
        entradas.put(cliente, new DataInputStream(cliente.getInputStream()));
        salidas.put(cliente, new DataOutputStream(cliente.getOutputStream()));
        return cliente;
    }

    /**
     * Lee un entero enviado por el cliente.
     *
     * @param cliente Socket del cliente
     * @return Entero leído
     * @throws IOException si ocurre un error de lectura
     */
    public int leerInt(Socket cliente) throws IOException {
        return entradas.get(cliente).readInt();
    }

    /**
     * Lee una cadena de texto enviada por el cliente.
     *
     * @param cliente Socket del cliente
     * @return Texto leído
     * @throws IOException si ocurre un error de lectura
     */
    public String leerTexto(Socket cliente) throws IOException {
        return entradas.get(cliente).readUTF();
    }

    /**
     * Envía un mensaje de texto al cliente indicado.
     *
     * @param cliente Socket del cliente destinatario
     * @param mensaje Texto a enviar
     * @throws IOException si ocurre un error de escritura
     */
    public void enviarTexto(Socket cliente, String mensaje) throws IOException {
        DataOutputStream salida = salidas.get(cliente);
        salida.writeUTF(mensaje);
        salida.flush();
    }
}