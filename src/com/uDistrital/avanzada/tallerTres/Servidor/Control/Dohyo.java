package com.uDistrital.avanzada.tallerTres.Servidor.Control;

import java.util.List;
import java.util.Random;

/**
 * Dohyo (ring de sumo). Controlador que gestiona la lógica sincronizada del combate.
 * Maneja el estado del combate y coordina los turnos entre los luchadores.
 * No crea hilos, pero sincroniza el acceso de los hilos de los rikishi.
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

    /** Combatientes actuales. */
    private ControlRikishi combatiente1;
    private ControlRikishi combatiente2;

    /** Generador de números aleatorios para técnicas y probabilidades. */
    private final Random random;

    /** Referencia al controlador general para notificar eventos y consultar rivales. */
    private final ControlGeneral controlGeneral;

    /** Probabilidad base de éxito de una técnica en el primer turno. */
    private static final double PROBABILIDAD_BASE = 0.05;

    /** Incremento de probabilidad de éxito por cada turno transcurrido. */
    private static final double INCREMENTO_POR_TURNO = 0.02;

    /**
     * Constructor que inicializa el dohyo con el combate sin terminar.
     *
     * @param controlGeneral Referencia al controlador general
     */
    public Dohyo(ControlGeneral controlGeneral) {
        this.combateTerminado = false;
        this.turno = 0;
        this.random = new Random();
        this.controlGeneral = controlGeneral;
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
     * Inicia un nuevo combate entre dos luchadores.
     * Resetea el estado del dohyo y arranca los hilos de los combatientes.
     *
     * @param c1 Primer combatiente
     * @param c2 Segundo combatiente
     */
    public synchronized void iniciarCombate(ControlRikishi c1, ControlRikishi c2) {
        this.combatiente1 = c1;
        this.combatiente2 = c2;
        this.combateTerminado = false;
        this.turno = 0;
        this.nombreTurnoActual = null;
        this.nombreGanador = null;
        
        // Los hilos ya están corriendo desde ControlConexion, solo los activamos
        c1.activarParaCombate();
        c2.activarParaCombate();
    }

    /**
     * Aplica una técnica del atacante sobre su rival de forma sincronizada.
     * Este método es invocado por los hilos de los rikishi y coordina el combate.
     * Solo permite combatir a los dos luchadores activos en el combate actual.
     *
     * @param atacante Controlador del luchador que ataca
     */
    public synchronized void aplicarTecnica(ControlRikishi atacante) {
        if (combateTerminado) {
            return;
        }

        // Verificar que el atacante esté en el combate actual
        if (atacante != combatiente1 && atacante != combatiente2) {
            return;
        }

        ControlRikishi rivalControl = controlGeneral.obtenerRival(atacante);
        if (rivalControl == null) {
            return;
        }

        String tecnicaUsada = seleccionarTecnicaAleatoria(atacante);
        turno++;
        nombreTurnoActual = atacante.ObtenerNombre();

        double probabilidadExito = calcularProbabilidadExito();
        boolean exito = random.nextDouble() < probabilidadExito;

        String mensaje = String.format("Turno %d | %s aplica [%s] a %s",
                turno, atacante.ObtenerNombre(), tecnicaUsada, rivalControl.ObtenerNombre());
        controlGeneral.notificarMovimiento(mensaje);

        if (exito) {
            rivalControl.recibirGolpe();
            nombreGanador = atacante.ObtenerNombre();
            combateTerminado = true;

            String mensajeVictoria = String.format("¡%s gana con %s en el turno %d!",
                    atacante.ObtenerNombre(), tecnicaUsada, turno);
            controlGeneral.notificarFinCombate(mensajeVictoria, atacante);
        } else {
            controlGeneral.notificarMovimiento("  -> " + rivalControl.ObtenerNombre() + " resiste. Sigue en el dohyo.");
        }
    }

    /**
     * Selecciona una técnica aleatoria del repertorio del luchador atacante.
     * Si no tiene técnicas, retorna "Oshidashi" como técnica por defecto.
     *
     * @param cr Controlador del luchador atacante
     * @return Nombre de la técnica seleccionada
     */
    private String seleccionarTecnicaAleatoria(ControlRikishi cr) {
        List<String> tecnicas = cr.ObtenerTecnicas();
        if (tecnicas == null || tecnicas.isEmpty()) {
            return "Oshidashi";
        }
        return tecnicas.get(random.nextInt(tecnicas.size()));
    }

    /**
     * Calcula la probabilidad de éxito de una técnica según el turno actual.
     * La probabilidad aumenta con cada turno hasta un máximo del 80%.
     *
     * @return Probabilidad de éxito entre 0.0 y 0.8
     */
    private double calcularProbabilidadExito() {
        double probabilidad = PROBABILIDAD_BASE + (turno * INCREMENTO_POR_TURNO);
        return Math.min(probabilidad, 0.8);
    }
}
