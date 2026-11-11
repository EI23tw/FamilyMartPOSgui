package com.twcorpororation.familymart;

import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class FrmLogin extends JFrame {

    // Variables de la interfaz
    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JComboBox<String> cbxRol;
    private JButton btnIngresar;
    private JButton btnRegistrar;

    // Colores corporativos basados en tu logo
    private final Color VERDE_FM = new Color(0, 168, 89);
    private final Color CELESTE_FM = new Color(0, 174, 239);
    private final Color GRIS_TEXTO = new Color(100, 100, 100);

    public FrmLogin() {
        configurarVentana();
        inicializarComponentes();
    }

    private void configurarVentana() {
        setTitle("FamilyMart - Acceso al Sistema");
        setSize(950, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new GridLayout(1, 2));
    }

    private void inicializarComponentes() {
        // PANEL IZQUIERDO (Formulario de Login)
        JPanel pnlIzquierdo = new JPanel(new GridBagLayout());
        pnlIzquierdo.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 40, 8, 40);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;

        // 1. Logo
        try {
            ImageIcon iconLogo = new ImageIcon(getClass().getResource("/img/logo.png"));
            Image img = iconLogo.getImage().getScaledInstance(200, -1, Image.SCALE_SMOOTH);
            JLabel lblLogo = new JLabel(new ImageIcon(img));
            lblLogo.setHorizontalAlignment(SwingConstants.CENTER);
            gbc.gridy = 0; gbc.insets = new Insets(10, 40, 20, 40);
            pnlIzquierdo.add(lblLogo, gbc);
        } catch (Exception e) {
            JLabel lblFallback = new JLabel("FamilyMart", SwingConstants.CENTER);
            lblFallback.setFont(new Font("Segoe UI", Font.BOLD, 36));
            lblFallback.setForeground(CELESTE_FM);
            gbc.gridy = 0; gbc.insets = new Insets(10, 40, 20, 40);
            pnlIzquierdo.add(lblFallback, gbc);
        }

        gbc.insets = new Insets(5, 40, 5, 40); // Restaurar espaciado

        // 2. Título y Subtítulo
        JLabel lblTitulo = new JLabel("Iniciar Sesión", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitulo.setForeground(new Color(20, 50, 40));
        gbc.gridy = 1;
        pnlIzquierdo.add(lblTitulo, gbc);

        JLabel lblSubtitulo = new JLabel("¡Hola de nuevo! Por favor, ingresa tus datos", SwingConstants.CENTER);
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitulo.setForeground(GRIS_TEXTO);
        gbc.gridy = 2; gbc.insets = new Insets(0, 40, 25, 40);
        pnlIzquierdo.add(lblSubtitulo, gbc);

        gbc.insets = new Insets(5, 40, 5, 40);

        // 3. Selector de Rol
        JLabel lblRol = new JLabel("Perfil de Acceso:");
        lblRol.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblRol.setForeground(GRIS_TEXTO);
        gbc.gridy = 3;
        pnlIzquierdo.add(lblRol, gbc);

        cbxRol = new JComboBox<>(new String[]{"Administrador", "Cajero"});
        cbxRol.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbxRol.setBackground(Color.WHITE);
        cbxRol.setPreferredSize(new Dimension(300, 38));
        cbxRol.setBorder(new LineBorder(Color.LIGHT_GRAY, 1, true));
        gbc.gridy = 4; gbc.insets = new Insets(0, 40, 15, 40);
        pnlIzquierdo.add(cbxRol, gbc);

        gbc.insets = new Insets(5, 40, 0, 40);

        // 4. Campo Usuario
        JLabel lblUsuario = new JLabel("Usuario:");
        lblUsuario.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblUsuario.setForeground(GRIS_TEXTO);
        gbc.gridy = 5;
        pnlIzquierdo.add(lblUsuario, gbc);

        txtUsuario = new JTextField();
        txtUsuario.setPreferredSize(new Dimension(300, 38));
        txtUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtUsuario.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.LIGHT_GRAY, 1, true),
                new EmptyBorder(5, 10, 5, 10)));
        gbc.gridy = 6; gbc.insets = new Insets(0, 40, 15, 40);
        pnlIzquierdo.add(txtUsuario, gbc);

        gbc.insets = new Insets(5, 40, 0, 40);

        // 5. Campo Contraseña
        JLabel lblPassword = new JLabel("Contraseña:");
        lblPassword.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPassword.setForeground(GRIS_TEXTO);
        gbc.gridy = 7;
        pnlIzquierdo.add(lblPassword, gbc);

        txtPassword = new JPasswordField();
        txtPassword.setPreferredSize(new Dimension(300, 38));
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.LIGHT_GRAY, 1, true),
                new EmptyBorder(5, 10, 5, 10)));
        gbc.gridy = 8; gbc.insets = new Insets(0, 40, 25, 40);
        pnlIzquierdo.add(txtPassword, gbc);

        // 6. Botones (Con los colores de la maqueta)
        btnIngresar = new JButton("Iniciar Sesión");
        btnIngresar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnIngresar.setBackground(VERDE_FM);
        btnIngresar.setForeground(Color.WHITE);
        btnIngresar.setFocusPainted(false);
        btnIngresar.setBorderPainted(false);
        btnIngresar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnIngresar.setPreferredSize(new Dimension(300, 42));
        gbc.gridy = 9; gbc.insets = new Insets(5, 40, 10, 40);
        pnlIzquierdo.add(btnIngresar, gbc);

        btnRegistrar = new JButton("Registrarse");
        btnRegistrar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnRegistrar.setBackground(Color.WHITE);
        btnRegistrar.setForeground(CELESTE_FM);
        btnRegistrar.setFocusPainted(false);
        btnRegistrar.setBorder(new LineBorder(CELESTE_FM, 1, true));
        btnRegistrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRegistrar.setPreferredSize(new Dimension(300, 42));
        gbc.gridy = 10; gbc.insets = new Insets(0, 40, 30, 40);
        pnlIzquierdo.add(btnRegistrar, gbc);

        // 7. Footer de Copyright
        JLabel lblFooter = new JLabel("<html><center>Derechos Reservados © 2026 FamilyMart POS.<br>Términos de Servicio | Privacidad</center></html>", SwingConstants.CENTER);
        lblFooter.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblFooter.setForeground(Color.GRAY);
        gbc.gridy = 11; gbc.insets = new Insets(10, 40, 10, 40);
        pnlIzquierdo.add(lblFooter, gbc);

        // PANEL DERECHO (Ilustración)
        JPanel pnlDerecho = new JPanel(new BorderLayout());
        pnlDerecho.setBackground(new Color(25, 60, 90));

        try {
            ImageIcon iconFondo = new ImageIcon(getClass().getResource("/img/tienda.png"));
            Image imgFondo = iconFondo.getImage().getScaledInstance(475, 600, Image.SCALE_SMOOTH);
            JLabel lblImagen = new JLabel(new ImageIcon(imgFondo));
            pnlDerecho.add(lblImagen, BorderLayout.CENTER);
        } catch (Exception e) {
            // Fallback si no encuentra la imagen
            JLabel lblFallback = new JLabel("<html><center><h1 style='color:white;'>IMAGEN DEL<br>MINIMARKET</h1><p style='color:white;'>(Guarda tu imagen como tienda.png en resources/img/)</p></center></html>", SwingConstants.CENTER);
            pnlDerecho.add(lblFallback, BorderLayout.CENTER);
        }

        // ASIGNACIÓN DE EVENTOS 
        btnIngresar.addActionListener(e -> iniciarSesion());
        btnRegistrar.addActionListener(e -> registrarUsuario());

        cbxRol.addActionListener(e -> {
            txtUsuario.setText("");
            txtPassword.setText("");
        });

        // Orden de agregado
        add(pnlIzquierdo);
        add(pnlDerecho);
    }

    // LÓGICA DE BASE DE DATOS 
    private void iniciarSesion() {
        String usuario = txtUsuario.getText().trim();
        String password = new String(txtPassword.getPassword());

        String sql = "SELECT id_rol FROM Usuarios WHERE username = ? AND password = ? AND estado = 1";

        try (java.sql.Connection conn = com.twcorpororation.familymart.dao.Conexion.conectar(); 
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, usuario);
            pstmt.setString(2, password);

            java.sql.ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int idRol = rs.getInt("id_rol");
                // Validar que el rol seleccionado en el ComboBox coincida con la Base de Datos
                int idRolSeleccionado = cbxRol.getSelectedIndex() + 1; 

                if (idRol != idRolSeleccionado) {
                    JOptionPane.showMessageDialog(this, "El usuario pertenece a otro perfil de acceso.", "Error de Rol", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (idRol == 1) {
                    JOptionPane.showMessageDialog(this, "Bienvenido Administrador");
                    com.twcorpororation.familymart.vistas.FrmAdmin admin = new com.twcorpororation.familymart.vistas.FrmAdmin();
                    admin.setVisible(true);
                } else if (idRol == 2) {
                    JOptionPane.showMessageDialog(this, "Bienvenido Cajero");
                    com.twcorpororation.familymart.vistas.FrmCajero cajero = new com.twcorpororation.familymart.vistas.FrmCajero();
                    cajero.setVisible(true);
                }
                this.dispose();

            } else {
                JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrectos, o cuenta inactiva.", "Acceso Denegado", JOptionPane.WARNING_MESSAGE);
            }

        } catch (java.sql.SQLException e) {
            JOptionPane.showMessageDialog(this, "Error de base de datos: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void registrarUsuario() {
        String usuario = txtUsuario.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (usuario.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!usuario.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ]+$")) {
            JOptionPane.showMessageDialog(this, "El usuario solo puede contener letras (sin números, símbolos ni espacios).", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (usuario.length() < 4 || usuario.length() > 15) {
            JOptionPane.showMessageDialog(this, "La longitud del usuario debe ser entre 4 y 15 caracteres.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (validarContrasenaFuerte(password)) {
            String sql = "INSERT INTO Usuarios (username, password, id_rol, estado) VALUES (?, ?, ?, 1)";

            try (java.sql.Connection conn = com.twcorpororation.familymart.dao.Conexion.conectar(); 
                 java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {

                pstmt.setString(1, usuario);
                pstmt.setString(2, password);
                int idRol = cbxRol.getSelectedIndex() + 1;
                pstmt.setInt(3, idRol);

                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Usuario registrado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);

            } catch (java.sql.SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al guardar en la BD: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "La contraseña no cumple con los requisitos mínimos:\n"
                    + "- Mínimo 8 caracteres\n"
                    + "- Una letra mayúscula y una minúscula\n"
                    + "- Un número\n"
                    + "- Un símbolo especial (@$!%*?&)",
                    "Seguridad de Contraseña", JOptionPane.WARNING_MESSAGE);
        }
    }

    private boolean validarContrasenaFuerte(String password) {
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    public static void main(String args[]) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        java.awt.EventQueue.invokeLater(() -> new FrmLogin().setVisible(true));
    }
}