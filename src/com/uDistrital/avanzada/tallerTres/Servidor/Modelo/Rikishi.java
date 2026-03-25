package com.uDistrital.avanzada.tallerTres.Servidor.Modelo;

import java.util.List;

/**
 * Modelo del luchador de sumo (Rikishi).
 * Solo contiene datos y getters/setters. Sin lógica de combate.
 */
public class Rikishi {

    /** Nombre del luchador. */
    private final String nombre;

    /** Peso del luchador en kilogramos. */
    private final int peso;

    /** Altura del luchador en centímetros. */
    private final int altura;

    /** Número de victorias previas del luchador. */
    private final int victorias;

    /** Lista de técnicas (kimarites) que domina el luchador. */
    private final List<String> tecnicas;

    /** Indica si el luchador fue eliminado en el turno actual. Volatile para visibilidad entre hilos. */
    private volatile boolean eliminado;

    /**
     * Constructor que inicializa todos los atributos del luchador.
     *
     * @param nombre    Nombre del luchador
     * @param peso      Peso en kg
     * @param altura    Altura en cm
     * @param victorias Número de victorias previas
     * @param tecnicas  Lista de técnicas que domina
     */
    public Rikishi(String nombre, int peso, int altura, int victorias, List<String> tecnicas) {
        this.nombre = nombre;
        this.peso = peso;
        this.altura = altura;
        this.victorias = victorias;
        this.tecnicas = tecnicas;
        this.eliminado = false;
    }

    /**
     * Retorna el nombre del luchador.
     *
     * @return Nombre del luchador
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Retorna el peso del luchador.
     *
     * @return Peso en kg
     */
    public int getPeso() {
        return peso;
    }

    /**
     * Retorna la altura del luchador.
     *
     * @return Altura en cm
     */
    public int getAltura() {
        return altura;
    }

    /**
     * Retorna el número de victorias previas del luchador.
     *
     * @return Número de victorias
     */
    public int getVictorias() {
        return victorias;
    }

    /**
     * Retorna la lista de técnicas que domina el luchador.
     *
     * @return Lista de kimarites
     */
    public List<String> getTecnicas() {
        return tecnicas;
    }

    /**
     * Establece si el luchador fue eliminado en el turno actual.
     *
     * @param eliminado true si fue eliminado
     */
    public synchronized void setEliminado(boolean eliminado) {
        this.eliminado = eliminado;
    }

    /**
     * Indica si el luchador fue eliminado en el turno actual.
     *
     * @return true si está eliminado
     */
    public synchronized boolean isEliminado() {
        return eliminado;
    }
}