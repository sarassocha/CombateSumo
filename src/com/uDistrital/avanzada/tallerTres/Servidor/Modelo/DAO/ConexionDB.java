package com.uDistrital.avanzada.tallerTres.Servidor.DAO;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Clase Singleton para gestionar la conexión a la base de datos.
 * Lee la configuración desde un archivo properties.
 */
public class ConexionDB {
    
    private static ConexionDB instancia;
    private Connection conexion;
    private String url;
    private String usuario;
    private String contrasena;
    
    /**
     * Constructor privado para implementar Singleton.
     * Carga la configuración desde el archivo properties.
     */
    private ConexionDB() {
        cargarConfiguracion();
    }
    
    /**
     * Obtiene la instancia única de ConexionDB (Singleton).
     * 
     * @return Instancia de ConexionDB
     */
    public static synchronized ConexionDB getInstancia() {
        if (instancia == null) {
            instancia = new ConexionDB();
        }
        return instancia;
    }
    
    /**
     * Carga la configuración de la base de datos desde el archivo properties.
     */
    private void cargarConfiguracion() {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream("Data/servidor.properties")) {
            props.load(fis);
            this.url = props.getProperty("db.url");
            this.usuario = props.getProperty("db.usuario");
            this.contrasena = props.getProperty("db.contrasena");
        } catch (IOException e) {
            this.url = "jdbc:mysql://localhost:3306/sumo_db";
            this.usuario = "root";
            this.contrasena = "";
        }
    }
    
    public Connection getConexion() throws SQLException {
        if (conexion == null || conexion.isClosed()) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                conexion = DriverManager.getConnection(url, usuario, contrasena);
            } catch (ClassNotFoundException e) {
                System.err.println("Driver MySQL no encontrado: " + e.getMessage());
                throw new SQLException("Driver MySQL no encontrado: " + e.getMessage());
            } catch (SQLException e) {
                System.err.println("Error conectando a BD: " + e.getMessage());
                throw e;
            }
        }
        return conexion;
    }
    
    public void cerrarConexion() {
        if (conexion != null) {
            try {
                conexion.close();
            } catch (SQLException e) {
            }
        }
    }
}
