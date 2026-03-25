package com.uDistrital.avanzada.tallerTres.Servidor.Control;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.uDistrital.avanzada.tallerTres.Servidor.DAO.RikishiDAO;

/**
 * Coordinador principal del servidor.
 * Los clientes se conectan, se registran en BD automáticamente, y luego combaten.
 */
public class ControlGeneral {

    private ControlConexion controlConexion;
    private final ControlVista cVista;
    private List<String[]> listaGifDatos;
    private final ControlArchivos cProps;
    private final Dohyo dohyo;
    private final List<ControlRikishi> rikishisConectados;
    private final List<ControlRikishi> rikishisActivos;
    private ControlRikishi ganadorActual;
    private ControlRikishi combatiente1;
    private ControlRikishi combatiente2;
    private int rondaActual;
    private RikishiDAO rikishiDAO;

    public ControlGeneral() {
        this.cVista = new ControlVista(this);
        this.cProps = new ControlArchivos(this);
        this.listaGifDatos = new ArrayList<>();
        this.dohyo = new Dohyo(this);
        this.rikishisConectados = new ArrayList<>();
        this.rikishisActivos = new ArrayList<>();
        this.rondaActual = 0;
        this.rikishiDAO = new RikishiDAO();

        try {
            controlConexion = new ControlConexion(this);
        } catch (Exception e) {
            cVista.mostrarError("Error iniciando servidor: " + e.getMessage());
        }
        cVista.iniciarFlujoInicial();
    }

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

    public void registrarRikishiEnDB(String nombre, int peso, int altura, int victorias, List<String> tecnicas) {
        try {
            com.uDistrital.avanzada.tallerTres.Servidor.Modelo.Rikishi existente = rikishiDAO.obtenerPorId(nombre);
            if (existente == null) {
                com.uDistrital.avanzada.tallerTres.Servidor.Modelo.Rikishi nuevoRikishi = 
                    new com.uDistrital.avanzada.tallerTres.Servidor.Modelo.Rikishi(nombre, peso, altura, victorias, tecnicas);
                if (rikishiDAO.insertar(nuevoRikishi)) {
                    cVista.registrarLog("Rikishi registrado en BD: " + nombre);
                } else {
                    cVista.registrarLog("Error: No se pudo insertar rikishi en BD: " + nombre);
                }
            } else {
                cVista.registrarLog("Rikishi ya existe en BD: " + nombre);
            }
        } catch (Exception e) {
            cVista.registrarLog("Error al registrar rikishi en BD: " + e.getMessage());
        }
    }

    public synchronized ControlRikishi conectarRikishi(String nombre, int peso, int altura, int victorias, List<String> tecnicas) {
        if (rikishisConectados.size() >= 6) {
            cVista.mostrarMensaje("Ya hay 6 luchadores conectados.");
            return null;
        }

        ControlRikishi nuevoControl = new ControlRikishi(nombre, peso, altura, victorias, tecnicas, this);
        rikishisConectados.add(nuevoControl);
        rikishisActivos.add(nuevoControl);

        cVista.mostrarMensaje(String.format("Rikishi conectado: %s (Peso: %dkg, Altura: %dcm, Victorias: %d)",
                nombre, peso, altura, victorias));

        int conectados = rikishisConectados.size();
        cVista.registrarLog("Esperando " + (6 - conectados) + " rikishi(s) más...");
        
        if (conectados == 6) {
            cVista.registrarLog("Todos los rikishis conectados. Presiona 'Iniciar Torneo' para comenzar.");
        }

        return nuevoControl;
    }

    public void verificarIniciarCombate() {
        if (rikishisConectados.size() < 6) {
            cVista.mostrarMensaje("Faltan " + (6 - rikishisConectados.size()) + " rikishi(s) por conectarse.");
            return;
        }
        iniciarCombate();
    }

