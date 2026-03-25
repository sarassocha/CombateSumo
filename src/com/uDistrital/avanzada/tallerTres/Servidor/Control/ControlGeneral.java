package com.uDistrital.avanzada.tallerTres.Servidor.Control;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Coordinador principal del servidor.
 * Orquesta los demás controladores sin acceder directamente a ningún modelo.
 * Es el punto de entrada de todos los eventos del sistema: conexiones, combate y vista.
 */
public class ControlGeneral {

    /** Controlador de conexiones de red. */
    private ControlConexion controlConexion;

    /** Controlador de la vista principal del servidor. */
    private final ControlVista cVista;

    /** Lista de datos de gifs cargados desde el archivo properties. Cada entrada es [nombre, ruta]. */
    private List<String[]> listaGifDatos;

    /** Controlador del archivo de propiedades. */
    private final ControlProperties cProps;

    /** Controlador del dohyo (lógica del combate). */
    private final ControlDohyo controlDohyo;

    /** Lista de controladores de luchadores conectados. Máximo 2. */
    private final List<ControlRikishi> rikishisConectados;

    /** Mapa de rivalidades entre luchadores. Gestionado aquí para que ControlDohyo no conozca a ControlRikishi directamente. */
    private final Map<ControlRikishi, ControlRikishi> rivales = new HashMap<>();

    /**
     * Constructor que inicializa todos los controladores y arranca el servidor de conexiones.
     */
    public ControlGeneral() {
        this.cVista = new ControlVista(this);
        this.cProps = new ControlProperties(this);
        this.listaGifDatos = new ArrayList<>();
        this.controlDohyo = new ControlDohyo(this);
        this.rikishisConectados = new ArrayList<>();

        try {
            controlConexion = new ControlConexion(this);
        } catch (Exception e) {
            cVista.mostrarError("Error iniciando servidor: " + e.getMessage());
        }
    }

    /**
     * Carga el archivo de propiedades de animaciones y actualiza la vista con el fondo y gif de espera.
     *
     * @param archivo Archivo .properties de animaciones seleccionado por el usuario
     */
    public void cargarProperties(File archivo) {
        try {
            cProps.cargarDesde(archivo);
            this.listaGifDatos = cProps.extraerGif();

            if (listaGifDatos.isEmpty()) {
                cVista.notificarError("No se encontraron datos de gif en el archivo de propiedades.");
                return;
            }

            String rutaDohyo = getGifRuta("dohyo");
            if (rutaDohyo != null) {
                cVista.mostrarFondoDohyo(rutaDohyo);
            }

            String rutaEspera = getGifRuta("espera");
            if (rutaEspera != null) {
                cVista.mostrarGifJugador1(rutaEspera);
                cVista.mostrarGifJugador2(rutaEspera);
            }

            cVista.notificarCargaExitosa();

        } catch (IOException ex) {
            cVista.notificarError("Error al leer el archivo: " + ex.getMessage());
        }
    }

    /**
     * Llamado por {@link ControlConexion} cuando llegan los datos de un cliente.
     * Crea el {@link ControlRikishi} correspondiente y actualiza la vista.
     *
     * @param nombre    Nombre del luchador
     * @param peso      Peso en kg
     * @param altura    Altura en cm
     * @param victorias Número de victorias previas
     * @param tecnicas  Lista de técnicas seleccionadas
     */
    public synchronized void conectarRikishi(String nombre, int peso, int altura, int victorias, List<String> tecnicas) {
        if (rikishisConectados.size() >= 2) {
            cVista.mostrarMensaje("Ya hay 2 luchadores conectados.");
            return;
        }

        ControlRikishi nuevoControl = new ControlRikishi(nombre, peso, altura, victorias, tecnicas, this);
        rikishisConectados.add(nuevoControl);

        cVista.mostrarMensaje(String.format("Rikishi conectado: %s (Peso: %dkg, Altura: %dcm, Victorias: %d)",
                nombre, peso, altura, victorias));

        int conectados = rikishisConectados.size();
        String rutaEntrada = getGifRuta("entrada");

        if (conectados == 1) {
            cVista.mostrarNombreJugador1(nombre);
            if (rutaEntrada != null) cVista.mostrarGifJugador1(rutaEntrada);
            cVista.registrarLog("Esperando " + (2 - conectados) + " rikishi(s) más...");
        } else {
            cVista.mostrarNombreJugador2(nombre);
            if (rutaEntrada != null) cVista.mostrarGifJugador2(rutaEntrada);
            cVista.registrarLog("Ambos rikishis conectados. Presiona 'Iniciar Combate' para comenzar.");
        }
    }

