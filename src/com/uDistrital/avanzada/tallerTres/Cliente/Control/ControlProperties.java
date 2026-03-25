/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.uDistrital.avanzada.tallerTres.Cliente.Control;

import com.uDistrital.avanzada.tallerTres.Cliente.Modelo.ArchivoPropiedades;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

/**
 *
 * @author Sara
 */ 
public class ControlProperties {
    private ControlGeneral cGeneral;
    private File origenActual;
    
    public static final String prefijoKimari = "kimari.";

    
        /**
     * recibe la inyeccion del control General
     *
     * @param cGeneral Control General
     */
    public ControlProperties(ControlGeneral cGeneral) {
        this.cGeneral = cGeneral;

    }

    /**
     * Delega la apertura del archivo de propiedades gracias al JFileChooser
     *
     * @param archivo Archivo
     * @throws IOException Error de lectura
     */
    public void cargarDesde(File archivo) throws IOException {
        if (archivo == null) {
            throw new IllegalStateException("No se recibió archivo"
                    + " para cargar.");
        }
        if (!archivo.exists() || !archivo.isFile()) {
            throw new IllegalStateException("El archivo no existe o no "
                    + "es válido: " + archivo);
        }

        ArchivoPropiedades ap = new ArchivoPropiedades(archivo);
        Properties props = ap.abrir();

        this.origenActual = archivo;
    }
    /**
     * Extrae los kimarites del archivo properties
     *
     * @return Lista de nombres de técnicas
     * @throws IOException si hay error de lectura
     */
    public ArrayList<String> extraerKimarites() throws IOException {
        if (this.origenActual == null) {
            throw new IllegalStateException("No hay archivo asociado. "
                    + "Use cargarDesde(File) primero.");
        }
        
        // Abrir archivo .properties
        Properties props = new ArchivoPropiedades(this.origenActual).abrir();
        
        ArrayList<String> kimarites = new ArrayList<>();
        
        // Recorrer todas las propiedades
        int i = 1;
        while (true) {
            String tecnica = props.getProperty(prefijoKimari + i);
            if (tecnica == null) {
                break;      
            }
            kimarites.add(tecnica.trim());
            i++;
        }
        
        return kimarites;
    }

    /**
     * Extrae el puerto del servidor desde el archivo properties.
     *
     * @return Puerto como entero
     * @throws IOException si hay error de lectura
     * @throws IllegalStateException si la clave no existe o el valor no es numérico
     */
    public int extraerPuerto() throws IOException {
        if (this.origenActual == null) {
            throw new IllegalStateException("No hay archivo asociado. "
                    + "Use cargarDesde(File) primero.");
        }
        Properties props = new ArchivoPropiedades(this.origenActual).abrir();
        String valor = props.getProperty("servidorP.puerto");
        if (valor == null) {
            throw new IllegalStateException("No se encontró la clave 'servidorP.puerto' en el archivo.");
        }
        try {
            return Integer.parseInt(valor.trim());
        } catch (NumberFormatException e) {
            throw new IllegalStateException("El valor de 'servidorP.puerto' no es un número válido: " + valor);
        }
    }

    /**
     * Extrae el hostname del servidor desde el archivo properties.
     *
     * @return Hostname como String
     * @throws IOException si hay error de lectura
     */
    public String extraerHostname() throws IOException {
        if (this.origenActual == null) {
            throw new IllegalStateException("No hay archivo asociado. "
                    + "Use cargarDesde(File) primero.");
        }
        Properties props = new ArchivoPropiedades(this.origenActual).abrir();
        String valor = props.getProperty("servidorH.hostname");
        if (valor == null) {
            throw new IllegalStateException("No se encontró la clave 'servidorH.hostname' en el archivo.");
        }
        return valor.trim();
    }
}