    private void iniciarCombate() {
        if (rikishisActivos.size() < 2) {
            if (rikishisActivos.size() == 1) {
                ControlRikishi campeon = rikishisActivos.get(0);
                cVista.registrarLog("\n========================================");
                cVista.registrarLog("¡¡¡ CAMPEÓN DEL TORNEO: " + campeon.ObtenerNombre() + " !!!");
                cVista.registrarLog("Rondas ganadas: " + rondaActual);
                cVista.registrarLog("========================================");
                controlConexion.enviarMensajeA(campeon, "¡Ganaste el torneo completo!");
            }
            return;
        }

        rondaActual++;
        java.util.Random random = new java.util.Random();

        if (ganadorActual == null) {
            int idx1 = random.nextInt(rikishisActivos.size());
            combatiente1 = rikishisActivos.get(idx1);
            
            int idx2;
            do {
                idx2 = random.nextInt(rikishisActivos.size());
            } while (idx2 == idx1);
            combatiente2 = rikishisActivos.get(idx2);
        } else {
            combatiente1 = ganadorActual;
            
            List<ControlRikishi> rivalesPosibles = new ArrayList<>(rikishisActivos);
            rivalesPosibles.remove(ganadorActual);
            
            if (rivalesPosibles.isEmpty()) {
                iniciarCombate();
                return;
            }
            
            int idx = random.nextInt(rivalesPosibles.size());
            combatiente2 = rivalesPosibles.get(idx);
        }

        mostrarCombatientes();
        cVista.registrarLog(String.format("\n========== RONDA %d ==========", rondaActual));
        cVista.registrarLog(String.format("Combate: %s vs %s", 
                combatiente1.ObtenerNombre(), combatiente2.ObtenerNombre()));
        
        dohyo.iniciarCombate(combatiente1, combatiente2);
        controlConexion.enviarMensajeA(combatiente1, "Ronda " + rondaActual + " - Combate en desarrollo");
        controlConexion.enviarMensajeA(combatiente2, "Ronda " + rondaActual + " - Combate en desarrollo");
    }

    public void notificarFinCombate(String mensaje, ControlRikishi ganador) {
        cVista.registrarLog(mensaje);
        
        ControlRikishi perdedor = (ganador == combatiente1) ? combatiente2 : combatiente1;
        
        ganador.incrementarVictorias();
        
        cVista.registrarLog(">>> GANADOR RONDA " + rondaActual + ": " + ganador.ObtenerNombre() + 
                " (Victorias: " + ganador.getVictoriasEnTorneo() + ")");
        
        ganadorActual = ganador;
        rikishisActivos.remove(perdedor);
        perdedor.detener();
        
        cVista.registrarLog("Luchadores restantes: " + rikishisActivos.size());
        
        controlConexion.enviarMensajeA(ganador, String.format("Ronda %d - ¡Ganaste! Victorias totales: %d", 
                rondaActual, ganador.getVictoriasEnTorneo()));
        controlConexion.enviarMensajeA(perdedor, String.format("Ronda %d - Perdiste. Fuiste eliminado del torneo. Victorias totales: %d", 
                rondaActual, perdedor.getVictoriasEnTorneo()));
        
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        controlConexion.cerrarConexion(perdedor);
        mostrarResultados(ganador);

        try {
            Thread.sleep(2500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        iniciarCombate();
    }

    private void mostrarCombatientes() {
        String rutaCombate = getGifRuta("combate");
        if (rutaCombate != null) {
            cVista.mostrarGifJugador1(rutaCombate);
            cVista.mostrarGifJugador2(rutaCombate);
        }

        cVista.mostrarNombreJugador1(combatiente1.ObtenerNombre());
        cVista.mostrarNombreJugador2(combatiente2.ObtenerNombre());
    }

    private void mostrarResultados(ControlRikishi ganador) {
        String rutaVictoria = getGifRuta("victoria");
        String rutaDerrota = getGifRuta("derrota");

        if (rutaVictoria != null && rutaDerrota != null) {
            if (ganador == combatiente1) {
                cVista.mostrarGifJugador1(rutaVictoria);
                cVista.mostrarGifJugador2(rutaDerrota);
            } else {
                cVista.mostrarGifJugador1(rutaDerrota);
                cVista.mostrarGifJugador2(rutaVictoria);
            }
        }
    }

    public void solicitarTurno(ControlRikishi atacante) {
        dohyo.aplicarTecnica(atacante);
    }

    public ControlRikishi obtenerRival(ControlRikishi atacante) {
        if (atacante == combatiente1) {
            return combatiente2;
        } else if (atacante == combatiente2) {
            return combatiente1;
        }
        return null;
    }

    public boolean isCombateTerminado() {
        return dohyo.isCombateTerminado();
    }

    public void notificarMovimiento(String mensaje) {
        cVista.registrarLog(mensaje);
    }

    public void registrarLog(String mensaje) {
        cVista.registrarLog(mensaje);
    }

    private String getGifRuta(String nombre) {
        for (String[] dato : listaGifDatos) {
            if (dato[0].equalsIgnoreCase(nombre)) {
                return dato[1];
            }
        }
        return null;
    }
}
