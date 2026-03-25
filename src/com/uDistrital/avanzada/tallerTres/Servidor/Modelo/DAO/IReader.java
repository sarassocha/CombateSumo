package com.uDistrital.avanzada.tallerTres.Servidor.Modelo.DAO;

import java.util.List;

/**
 * Interfaz para operaciones de lectura en la base de datos.
 * 
 * @param <T> Tipo de entidad a leer
 */
public interface IReader<T> {
    
    /**
     * Obtiene todos los registros de la entidad.
     * 
     * @return Lista de entidades
     */
    List<T> obtenerTodos();
    
    /**
     * Obtiene una entidad por su identificador.
     * 
     * @param id Identificador de la entidad
     * @return La entidad encontrada o null
     */
    T obtenerPorId(String id);
}
