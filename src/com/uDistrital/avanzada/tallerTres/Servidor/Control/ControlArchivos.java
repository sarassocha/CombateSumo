package com.uDistrital.avanzada.tallerTres.Servidor.Control;

import com.uDistrital.avanzada.tallerTres.Servidor.Modelo.ArchivoPropiedades;
import com.uDistrital.avanzada.tallerTres.Servidor.Modelo.ArchivoAleatorio;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Controlador del archivo de propiedades del servidor.
 * Delega la lectura a {@link ArchivoPropiedades} y extrae los datos necesarios.
 *
 * @author Sara
 */
public class ControlArchivos {
    
    private static final int RECORD_SIZE = 200;
    private static final int RECORD_SIZE_NEWLINE = RECORD_SIZE + 1;
    
    private ControlGeneral cGeneral;
    private File origenActual;
    private ArchivoAleatorio conexionArchivo;

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
            throw new IllegalStateException("No se recibió archivo para cargar.");
        }
        if (!archivo.exists() || !archivo.isFile()) {
            throw new IllegalStateException("El archivo no existe o no es válido: " + archivo);
        }

        ArchivoPropiedades ap = new ArchivoPropiedades(archivo);
        Properties props = ap.abrir();

        this.origenActual = archivo;
        
        String rutaArchivoResultados = props.getProperty("archivo.resultados", "Data/resultados_combates.dat");
        inicializarArchivoAleatorio(rutaArchivoResultados);
    }
    
    /**
     * Extrae los datos de gif del archivo properties
     *
     * @return Lista de arrays con [nombreGif, ruta]
     * @throws IOException si hay error de lectura
     */
    public ArrayList<String[]> extraerGif() throws IOException {
        if (this.origenActual == null) {
            throw new IllegalStateException("No hay archivo asociado. Use cargarDesde(File) primero.");
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
    
    // MÉTODOS PARA ARCHIVO DE ACCESO ALEATORIO
    
    public void inicializarArchivoAleatorio(String rutaArchivo) {
        this.conexionArchivo = new ArchivoAleatorio(rutaArchivo);
    }
    
    public void guardarResultadosCombate(Map<String, String> resultadosCombate) {
        if (conexionArchivo == null) {
            throw new IllegalStateException("Archivo de acceso aleatorio no inicializado");
        }
        try (RandomAccessFile file = conexionArchivo.obtenerPorId("")) {
            int siguienteId = obtenerUltimoId(file) + 1;
            for (Map.Entry<String, String> entrada : resultadosCombate.entrySet()) {
                String registro = siguienteId + "," + entrada.getKey() + "," + entrada.getValue();
                conexionArchivo.insertar(ajustarTamano(registro, RECORD_SIZE));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public List<String> leerVictoriasPorRikishi(String nombreRikishi) {
        try (RandomAccessFile file = conexionArchivo.obtenerPorId("")) {
            if (file.length() == 0) return new ArrayList<>();
            return filtrarLineasVictoria(leerTodasLineas(file), nombreRikishi);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public List<String> leerTodosRegistros() {
        try (RandomAccessFile file = conexionArchivo.obtenerPorId("")) {
            if (file.length() == 0) return new ArrayList<>();
            String[] lineas = leerTodasLineas(file);
            List<String> registros = new ArrayList<>();
            for (String linea : lineas) {
                String limpia = linea.trim();
                if (!limpia.isEmpty()) {
                    registros.add(limpia);
                }
            }
            return registros;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    private String[] leerTodasLineas(RandomAccessFile file) throws IOException {
        byte[] bytes = new byte[(int) file.length()];
        file.readFully(bytes);
        return new String(bytes, java.nio.charset.StandardCharsets.UTF_8).split("\n");
    }
    
    private List<String> filtrarLineasVictoria(String[] lineas, String nombreRikishi) {
        List<String> resultado = new ArrayList<>();
        for (String linea : lineas) {
            String limpia = linea.trim();
            if (limpia.isEmpty()) continue;
            String[] partes = limpia.split(",");
            if (partes.length >= 3 
                && partes[1].trim().equals(nombreRikishi) 
                && partes[partes.length - 1].trim().equals("GANADOR")) {
                resultado.add(limpia);
            }
        }
        return resultado;
    }
    
    private int obtenerUltimoId(RandomAccessFile file) {
        String ultimo = leerUltimoRegistro(file);
        if (ultimo.isBlank()) return 0;
        try {
            return Integer.parseInt(ultimo.split(",")[0].trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    private String leerUltimoRegistro(RandomAccessFile file) {
        try {
            long longitud = file.length();
            if (longitud == 0) return "";
            long pos = Math.max(0, longitud - RECORD_SIZE_NEWLINE);
            file.seek(pos);
            byte[] buffer = new byte[RECORD_SIZE];
            int leidos = file.read(buffer);
            return leidos > 0 ? new String(buffer, 0, leidos, java.nio.charset.StandardCharsets.UTF_8).trim() : "";
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    private String ajustarTamano(String str, int tamano) {
        if (str == null) str = "";
        if (str.length() >= tamano) return str.substring(0, tamano);
        return String.format("%-" + tamano + "s", str);
    }
}
