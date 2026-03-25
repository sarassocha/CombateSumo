package com.uDistrital.avanzada.tallerTres.Servidor.Modelo;

/**
 * Modelo del dohyo (ring de sumo).
 * Almacena el estado del combate: turno actual, ganador y si terminó.
 * Solo contiene primitivos y Strings, sin referencias a otros modelos.
 */
public class Dohyo {

    /** Nombre del luchador que tiene el turno actual. */
    private String nombreTurnoActual;

    /** Nombre del luchador ganador del combate. */
    private String nombreGanador;

    /** Indica si el combate ha finalizado. Volatile para visibilidad entre hilos. */
    private volatile boolean combateTerminado;

    /** Contador de turnos transcurridos en el combate. */
    private int turno;

    /**
     * Constructor que inicializa el dohyo con el combate sin terminar y turno en cero.
     */
    public Dohyo() {
        this.combateTerminado = false;
        this.turno = 0;
    }

    /**
     * Retorna el nombre del luchador con el turno actual.
     *
     * @return Nombre del luchador en turno
     */
    public String getNombreTurnoActual() {
        return nombreTurnoActual;
    }

    /**
     * Retorna el nombre del ganador del combate.
     *
     * @return Nombre del ganador, o null si el combate no ha terminado
     */
    public String getNombreGanador() {
        return nombreGanador;
    }

    /**
     * Indica si el combate ha finalizado.
     *
     * @return true si el combate terminó
     */
    public boolean isCombateTerminado() {
        return combateTerminado;
    }

    /**
     * Retorna el número de turno actual.
     *
     * @return Número de turno
     */
    public int getTurno() {
        return turno;
    }

    /**
     * Establece el nombre del luchador con el turno actual.
     *
     * @param nombre Nombre del luchador
     */
    public void setNombreTurnoActual(String nombre) {
        this.nombreTurnoActual = nombre;
    }

    /**
     * Establece el nombre del ganador del combate.
     *
     * @param nombre Nombre del ganador
     */
    public void setNombreGanador(String nombre) {
        this.nombreGanador = nombre;
    }

    /**
     * Establece si el combate ha terminado.
     *
     * @param v true para marcar el combate como terminado
     */
    public void setCombateTerminado(boolean v) {
        this.combateTerminado = v;
    }

    /**
     * Incrementa el contador de turnos en uno.
     */
    public void incrementarTurno() {
        this.turno++;
    }
}