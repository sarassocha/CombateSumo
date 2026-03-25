package com.uDistrital.avanzada.tallerTres.Servidor.Control;

import com.uDistrital.avanzada.tallerTres.Servidor.Modelo.ArchivoPropiedades;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

/**
 * Controlador del archivo de propiedades del servidor.
 * Delega la lectura a {@link ArchivoPropiedades} y extrae los datos necesarios.
 *
 * @author Sara
 */
public class ControlArchivos {
    private ControlGeneral cGeneral;
    private File origenActual;

    /**
     * recibe la inyeccion del control General
     *
     * @param cGeneral Control General
     */
    public ControlArchivos(ControlGeneral cGeneral) {
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
     * Extrae los datos de gif del archivo properties
     *
     * @return Lista de arrays con [nombreGif, ruta]
     * @throws IOException si hay error de lectura
     */
    public ArrayList<String[]> extraerGif() throws IOException {
        if (this.origenActual == null) {
            throw new IllegalStateException("No hay archivo asociado. "
                    + "Use cargarDesde(File) primero.");
        }
        
        Properties props = new ArchivoPropiedades(this.origenActual).abrir();
        
        ArrayList<String[]> gifDatos = new ArrayList<>();
        
        for (String key : props.stringPropertyNames()) {
            if (!key.startsWith("gif.") && !key.startsWith("jpg.")) {
                continue;
            }
            String nombreGif = key.contains(".") ? key.substring(key.indexOf('.') + 1).trim() : key;
            String ruta = props.getProperty(key, "").trim();
            
            if (nombreGif.isEmpty()) {
                continue;
            } else if (!nombreGif.isEmpty() && !ruta.isEmpty()) {
                gifDatos.add(new String[]{nombreGif, ruta});
            }
        }
        
        return gifDatos;
    }
}