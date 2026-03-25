package com.uDistrital.avanzada.tallerTres.Servidor.Vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Vista principal del servidor.
 */
public class VentanaPrincipal extends JFrame {

    private JButton btnIniciar;
    private JButton btnSalir;
    private JFileChooser fileChooser;
    private JLabel lblMensajeEstado;
    private JTextArea consola;

    // Panel izquierdo con fondo dohyo
    private PanelDohyo panelDohyo;

    // Nombres de los jugadores
    private JLabel lblNombreJugador1;
    private JLabel lblNombreJugador2;
    private JLabel lblVS;

    // Tamaño fijo para cada gif
    private static final int GIF_ANCHO = 200;
    private static final int GIF_ALTO  = 220;

    // GIFs de cada jugador
    private JLabel lblGifJugador1;
    private JLabel lblGifJugador2;

    public VentanaPrincipal() {
        configurarVentana();
        configurarFileChooser();
        inicializarComponentes();
        setVisible(false);
    }
    public File iniciarConArchivo() {
        int resultado = fileChooser.showOpenDialog(null);

        if (resultado == JFileChooser.APPROVE_OPTION) {
            File archivo = fileChooser.getSelectedFile();

            setVisible(true);
            return archivo;
        }

        return null;
    }
    public void configurarVentana() {
        setTitle("Dohyo - Combate de Sumo");
        setSize(1100, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
    }

    public void configurarFileChooser() {
        fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Archivos Properties (*.properties)", "properties");
        fileChooser.setFileFilter(filter);
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
    }

    public void inicializarComponentes() {
        // Panel título
        JPanel panelTitulo = new JPanel(new BorderLayout());
        panelTitulo.setBackground(new Color(19, 17, 156));
        panelTitulo.setPreferredSize(new Dimension(900, 70));
        JLabel lblTitulo = new JLabel("Dohyo - Combate de Sumo", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitulo.setForeground(Color.WHITE);
        panelTitulo.add(lblTitulo, BorderLayout.CENTER);
        add(panelTitulo, BorderLayout.NORTH);

        // Panel izquierdo con fondo dohyo
        panelDohyo = new PanelDohyo();
        panelDohyo.setLayout(new BorderLayout());
        panelDohyo.setPreferredSize(new Dimension(550, 600));

        // Panel de nombres (NORTH del dohyo)
        JPanel panelNombres = new JPanel(new BorderLayout());
        panelNombres.setOpaque(false);
        panelNombres.setPreferredSize(new Dimension(550, 50));

        lblNombreJugador1 = new JLabel("", SwingConstants.CENTER);
        lblNombreJugador1.setFont(new Font("Arial", Font.BOLD, 20));
        lblNombreJugador1.setForeground(Color.BLACK);

        lblVS = new JLabel("VS", SwingConstants.CENTER);
        lblVS.setFont(new Font("Arial", Font.BOLD, 22));
        lblVS.setForeground(new Color(255, 80, 80));
        lblVS.setVisible(false);

        lblNombreJugador2 = new JLabel("", SwingConstants.CENTER);
        lblNombreJugador2.setFont(new Font("Arial", Font.BOLD, 20));
        lblNombreJugador2.setForeground(Color.BLACK);

        panelNombres.add(lblNombreJugador1, BorderLayout.WEST);
        panelNombres.add(lblVS, BorderLayout.CENTER);
        panelNombres.add(lblNombreJugador2, BorderLayout.EAST);
        panelDohyo.add(panelNombres, BorderLayout.NORTH);

        // Panel central con los dos gifs — GridLayout divide el espacio en mitades iguales
        JPanel panelGifs = new JPanel(new GridLayout(1, 2, 0, 0));
        panelGifs.setOpaque(false);

        lblGifJugador1 = new JLabel("", SwingConstants.CENTER);
        lblGifJugador1.setOpaque(false);
        lblGifJugador1.setPreferredSize(new Dimension(GIF_ANCHO, GIF_ALTO));

        lblGifJugador2 = new JLabel("", SwingConstants.CENTER);
        lblGifJugador2.setOpaque(false);
        lblGifJugador2.setPreferredSize(new Dimension(GIF_ANCHO, GIF_ALTO));

        panelGifs.add(lblGifJugador1);
        panelGifs.add(lblGifJugador2);
        panelDohyo.add(panelGifs, BorderLayout.CENTER);

        // Consola derecha
        consola = new JTextArea();
        consola.setEditable(false);
        consola.setFont(new Font("Monospaced", Font.PLAIN, 13));
        consola.setBackground(new Color(20, 20, 20));
        consola.setForeground(new Color(0, 220, 0));
        consola.setMargin(new java.awt.Insets(8, 8, 8, 8));
        JScrollPane scrollConsola = new JScrollPane(consola);
        scrollConsola.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Combate"));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelDohyo, scrollConsola);
        splitPane.setDividerLocation(550);
        splitPane.setDividerSize(4);
        splitPane.setResizeWeight(0.5);
        add(splitPane, BorderLayout.CENTER);

        // Botones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        panelBotones.setBackground(new Color(220, 220, 220));
        panelBotones.setPreferredSize(new Dimension(900, 80));

 
        btnIniciar = crearBoton("Iniciar", "INICIAR", new Color(12, 12, 51));
        btnSalir = crearBoton("Salir", "SALIR", new Color(12, 12, 51));

        panelBotones.add(btnIniciar);
        panelBotones.add(btnSalir);

        // Panel estado + botones
        JPanel panelMensajes = new JPanel(new BorderLayout());
        panelMensajes.setBackground(new Color(240, 240, 240));
        panelMensajes.setBorder(BorderFactory.createEtchedBorder());
        panelMensajes.setPreferredSize(new Dimension(1100, 30));

        lblMensajeEstado = new JLabel();
        lblMensajeEstado.setFont(new Font("Arial", Font.ITALIC, 12));
        lblMensajeEstado.setForeground(new Color(70, 70, 70));
        panelMensajes.add(lblMensajeEstado, BorderLayout.WEST);

        JPanel panelSur = new JPanel(new BorderLayout());
        panelSur.add(panelMensajes, BorderLayout.NORTH);
        panelSur.add(panelBotones, BorderLayout.SOUTH);
        add(panelSur, BorderLayout.SOUTH);
    }

    public JButton crearBoton(String texto, String comando, Color color) {
        JButton boton = new JButton(texto);
        boton.setActionCommand(comando);
        boton.setFont(new Font("Arial", Font.BOLD, 15));
        boton.setBackground(color);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setPreferredSize(new Dimension(160, 45));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setBorder(BorderFactory.createRaisedBevelBorder());
        return boton;
    }

    // ── Métodos públicos para el controlador ──────────────────────────────────

    /**
     * Establece la imagen de fondo del dohyo.
     */
    public void setFondoDohyo(String ruta) {
        SwingUtilities.invokeLater(() -> {
            try {
                BufferedImage img = ImageIO.read(new File(ruta));
                panelDohyo.setFondo(img);
            } catch (IOException e) {
                // Si no carga la imagen, el panel queda con fondo negro
            }
        });
    }

    /**
     * Muestra el gif del jugador 1 (izquierda), escalado al tamaño fijo.
     */
    public void setGifJugador1(String ruta) {
        SwingUtilities.invokeLater(() -> {
            lblGifJugador1.setIcon(escalarGif(ruta));
        });
    }

    /**
     * Muestra el gif del jugador 2 (derecha), escalado al tamaño fijo.
     */
    public void setGifJugador2(String ruta) {
        SwingUtilities.invokeLater(() -> {
            lblGifJugador2.setIcon(escalarGif(ruta));
        });
    }

    /** Escala un gif/imagen a GIF_ANCHO x GIF_ALTO manteniendo la animación. */
    public ImageIcon escalarGif(String ruta) {
        ImageIcon original = new ImageIcon(ruta);
        Image escalada = original.getImage().getScaledInstance(GIF_ANCHO, GIF_ALTO, Image.SCALE_DEFAULT);
        return new ImageIcon(escalada);
    }

    /**
     * Actualiza el nombre del jugador 1 en la parte superior.
     */
    public void setNombreJugador1(String nombre) {
        SwingUtilities.invokeLater(() -> {
            lblNombreJugador1.setText(nombre);
        });
    }

    /**
     * Actualiza el nombre del jugador 2 en la parte superior.
     */
    public void setNombreJugador2(String nombre) {
        SwingUtilities.invokeLater(() -> {
            lblNombreJugador2.setText(nombre);
            lblVS.setVisible(true);
        });
    }

    public File solicitarArchivoPropiedades() {
    int resultado = fileChooser.showOpenDialog(this);

    if (resultado == JFileChooser.APPROVE_OPTION) {
        return fileChooser.getSelectedFile();
    }

    return null;
}

    public JButton getBtnIniciar() { return btnIniciar; }
    public JButton getBtnSalir() { return btnSalir; }

    public void agregarLog(String mensaje) {
        SwingUtilities.invokeLater(() -> {
            consola.append(mensaje + "\n");
            consola.setCaretPosition(consola.getDocument().getLength());
        });
    }

    public void mostrarMensaje(String mensaje) {
        SwingUtilities.invokeLater(() -> {
            lblMensajeEstado.setText(mensaje);
            lblMensajeEstado.setForeground(new Color(0, 100, 0));
        });
    }

    public void mostrarError(String mensaje) {
        SwingUtilities.invokeLater(() -> {
            lblMensajeEstado.setText("Error: " + mensaje);
            lblMensajeEstado.setForeground(Color.RED);
        });
    }

    // ── Clase interna: panel con imagen de fondo ──────────────────────────────

    /**
     * Panel que pinta una imagen de fondo escalada, manteniendo los hijos encima.
     */
    private static class PanelDohyo extends JPanel {

        private BufferedImage fondo;

        public void setFondo(BufferedImage fondo) {
            this.fondo = fondo;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (fondo != null) {
                g.drawImage(fondo, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }
}