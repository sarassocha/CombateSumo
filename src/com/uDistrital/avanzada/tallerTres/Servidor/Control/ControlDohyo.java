package com.uDistrital.avanzada.tallerTres.Servidor.Control;

import com.uDistrital.avanzada.tallerTres.Servidor.Modelo.Dohyo;
import java.util.List;
import java.util.Random;

/**
 * Controlador del Dohyo. Gestiona la lógica del combate de sumo.
 * Trabaja exclusivamente con {@link ControlRikishi}, nunca accede a {@link com.uDistrital.avanzada.tallerTres.Servidor.Modelo.Rikishi} directamente.
 * Consulta el rival de cada atacante a través de {@link ControlGeneral}.
 */
public class ControlDohyo {

    /** Modelo del dohyo que almacena el estado del combate. */
    private final Dohyo dohyo;

    /** Generador de números aleatorios para técnicas y probabilidades. */
    private final Random random;

    /** Referencia al controlador general para notificar eventos y consultar rivales. */
    private final ControlGeneral controlGeneral;

    /** Probabilidad base de éxito de una técnica en el primer turno. */
    private static final double PROBABILIDAD_BASE = 0.05;

    /** Incremento de probabilidad de éxito por cada turno transcurrido. */
    private static final double INCREMENTO_POR_TURNO = 0.02;

    /**
     * Constructor que inicializa el dohyo y el generador aleatorio.
     *
     * @param controlGeneral Referencia al controlador general
     */
    public ControlDohyo(ControlGeneral controlGeneral) {
        this.dohyo = new Dohyo();
        this.random = new Random();
        this.controlGeneral = controlGeneral;
    }

    /**
     * Indica si el combate ha finalizado.
     *
     * @return true si el combate terminó
     */
    public boolean isCombateTerminado() {
        return dohyo.isCombateTerminado();
    }

    /**
     * Retorna el modelo del dohyo.
     *
     * @return Instancia de {@link Dohyo}
     */
    public Dohyo getDohyo() {
        return dohyo;
    }

    /**
     * Aplica una técnica del atacante sobre su rival de forma sincronizada.
     * Pide el rival a ControlGeneral en lugar de gestionarlo internamente.
     *
     * @param atacante Controlador del luchador que ataca
     */
    public synchronized void aplicarTecnica(ControlRikishi atacante) {
        if (dohyo.isCombateTerminado()) {
            return;
        }

        ControlRikishi rivalControl = controlGeneral.obtenerRival(atacante);
        if (rivalControl == null) {
            return;
        }

        String tecnicaUsada = seleccionarTecnicaAleatoria(atacante);
        dohyo.incrementarTurno();
        dohyo.setNombreTurnoActual(atacante.ObtenerNombre());

        double probabilidadExito = calcularProbabilidadExito();
        boolean exito = random.nextDouble() < probabilidadExito;

        String mensaje = String.format("Turno %d | %s aplica [%s] a %s",
                dohyo.getTurno(), atacante.ObtenerNombre(), tecnicaUsada, rivalControl.ObtenerNombre());
        controlGeneral.notificarMovimiento(mensaje);

        if (exito) {
            rivalControl.recibirGolpe();
            dohyo.setNombreGanador(atacante.ObtenerNombre());
            dohyo.setCombateTerminado(true);

            String mensajeVictoria = String.format("¡%s gana con %s en el turno %d!",
                    atacante.ObtenerNombre(), tecnicaUsada, dohyo.getTurno());
            controlGeneral.notificarFinCombate(mensajeVictoria, atacante);
        } else {
            rivalControl.recibirGolpe();
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
        double probabilidad = PROBABILIDAD_BASE + (dohyo.getTurno() * INCREMENTO_POR_TURNO);
        return Math.min(probabilidad, 0.8);
    }
}