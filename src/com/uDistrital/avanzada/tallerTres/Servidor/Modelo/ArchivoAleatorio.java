package com.uDistrital.avanzada.tallerTres.Servidor.Modelo;

import com.uDistrital.avanzada.tallerTres.Servidor.Modelo.DAO.IReader;
import com.uDistrital.avanzada.tallerTres.Servidor.Modelo.DAO.IWriter;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

/**
 * Clase para gestionar conexión a archivo de acceso aleatorio.
 * Implementa IWriter e IReader para mantener consistencia con el patrón DAO.
 */
public class ArchivoAleatorio implements IWriter<String>, IReader<RandomAccessFile> {
    
    private final String rutaArchivo;
    
    public ArchivoAleatorio(String rutaArchivo) {
        this.rutaArchivo = new File(rutaArchivo).getAbsolutePath();
    }
    
    @Override
    public boolean insertar(String datos) {
        try (RandomAccessFile file = new RandomAccessFile(rutaArchivo, "rw")) {
            long longitudArchivo = file.length();
            file.seek(longitudArchivo);
            file.writeBytes(datos);
            file.writeBytes("\n");
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    
    @Override
    public RandomAccessFile obtenerPorId(String id) {
        try {
            return new RandomAccessFile(rutaArchivo, "rw");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public List<RandomAccessFile> obtenerTodos() {
        return null;
    }
    
    @Override
    public boolean actualizar(String datos) {
        return insertar(datos);
    }
    
    @Override
    public boolean eliminar(String id) {
        return false;
    }
}