    /**
     * Verifica si hay 2 luchadores conectados e inicia el combate.
     * Llamado desde el botón "Iniciar" de la vista.
     */
    public void verificarIniciarCombate() {
        if (rikishisConectados.size() < 2) {
            cVista.mostrarMensaje("Faltan " + (2 - rikishisConectados.size()) + " rikishi(s) por conectarse.");
            return;
        }
        iniciarCombate();
    }

    /**
     * Inicia el combate entre los dos luchadores conectados.
     * Arranca los hilos de cada {@link ControlRikishi} y espera su finalización.
     */
    public void iniciarCombate() {
        ControlRikishi cr1 = rikishisConectados.get(0);
        ControlRikishi cr2 = rikishisConectados.get(1);

        rivales.put(cr1, cr2);
        rivales.put(cr2, cr1);

        String rutaCombate = getGifRuta("combate");
        if (rutaCombate != null) {
            cVista.mostrarGifJugador1(rutaCombate);
            cVista.mostrarGifJugador2(rutaCombate);
        }

        cVista.registrarLog(String.format("¡Combate iniciado! %s vs %s", cr1.ObtenerNombre(), cr2.ObtenerNombre()));
        controlConexion.enviarResultadoATodos("Combate en desarrollo");

        cr1.start();
        cr2.start();

        new Thread(() -> {
            try {
                cr1.join();
                cr2.join();
                if (!controlDohyo.isCombateTerminado()) {
                    cVista.registrarLog("Combate terminado sin ganador claro.");
                }
                rikishisConectados.clear();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                cVista.mostrarError("Error esperando fin del combate: " + e.getMessage());
            }
        }).start();
    }

    /**
     * Llamado por {@link ControlRikishi} para solicitar su turno de ataque.
     *
     * @param atacante Controlador del luchador que solicita el turno
     */
    public void solicitarTurno(ControlRikishi atacante) {
        controlDohyo.aplicarTecnica(atacante);
    }

    /**
     * Retorna el rival de un luchador. Llamado por {@link ControlDohyo} para
     * evitar que conozca directamente a {@link ControlRikishi}.
     *
     * @param atacante Controlador del luchador atacante
     * @return Controlador del rival, o null si no existe
     */
    public ControlRikishi obtenerRival(ControlRikishi atacante) {
        return rivales.get(atacante);
    }

    /**
     * Consultado por {@link ControlRikishi} para saber si el combate ya terminó.
     *
     * @return true si el combate finalizó
     */
    public boolean isCombateTerminado() {
        return controlDohyo.isCombateTerminado();
    }

    /**
     * Llamado por {@link ControlDohyo} para notificar un movimiento del combate.
     *
     * @param mensaje Descripción del movimiento
     */
    public void notificarMovimiento(String mensaje) {
        cVista.registrarLog(mensaje);
    }

    /**
     * Llamado por {@link ControlDohyo} al terminar el combate.
     * Notifica el resultado a la vista y a todos los clientes conectados.
     * Actualiza los gifs de victoria y derrota según el ganador.
     *
     * @param mensaje Mensaje de resultado del combate
     * @param ganador Controlador del luchador ganador
     */
    public void notificarFinCombate(String mensaje, ControlRikishi ganador) {
        cVista.registrarLog(mensaje);
        cVista.registrarLog("GANADOR");
        cVista.registrarLog(ganador.ObtenerResumen());
        controlConexion.enviarResultadoATodos(mensaje);

        String rutaVictoria = getGifRuta("victoria");
        String rutaDerrota = getGifRuta("derrota");
        ControlRikishi cr1 = rikishisConectados.get(0);
        ControlRikishi cr2 = rikishisConectados.get(1);

        if (rutaVictoria != null && rutaDerrota != null) {
            if (ganador == cr1) {
                cVista.mostrarGifJugador1(rutaVictoria);
                cVista.mostrarGifJugador2(rutaDerrota);
            } else {
                cVista.mostrarGifJugador1(rutaDerrota);
                cVista.mostrarGifJugador2(rutaVictoria);
            }
        }
    }

    /**
     * Registra un mensaje en la consola de la vista.
     *
     * @param mensaje Mensaje a registrar
     */
    public void registrarLog(String mensaje) {
        cVista.registrarLog(mensaje);
    }

    /**
     * Busca la ruta de un gif por su nombre clave en la lista cargada del properties.
     *
     * @param nombre Nombre clave del gif (ej: "espera", "combate", "victoria")
     * @return Ruta del gif, o null si no se encontró
     */
    private String getGifRuta(String nombre) {
        for (String[] dato : listaGifDatos) {
            if (dato[0].equalsIgnoreCase(nombre)) {
                return dato[1];
            }
        }
        return null;
    }
}