package com.uDistrital.avanzada.tallerTres.Cliente.Vista;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.File;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Sara
 */
public class VentanaPrincipal extends JFrame {
    
    private JButton btnCargar;
    private JButton btnIniciar;
    private JButton btnSalir;
    private JFileChooser fileChooser;
    private JLabel lblMensajeEstado;
    private JList<String> listaTecnicas;
    private DefaultListModel<String> modeloLista;
    private JLabel textoNombre, textoPeso, textoAltura, textoVictorias, textoPuerto, textoHostname;
    private JTextField cajaNombre, cajaPeso, cajaAltura, cajaVictorias, cajaPuerto, cajaHostname;

    

        /**
     * Constructor encargado de inicializar la vista
     *
     * @param listener Listener para los botones
     */
    public VentanaPrincipal() {
        configurarVentana();
        configurarFileChooser();
        inicializarComponentes();
        setVisible(false);
    }

    /**
     * Metodo encargado de configurar la ventana
     */
    public void configurarVentana() {
        setTitle("Combate De Sumo");
        setSize(750, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
    }

    /**
     * Metodo encargado de inicializar todos los elementos de la interfaz
     *
     * @param listener Listener para eventos
     */
    public void inicializarComponentes() {
        // Panel superior con título
        JPanel panelTitulo = new JPanel(new BorderLayout());
        panelTitulo.setBackground(new Color(19, 17, 156));
        panelTitulo.setPreferredSize(new Dimension(70, 40));
        JLabel lblTitulo = new JLabel("Registro Luchador", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitulo.setForeground(Color.WHITE);
        panelTitulo.add(lblTitulo, BorderLayout.CENTER);
        add(panelTitulo, BorderLayout.NORTH);
        
        // Panel Izquierdo 
        JPanel panelIzquierdo = new JPanel(new BorderLayout());
        panelIzquierdo.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Técnicas Disponibles (Kimarites)"));

        // Lista de técnicas 
        modeloLista = new DefaultListModel<>();  
        listaTecnicas = new JList<>(modeloLista);
        listaTecnicas.setFont(new Font("Monospaced", Font.PLAIN, 14));
        listaTecnicas.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        // Agregar scroll a la lista
        JScrollPane scrollTecnicas = new JScrollPane(listaTecnicas);
        scrollTecnicas.setPreferredSize(new Dimension(280, 260));
        panelIzquierdo.add(scrollTecnicas, BorderLayout.CENTER);
        
        add(panelIzquierdo, BorderLayout.WEST);
        
        // Panel 
        JPanel panelDerecho = new JPanel(new BorderLayout());
        panelDerecho.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Datos del Luchador"));
        panelDerecho.setBackground(new Color(245, 245, 250));

        // Inicializar los componentes
        textoNombre = new JLabel("Nombre:");
        textoPeso = new JLabel("Peso (kg):");
        textoAltura = new JLabel("Altura (cm):");
        textoVictorias = new JLabel("Victorias:");
        textoPuerto = new JLabel("Puerto:");
        textoHostname = new JLabel("Hostname:");

        cajaNombre = new JTextField(15);
        cajaPeso = new JTextField(15);
        cajaAltura = new JTextField(15);
        cajaVictorias = new JTextField("0", 15);
        cajaPuerto = new JTextField(15);
        cajaHostname = new JTextField(15);

        // Panel para los campos usando GridLayout
        JPanel panelCampos = new JPanel(new GridLayout(6, 2, 5, 5));
        panelCampos.setBackground(new Color(245, 245, 250));

        panelCampos.add(textoNombre);
        panelCampos.add(cajaNombre);
        panelCampos.add(textoPeso);
        panelCampos.add(cajaPeso);
        panelCampos.add(textoAltura);
        panelCampos.add(cajaAltura);
        panelCampos.add(textoVictorias);
        panelCampos.add(cajaVictorias);
        panelCampos.add(textoPuerto);
        panelCampos.add(cajaPuerto);
        panelCampos.add(textoHostname);
        panelCampos.add(cajaHostname);

        panelDerecho.add(panelCampos, BorderLayout.CENTER);
    
        // Panel inferior con botones
        JSplitPane panelCentral = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        panelCentral.setLeftComponent(panelIzquierdo);
        panelCentral.setRightComponent(panelDerecho);
        panelCentral.setDividerLocation(280);
        panelCentral.setDividerSize(3);
        add(panelCentral, BorderLayout.CENTER);
        
        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 15));
        panelBotones.setBackground(new Color(220, 220, 220));
        panelBotones.setPreferredSize(new Dimension(900, 80));

        btnCargar = crearBoton("Cargar Properties", "CARGAR",
                new Color(12, 12, 51));
        btnIniciar = crearBoton("Conectarse", "CONECTAR",
                new Color(12, 12, 51));
        btnSalir = crearBoton("Salir", "SALIR",
                new Color(12, 12, 51));

        panelBotones.add(btnCargar);
        panelBotones.add(btnIniciar);
        panelBotones.add(btnSalir);

        add(panelBotones, BorderLayout.SOUTH);
        
        // Panel de mensajes con JLabel 
        JPanel panelMensajes = new JPanel(new BorderLayout());
        panelMensajes.setBackground(new Color(240, 240, 240));
        panelMensajes.setBorder(BorderFactory.createEtchedBorder());
        panelMensajes.setPreferredSize(new Dimension(750, 30));

        lblMensajeEstado = new JLabel("Listo para cargar archivo properties");
        lblMensajeEstado.setFont(new Font("Arial", Font.ITALIC, 12));
        lblMensajeEstado.setForeground(new Color(70, 70, 70));

        panelMensajes.add(lblMensajeEstado, BorderLayout.WEST);

        // Panel contenedor para la parte inferior (mensajes + botones)
        JPanel panelSur = new JPanel(new BorderLayout());
        panelSur.add(panelMensajes, BorderLayout.NORTH);
        panelSur.add(panelBotones, BorderLayout.SOUTH);

        // Agregar el panel sur a la ventana
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
    public void configurarFileChooser() {
        fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Archivos Properties (*.properties)", "properties");
        fileChooser.setFileFilter(filter);
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
    }


    /**
     * Devuelve el botón encargado de cargar los datos o archivos requeridos por
     * la aplicación.
     *
     * @return Objeto {@link JButton} correspondiente al botón "Cargar".
     */
    public JButton getBtnCargar() {
        return btnCargar;
    }

    /**
     * Devuelve el botón que permite iniciar el proceso principal de la
     * aplicación.
     *
     * @return Objeto {@link JButton} correspondiente al botón "Iniciar".
     */
    public JButton getBtnIniciar() {
        return btnIniciar;
    }


    /**
     * Devuelve el botón que permite cerrar la aplicación o salir de la ventana
     * principal.
     *
     * @return Objeto {@link JButton} correspondiente al botón "Salir".
     */
    public JButton getBtnSalir() {
        return btnSalir;
    }
    

    /**
     * Muestra un mensaje de error en el panel de estado
     * 
     * @param mensaje Mensaje de error a mostrar
     */
    public void mostrarError(String mensaje) {
        lblMensajeEstado.setText("Error: " + mensaje);
        lblMensajeEstado.setForeground(Color.RED);
        lblMensajeEstado.setFont(new Font("Arial", Font.BOLD, 12));
    }

    /**
     * Muestra un mensaje informativo en el panel de estado
     * 
     * @param mensaje Mensaje informativo a mostrar
     */
    public void mostrarMensaje(String mensaje) {
        lblMensajeEstado.setText(mensaje);
        lblMensajeEstado.setForeground(new Color(0, 100, 0)); // Verde oscuro
        lblMensajeEstado.setFont(new Font("Arial", Font.PLAIN, 12));
    }

    
    /**
     * Metodo encargado de abrir la ventana de JFileChoser con el fin de buscar
     * el archivo properties y cumplir con el requisito de abierto y cerrado
     *
     * @return Archivo seleccionado o null si se cancela
     
     */
    
    /**
     * Metodo encargado de abrir la ventana de JFileChoser con el fin de buscar
     * el archivo properties y cumplir con el requisito de abierto y cerrado
     * @return 
     */
    public File solicitarArchivoPropiedades() {
        int resultado = fileChooser.showOpenDialog(null);

        if (resultado == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        }

        return null;
    }
    /**
     * Carga las técnicas en la lista del panel izquierdo
     *
     * @param tecnicas Lista de técnicas a mostrar
     */
    public void cargarTecnicas(List<String> tecnicas) {
        modeloLista.clear();
        for (String tecnica : tecnicas) {
            modeloLista.addElement(tecnica);
        }
    }

    /**
     * Establece el valor del campo puerto.
     *
     * @param puerto Puerto a mostrar
     */
    public void setPuerto(String puerto) {
        cajaPuerto.setText(puerto);
    }

    /**
     * Establece el hostname en el campo correspondiente de la vista.
     *
     * @param hostname Hostname del servidor
     */
    public void setHostname(String hostname) {
        cajaHostname.setText(hostname);
    }

    /**
     * Retorna el nombre ingresado por el usuario.
     *
     * @return Nombre del luchador
     */
    public String getNombre() {
        return cajaNombre.getText().trim();
    }

    /**
     * Retorna el peso ingresado por el usuario.
     *
     * @return Peso como String
     */
    public String getPeso() {
        return cajaPeso.getText().trim();
    }

    /**
     * Retorna la altura ingresada por el usuario.
     *
     * @return Altura como String
     */
    public String getAltura() {
        return cajaAltura.getText().trim();
    }

    /**
     * Retorna las victorias ingresadas por el usuario.
     *
     * @return Victorias como String
     */
    public String getVictorias() {
        return cajaVictorias.getText().trim();
    }

    /**
     * Retorna el puerto ingresado por el usuario.
     *
     * @return Puerto como String
     */
    public String getPuerto() {
        return cajaPuerto.getText().trim();
    }

    /**
     * Valida que los campos obligatorios estén completos y sean numéricos donde corresponde.
     * Muestra el error en pantalla si hay problemas.
     *
     * @return true si todos los campos son válidos
     */
    public boolean validarCampos() {
        StringBuilder camposVacios = new StringBuilder("Completa los siguientes campos: ");
        boolean hayErrores = false;

        if (cajaNombre.getText().trim().isEmpty()) {
            camposVacios.append("Nombre, ");
            hayErrores = true;
        }
        if (cajaPeso.getText().trim().isEmpty()) {
            camposVacios.append("Peso, ");
            hayErrores = true;
        } else {
            try { Integer.parseInt(cajaPeso.getText().trim()); }
            catch (NumberFormatException e) { camposVacios.append("Peso (debe ser número), "); hayErrores = true; }
        }
        if (cajaAltura.getText().trim().isEmpty()) {
            camposVacios.append("Altura, ");
            hayErrores = true;
        } else {
            try { Integer.parseInt(cajaAltura.getText().trim()); }
            catch (NumberFormatException e) { camposVacios.append("Altura (debe ser número), "); hayErrores = true; }
        }
        if (cajaVictorias.getText().trim().isEmpty()) {
            camposVacios.append("Victorias, ");
            hayErrores = true;
        } else {
            try { Integer.parseInt(cajaVictorias.getText().trim()); }
            catch (NumberFormatException e) { camposVacios.append("Victorias (debe ser número), "); hayErrores = true; }
        }
        if (cajaPuerto.getText().trim().isEmpty()) {
            camposVacios.append("Puerto, ");
            hayErrores = true;
        }

        if (hayErrores) {
            mostrarError(camposVacios.toString().replaceAll(", $", "."));
            return false;
        }
        return true;
    }

    /**
     * Retorna las técnicas seleccionadas por el usuario en la lista.
     *
     * @return Lista de técnicas seleccionadas
     */
    public List<String> getTecnicasSeleccionadas() {
        return listaTecnicas.getSelectedValuesList();
    }

    /**
     * Expone la lista de técnicas para que el controlador registre listeners.
     *
     * @return JList de técnicas
     */
    public JList<String> getListaTecnicas() {
        return listaTecnicas;
    }
    
}