package com.uDistrital.avanzada.tallerTres.Servidor.Control;

import com.uDistrital.avanzada.tallerTres.Servidor.Modelo.Rikishi;
import java.util.List;
import java.util.Random;

/**
 * Controlador del luchador de sumo. Implementa {@link Runnable} para ejecutarse concurrentemente.
 * Crea y gestiona su propio modelo {@link Rikishi} internamente.
 * Se comunica con {@link ControlGeneral} para solicitar turnos y consultar el estado del combate.
 */
public class ControlRikishi implements Runnable {

    /** Modelo del luchador asociado a este controlador. */
    private final Rikishi rikishi;

    /** Referencia al controlador general para solicitar turnos y consultar estado. */
    private final ControlGeneral controlGeneral;

    /** Generador de números aleatorios para los tiempos de espera entre turnos. */
    private final Random random;

    /** Indica si el luchador está activo en un combate. */
    private volatile boolean enCombate;

    /** Indica si el hilo debe detenerse. */
    private volatile boolean detenido;

    /** Contador de victorias en el torneo actual. */
    private int victoriasEnTorneo;

    /**
     * Constructor que inicializa el controlador con los datos del luchador.
     *
     * @param nombre        Nombre del luchador
     * @param peso          Peso en kg
     * @param altura        Altura en cm
     * @param victorias     Número de victorias previas
     * @param tecnicas      Lista de técnicas que domina
     * @param controlGeneral Referencia al controlador general
     */
    public ControlRikishi(String nombre, int peso, int altura, int victorias, List<String> tecnicas, ControlGeneral controlGeneral) {
        this.rikishi = new Rikishi(nombre, peso, altura, victorias, tecnicas);
        this.controlGeneral = controlGeneral;
        this.random = new Random();
        this.enCombate = false;
        this.detenido = false;
        this.victoriasEnTorneo = 0;
    }

    /**
     * Retorna el nombre del luchador para que {@link ControlDohyo} pueda construir mensajes.
     *
     * @return Nombre del luchador
     */
    public String ObtenerNombre() {
        return rikishi.getNombre();
    }

    /**
     * Retorna las técnicas del luchador para que {@link ControlDohyo} seleccione una aleatoriamente.
     *
     * @return Lista de kimarites
     */
    public List<String> ObtenerTecnicas() {
        return rikishi.getTecnicas();
    }

    /**
     * Marca al luchador como eliminado en el turno actual.
     * Llamado por {@link ControlDohyo} cuando este luchador recibe un golpe.
     */
    public void recibirGolpe() {
        rikishi.setEliminado(true);
    }

    /**
     * Indica si el luchador fue eliminado en el turno actual.
     *
     * @return true si está eliminado
     */
    public boolean ObtenerEliminado() {
        return rikishi.isEliminado();
    }

    /**
     * Retorna un resumen completo del luchador para mostrar en la consola del servidor.
     *
     * @return Cadena con nombre, peso, altura, victorias y técnicas
     */
    public String ObtenerResumen() {
        return String.format("Nombre: %s | Peso: %dkg | Altura: %dcm | Victorias: %d | Técnicas: %s",
                rikishi.getNombre(), rikishi.getPeso(), rikishi.getAltura(),
                rikishi.getVictorias(), rikishi.getTecnicas());
    }

    /**
     * Activa al luchador para participar en un combate.
     */
    public void activarParaCombate() {
        this.enCombate = true;
        rikishi.setEliminado(false);
    }

    /**
     * Detiene el hilo del luchador (cuando es eliminado del torneo).
     */
    public void detener() {
        this.detenido = true;
        this.enCombate = false;
    }

    /**
     * Incrementa el contador de victorias en el torneo.
     */
    public void incrementarVictorias() {
        this.victoriasEnTorneo++;
    }

    /**
     * Retorna el número de victorias en el torneo actual.
     *
     * @return Número de victorias
     */
    public int getVictoriasEnTorneo() {
        return victoriasEnTorneo;
    }

    /**
     * Lógica de ejecución del hilo del luchador.
     * Espera a ser activado para un combate, luego solicita turnos mientras el combate no termine.
     * Si fue golpeado, espera un tiempo aleatorio antes de continuar.
     */
    @Override
    public void run() {
        while (!detenido) {
            if (!enCombate) {
                // Esperar a ser activado para un combate
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
                continue;
            }

            if (controlGeneral.isCombateTerminado()) {
                enCombate = false;
                continue;
            }

            if (rikishi.isEliminado()) {
                try {
                    Thread.sleep(random.nextInt(250));
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
                rikishi.setEliminado(false);
            } else {
                controlGeneral.solicitarTurno(this);
                try {
                    Thread.sleep(random.nextInt(500));
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }
}