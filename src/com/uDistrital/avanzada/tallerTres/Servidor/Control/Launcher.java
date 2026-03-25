/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.uDistrital.avanzada.tallerTres.Servidor.Control;

/**
 * Punto de entrada del servidor.
 * Únicamente instancia el {@link ControlGeneral} para arrancar la aplicación.
 *
 * @author Sara
 */
public class Launcher {

    /**
     * Método principal que lanza el servidor.
     *
     * @param arg Argumentos de línea de comandos (no utilizados)
     */
    public static void main(String[] arg) {
        new ControlGeneral();
    }
}