package com.uDistrital.avanzada.tallerTres.Cliente.Modelo;

import java.io.*;
import java.net.Socket;

/**
 * Maneja la conexión con el servidor y el envío/recepción de datos.
 */
public class ConexionCliente {

    private Socket socket;
    private DataOutputStream salida;
    private DataInputStream entrada;

    /**
     * Conecta al servidor.
     */
    public ConexionCliente(String host, int puerto) throws IOException {
        socket = new Socket(host, puerto);
        salida = new DataOutputStream(socket.getOutputStream());
        entrada = new DataInputStream(socket.getInputStream());
    }

    /**
     * Envía datos y técnicas al servidor.
     */
    public void enviarDatos(String[] datos, String[] tecnicas) throws IOException {

        salida.writeInt(datos.length);
        for (String d : datos) {
            salida.writeUTF(d);
        }

        salida.writeInt(tecnicas.length);
        for (String t : tecnicas) {
            salida.writeUTF(t);
        }
    }

    /**
     * Recibe el resultado del servidor.
     */
    public String recibirResultado() throws IOException {
        return entrada.readUTF();
    }

    /**
     * Cierra la conexión.
     */
    public void cerrar() throws IOException {
        socket.close();
    }
}