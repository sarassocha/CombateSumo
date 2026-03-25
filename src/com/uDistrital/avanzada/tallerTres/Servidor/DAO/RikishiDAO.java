package com.uDistrital.avanzada.tallerTres.Servidor.DAO;

import com.uDistrital.avanzada.tallerTres.Servidor.Modelo.Rikishi;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * DAO para gestionar operaciones CRUD de Rikishi en la base de datos.
 * Implementa IReader e IWriter.
 */
public class RikishiDAO implements IReader<Rikishi>, IWriter<Rikishi> {
    
    private ConexionDB conexionDB;
    
    public RikishiDAO() {
        this.conexionDB = ConexionDB.getInstancia();
    }
    
    @Override
    public List<Rikishi> obtenerTodos() {
        List<Rikishi> rikishis = new ArrayList<>();
        String sql = "SELECT nombre, peso, altura, victorias, kimarites FROM rikishi";
        
        try (Connection conn = conexionDB.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                String nombre = rs.getString("nombre");
                int peso = rs.getInt("peso");
                int altura = rs.getInt("altura");
                int victorias = rs.getInt("victorias");
                String kimaritesStr = rs.getString("kimarites");
                
                List<String> tecnicas = Arrays.asList(kimaritesStr.split(","));
                Rikishi rikishi = new Rikishi(nombre, peso, altura, victorias, tecnicas);
                rikishis.add(rikishi);
            }
            
        } catch (SQLException e) {
            System.err.println("Error obteniendo rikishis: " + e.getMessage());
        }
        
        return rikishis;
    }
    
    @Override
    public Rikishi obtenerPorId(String nombre) {
        String sql = "SELECT nombre, peso, altura, victorias, kimarites FROM rikishi WHERE nombre = ?";
        
        try (Connection conn = conexionDB.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nombre);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                int peso = rs.getInt("peso");
                int altura = rs.getInt("altura");
                int victorias = rs.getInt("victorias");
                String kimaritesStr = rs.getString("kimarites");
                
                List<String> tecnicas = Arrays.asList(kimaritesStr.split(","));
                return new Rikishi(nombre, peso, altura, victorias, tecnicas);
            }
            
        } catch (SQLException e) {
        }
        
        return null;
    }
    
    @Override
    public boolean insertar(Rikishi rikishi) {
        String sql = "INSERT INTO rikishi (nombre, peso, altura, victorias, kimarites) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = conexionDB.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, rikishi.getNombre());
            stmt.setInt(2, rikishi.getPeso());
            stmt.setInt(3, rikishi.getAltura());
            stmt.setInt(4, rikishi.getVictorias());
            stmt.setString(5, String.join(",", rikishi.getTecnicas()));
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error SQL insertando rikishi: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean actualizar(Rikishi rikishi) {
        String sql = "UPDATE rikishi SET peso = ?, altura = ?, victorias = ?, kimarites = ? WHERE nombre = ?";
        
        try (Connection conn = conexionDB.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, rikishi.getPeso());
            stmt.setInt(2, rikishi.getAltura());
            stmt.setInt(3, rikishi.getVictorias());
            stmt.setString(4, String.join(",", rikishi.getTecnicas()));
            stmt.setString(5, rikishi.getNombre());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error actualizando rikishi: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean eliminar(String nombre) {
        String sql = "DELETE FROM rikishi WHERE nombre = ?";
        
        try (Connection conn = conexionDB.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nombre);
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Error eliminando rikishi: " + e.getMessage());
            return false;
        }
    }
}
