package com.uDistrital.avanzada.tallerTres.Servidor.Modelo.DAO;

/**
 * Interfaz para operaciones de escritura en la base de datos.
 * 
 * @param <T> Tipo de entidad a escribir
 */
public interface IWriter<T> {
    
    /**
     * Inserta una nueva entidad en la base de datos.
     * 
     * @param entidad Entidad a insertar
     * @return true si se insertó correctamente
     */
    boolean insertar(T entidad);
    
    /**
     * Actualiza una entidad existente en la base de datos.
     * 
     * @param entidad Entidad a actualizar
     * @return true si se actualizó correctamente
     */
    boolean actualizar(T entidad);
    
    /**
     * Elimina una entidad de la base de datos.
     * 
     * @param id Identificador de la entidad a eliminar
     * @return true si se eliminó correctamente
     */
    boolean eliminar(String id);
}
