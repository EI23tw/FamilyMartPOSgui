package com.twcorpororation.familymart.vistas;

import com.twcorpororation.familymart.dao.Conexion;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FrmAdmin extends JFrame {

    // Colores
    private final Color VERDE_FM = new Color(0, 168, 89);
    private final Color CELESTE_FM = new Color(0, 174, 239);
    private final Color FONDO = Color.WHITE;

    // Componentes UI - Productos
    private JTextField txtCodigo, txtNombre, txtPrecio, txtStock, txtBuscar;
    private JTable tblProductos;
    private int idProductoSeleccionado = -1;

    // Componentes UI - Usuarios
    private JTextField txtNomUsuario;
    private JPasswordField txtClave;
    private JComboBox<String> cbxRol;
    private JTable tblUsuarios;
    private int idUsuarioSeleccionado = -1;

    // Componentes UI - Dashboard
    private JTable tblVentas, tblEliminados;
    private JLabel lblIngresosTotales, lblTotalTransacciones, lblAlertasStock, lblTicketMaximo, lblTicketMinimo, lblTicketPromedio;
    private JPanel pnlGraficoStock;

    public FrmAdmin() {
        configurarVentana();
        inicializarTabs();
        
        // Cargar datos iniciales
        cargarProductos();
        cargarUsuarios();
        cargarHistorialVentas();
        cargarProductosEliminados();
        cargarIndicadores();
        cargarGraficoStock();
    }

    private void configurarVentana() {
        setTitle("FamilyMart - Panel de Administración");
        setSize(1100, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(FONDO);
        setLayout(new BorderLayout());

        // Cabecera superior
        JPanel pnlCabecera = new JPanel(new BorderLayout());
        pnlCabecera.setBackground(VERDE_FM);
        pnlCabecera.setPreferredSize(new Dimension(1100, 60));
        pnlCabecera.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel lblTitulo = new JLabel("PANEL DE ADMINISTRADOR");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(Color.WHITE);
        pnlCabecera.add(lblTitulo, BorderLayout.WEST);

        JButton btnCerrarSesion = new JButton("Cerrar Sesión");
        btnCerrarSesion.setBackground(Color.WHITE);
        btnCerrarSesion.setForeground(VERDE_FM);
        btnCerrarSesion.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCerrarSesion.setFocusPainted(false);
        btnCerrarSesion.addActionListener(e -> {
            new com.twcorpororation.familymart.FrmLogin().setVisible(true);
            this.dispose();
        });
        pnlCabecera.add(btnCerrarSesion, BorderLayout.EAST);

        add(pnlCabecera, BorderLayout.NORTH);
    }

    private void inicializarTabs() {
        UIManager.put("TabbedPane.selected", CELESTE_FM);
        UIManager.put("TabbedPane.contentBorderInsets", new Insets(0, 0, 0, 0));
        
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabbedPane.setBackground(Color.LIGHT_GRAY);
        tabbedPane.setForeground(Color.BLACK);

        tabbedPane.addTab("Catálogo de Productos", crearPanelProductos());
        tabbedPane.addTab("Dashboard (Reportes)", crearPanelDashboard());
        tabbedPane.addTab("Gestión de Usuarios", crearPanelUsuarios());

        add(tabbedPane, BorderLayout.CENTER);
    }

    // 1. PESTAÑA: PRODUCTOS
    private JPanel crearPanelProductos() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(FONDO);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Formulario Superior
        JPanel pnlForm = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        pnlForm.setBackground(FONDO);
        pnlForm.setBorder(BorderFactory.createTitledBorder(new LineBorder(Color.GRAY), "Datos del Producto", TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14)));

        pnlForm.add(new JLabel("Código:"));
        txtCodigo = new JTextField(10);
        pnlForm.add(txtCodigo);

        pnlForm.add(new JLabel("Nombre:"));
        txtNombre = new JTextField(20);
        pnlForm.add(txtNombre);

        pnlForm.add(new JLabel("Precio:"));
        txtPrecio = new JTextField(8);
        pnlForm.add(txtPrecio);

        pnlForm.add(new JLabel("Stock:"));
        txtStock = new JTextField(8);
        pnlForm.add(txtStock);

        panel.add(pnlForm, BorderLayout.NORTH);

        // Tabla
        tblProductos = new JTable();
        tblProductos.setRowHeight(25);
        tblProductos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int fila = tblProductos.getSelectedRow();
                if (fila >= 0) {
                    idProductoSeleccionado = Integer.parseInt(tblProductos.getValueAt(fila, 0).toString());
                    txtCodigo.setText(tblProductos.getValueAt(fila, 1).toString());
                    txtNombre.setText(tblProductos.getValueAt(fila, 2).toString());
                    txtPrecio.setText(tblProductos.getValueAt(fila, 3).toString());
                    txtStock.setText(tblProductos.getValueAt(fila, 4).toString());
                    
                    // REQUISITO: Bloquear cursor en el Código de Barras al modificar
                    txtCodigo.setEditable(false); 
                }
            }
        });
        JScrollPane scroll = new JScrollPane(tblProductos);
        panel.add(scroll, BorderLayout.CENTER);

        // Botones Inferiores y Buscador
        JPanel pnlBotones = new JPanel(new BorderLayout());
        pnlBotones.setBackground(FONDO);
        
        JPanel pnlBuscador = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlBuscador.setBackground(FONDO);
        pnlBuscador.add(new JLabel("Buscar (Nombre/Código):"));
        txtBuscar = new JTextField(20);
        txtBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) { buscarProductos(txtBuscar.getText().trim()); }
        });
        pnlBuscador.add(txtBuscar);
        pnlBotones.add(pnlBuscador, BorderLayout.WEST);

        JPanel pnlAcciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlAcciones.setBackground(FONDO);
        
        JButton btnGuardar = crearBotonApp("Guardar", VERDE_FM);
        btnGuardar.addActionListener(e -> guardarProducto());
        
        JButton btnModificar = crearBotonApp("Modificar", CELESTE_FM);
        btnModificar.addActionListener(e -> modificarProducto());
        
        JButton btnEliminar = crearBotonApp("Eliminar", new Color(220, 53, 69));
        btnEliminar.addActionListener(e -> eliminarProducto());
        
        JButton btnLimpiar = crearBotonApp("Limpiar", Color.GRAY);
        btnLimpiar.addActionListener(e -> limpiarCamposProductos());

        pnlAcciones.add(btnGuardar);
        pnlAcciones.add(btnModificar);
        pnlAcciones.add(btnEliminar);
        pnlAcciones.add(btnLimpiar);
        
        pnlBotones.add(pnlAcciones, BorderLayout.EAST);
        panel.add(pnlBotones, BorderLayout.SOUTH);

        return panel;
    }

    // 2. PESTAÑA: USUARIOS
    private JPanel crearPanelUsuarios() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(FONDO);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel pnlForm = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        pnlForm.setBackground(FONDO);
        pnlForm.setBorder(BorderFactory.createTitledBorder(new LineBorder(Color.GRAY), "Gestión de Personal", TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14)));

        pnlForm.add(new JLabel("Usuario:"));
        txtNomUsuario = new JTextField(15);
        pnlForm.add(txtNomUsuario);

        pnlForm.add(new JLabel("Contraseña:"));
        txtClave = new JPasswordField(15);
        pnlForm.add(txtClave);

        pnlForm.add(new JLabel("Rol:"));
        cbxRol = new JComboBox<>(new String[]{"Administrador", "Cajero"});
        pnlForm.add(cbxRol);

        panel.add(pnlForm, BorderLayout.NORTH);

        tblUsuarios = new JTable();
        tblUsuarios.setRowHeight(25);
        tblUsuarios.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int fila = tblUsuarios.getSelectedRow();
                if (fila >= 0) {
                    idUsuarioSeleccionado = Integer.parseInt(tblUsuarios.getValueAt(fila, 0).toString());
                    txtNomUsuario.setText(tblUsuarios.getValueAt(fila, 1).toString());
                    txtClave.setText(""); 
                    cbxRol.setSelectedItem(tblUsuarios.getValueAt(fila, 2).toString());
                }
            }
        });
        panel.add(new JScrollPane(tblUsuarios), BorderLayout.CENTER);

        JPanel pnlBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        pnlBotones.setBackground(FONDO);
        
        JButton btnGuardarU = crearBotonApp("Guardar Usuario", VERDE_FM);
        btnGuardarU.addActionListener(e -> guardarUsuario());
        
        JButton btnModificarU = crearBotonApp("Modificar Usuario", CELESTE_FM);
        btnModificarU.addActionListener(e -> modificarUsuario());
        
        JButton btnEliminarU = crearBotonApp("Eliminar Usuario", new Color(220, 53, 69));
        btnEliminarU.addActionListener(e -> eliminarUsuario());
        
        JButton btnLimpiarU = crearBotonApp("Limpiar", Color.GRAY);
        btnLimpiarU.addActionListener(e -> limpiarCamposUsuarios());

        pnlBotones.add(btnGuardarU); pnlBotones.add(btnModificarU); 
        pnlBotones.add(btnEliminarU); pnlBotones.add(btnLimpiarU);
        
        panel.add(pnlBotones, BorderLayout.SOUTH);
        return panel;
    }

    // 3. PESTAÑA: DASHBOARD
    private JPanel crearPanelDashboard() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(FONDO);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Panel de KPIs
        JPanel pnlKPIs = new JPanel(new GridLayout(2, 3, 10, 10));
        pnlKPIs.setBackground(FONDO);
        pnlKPIs.setPreferredSize(new Dimension(1100, 150));
        
        lblIngresosTotales = crearLabelKPI("Ingresos Totales", "S/ 0.00");
        lblTotalTransacciones = crearLabelKPI("Transacciones", "0");
        lblAlertasStock = crearLabelKPI("Agotados", "0");
        lblTicketMaximo = crearLabelKPI("Ticket Máximo", "S/ 0.00");
        lblTicketMinimo = crearLabelKPI("Ticket Mínimo", "S/ 0.00");
        lblTicketPromedio = crearLabelKPI("Ticket Promedio", "S/ 0.00");

        pnlKPIs.add(lblIngresosTotales.getParent()); pnlKPIs.add(lblTotalTransacciones.getParent()); pnlKPIs.add(lblAlertasStock.getParent());
        pnlKPIs.add(lblTicketMaximo.getParent()); pnlKPIs.add(lblTicketMinimo.getParent()); pnlKPIs.add(lblTicketPromedio.getParent());
        
        panel.add(pnlKPIs, BorderLayout.NORTH);

        // Centro: Tablas y Gráfico
        JPanel pnlCentro = new JPanel(new GridLayout(1, 2, 10, 10));
        pnlCentro.setBackground(FONDO);

        // Subpanel Izquierdo (Tablas)
        JPanel pnlTablas = new JPanel(new GridLayout(2, 1, 0, 10));
        pnlTablas.setBackground(FONDO);
        
        tblVentas = new JTable();
        JPanel pnlVentas = new JPanel(new BorderLayout());
        pnlVentas.setBackground(FONDO);
        pnlVentas.add(new JLabel("Historial de Ventas"), BorderLayout.NORTH);
        pnlVentas.add(new JScrollPane(tblVentas), BorderLayout.CENTER);
        
        tblEliminados = new JTable();
        JPanel pnlBajas = new JPanel(new BorderLayout());
        pnlBajas.setBackground(FONDO);
        pnlBajas.add(new JLabel("Productos Eliminados (Bajas)"), BorderLayout.NORTH);
        pnlBajas.add(new JScrollPane(tblEliminados), BorderLayout.CENTER);
        
        pnlTablas.add(pnlVentas);
        pnlTablas.add(pnlBajas);

        pnlCentro.add(pnlTablas);

        // Subpanel Derecho (Gráfico)
        pnlGraficoStock = new JPanel(new BorderLayout());
        pnlGraficoStock.setBackground(FONDO);
        pnlGraficoStock.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        pnlCentro.add(pnlGraficoStock);

        panel.add(pnlCentro, BorderLayout.CENTER);
        return panel;
    }

    // UTILERÍAS DE DISEÑO
    private JButton crearBotonApp(String texto, Color colorFondo) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(colorFondo);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(130, 35));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JLabel crearLabelKPI(String titulo, String valorInicial) {
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.setBackground(new Color(245, 247, 250));
        pnl.setBorder(new LineBorder(Color.LIGHT_GRAY, 1));
        
        JLabel lblTitulo = new JLabel(titulo, SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblTitulo.setForeground(Color.DARK_GRAY);
        
        JLabel lblValor = new JLabel(valorInicial, SwingConstants.CENTER);
        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblValor.setForeground(VERDE_FM);
        
        pnl.add(lblTitulo, BorderLayout.NORTH);
        pnl.add(lblValor, BorderLayout.CENTER);
        return lblValor;
    }

    // LÓGICA DE BASE DE DATOS
    private void limpiarCamposProductos() {
        txtCodigo.setText(""); txtNombre.setText(""); txtPrecio.setText(""); txtStock.setText("");
        idProductoSeleccionado = -1; 
        
        // Restaurar el campo para permitir escribir códigos nuevos
        txtCodigo.setEditable(true); 
        
        txtCodigo.requestFocus();
    }

    private void limpiarCamposUsuarios() {
        txtNomUsuario.setText(""); txtClave.setText(""); cbxRol.setSelectedIndex(0);
        idUsuarioSeleccionado = -1; txtNomUsuario.requestFocus();
    }

    private void cargarProductos() {
        DefaultTableModel modelo = new DefaultTableModel(new String[]{"ID", "Código", "Nombre", "Precio", "Stock"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tblProductos.setModel(modelo);
        String sql = "SELECT id_producto, codigo_barras, nombre, precio, stock FROM Productos WHERE estado = 1";
        try (Connection conn = Conexion.conectar(); PreparedStatement pst = conn.prepareStatement(sql); ResultSet rs = pst.executeQuery()) {
            while (rs.next()) modelo.addRow(new Object[]{rs.getInt(1), rs.getString(2), rs.getString(3), rs.getDouble(4), rs.getInt(5)});
        } catch (SQLException e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    private void buscarProductos(String txt) {
        DefaultTableModel modelo = (DefaultTableModel) tblProductos.getModel();
        modelo.setRowCount(0);
        String sql = "SELECT id_producto, codigo_barras, nombre, precio, stock FROM Productos WHERE estado = 1 AND (nombre LIKE ? OR codigo_barras LIKE ?)";
        try (Connection conn = Conexion.conectar(); PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, "%" + txt + "%"); pst.setString(2, "%" + txt + "%");
            ResultSet rs = pst.executeQuery();
            while (rs.next()) modelo.addRow(new Object[]{rs.getInt(1), rs.getString(2), rs.getString(3), rs.getDouble(4), rs.getInt(5)});
        } catch (SQLException e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    private void guardarProducto() {
        String codigo = txtCodigo.getText().trim(); String nombre = txtNombre.getText().trim();
        if (codigo.isEmpty() || nombre.isEmpty() || txtPrecio.getText().isEmpty() || txtStock.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos obligatorios."); return;
        }
        try {
            double precio = Double.parseDouble(txtPrecio.getText().trim());
            int stock = Integer.parseInt(txtStock.getText().trim());
            if (stock < 1 || stock > 500) { JOptionPane.showMessageDialog(this, "Stock debe ser 1-500"); return; }
            if (precio <= 0) { JOptionPane.showMessageDialog(this, "Precio debe ser > 0"); return; }

            String sql = "INSERT INTO Productos (codigo_barras, nombre, precio, stock, estado) VALUES (?, ?, ?, ?, 1) ON DUPLICATE KEY UPDATE nombre = ?, precio = ?, stock = ?, estado = 1";
            try (Connection conn = Conexion.conectar(); PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setString(1, codigo); pst.setString(2, nombre); pst.setDouble(3, precio); pst.setInt(4, stock);
                pst.setString(5, nombre); pst.setDouble(6, precio); pst.setInt(7, stock);
                pst.executeUpdate();
                JOptionPane.showMessageDialog(this, "Guardado correctamente.");
                cargarProductos(); cargarProductosEliminados(); cargarGraficoStock(); limpiarCamposProductos();
            }
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error de datos numéricos o SQL."); }
    }

    private void modificarProducto() {
        if (idProductoSeleccionado == -1) { JOptionPane.showMessageDialog(this, "Seleccione un producto."); return; }
        try {
            double precio = Double.parseDouble(txtPrecio.getText().trim());
            int stock = Integer.parseInt(txtStock.getText().trim());
            String sql = "UPDATE Productos SET nombre=?, precio=?, stock=? WHERE id_producto=?";
            try (Connection conn = Conexion.conectar(); PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setString(1, txtNombre.getText().trim()); pst.setDouble(2, precio); pst.setInt(3, stock); pst.setInt(4, idProductoSeleccionado);
                pst.executeUpdate();
                cargarProductos(); cargarGraficoStock(); limpiarCamposProductos();
            }
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error de datos numéricos o SQL."); }
    }

    private void eliminarProducto() {
        if (idProductoSeleccionado == -1) { JOptionPane.showMessageDialog(this, "Seleccione un producto."); return; }
        
        if (JOptionPane.showConfirmDialog(this, "¿Eliminar este producto?", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            
            try (Connection conn = Conexion.conectar()) {
                
                // INTENTO 1: ELIMINACIÓN FÍSICA (Borrado real)
                String sqlFisica = "DELETE FROM Productos WHERE id_producto=?";
                try (PreparedStatement pstFisica = conn.prepareStatement(sqlFisica)) {
                    pstFisica.setInt(1, idProductoSeleccionado);
                    pstFisica.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Producto eliminado FÍSICAMENTE de la base de datos.");
                }
                
            } catch (SQLException e) {
                // Captura el error de Violación de Llave Foránea (SQLState 23000)
                if (e.getSQLState() != null && e.getSQLState().startsWith("23")) {
                    
                    // INTENTO 2: ELIMINACIÓN LÓGICA (Ocultamiento por seguridad)
                    String sqlLogica = "UPDATE Productos SET estado=0 WHERE id_producto=?";
                    try (Connection conn2 = Conexion.conectar(); PreparedStatement pstLogica = conn2.prepareStatement(sqlLogica)) {
                        pstLogica.setInt(1, idProductoSeleccionado);
                        pstLogica.executeUpdate();
                        JOptionPane.showMessageDialog(this, "El producto tiene ventas registradas. Se aplicó ELIMINACIÓN LÓGICA para proteger el historial.");
                    } catch (SQLException ex2) {
                        JOptionPane.showMessageDialog(this, "Error en eliminación lógica: " + ex2.getMessage());
                    }
                    
                } else {
                    JOptionPane.showMessageDialog(this, "Error SQL: " + e.getMessage());
                }
            } finally {
                // Actualiza las tablas visuales pase lo que pase
                cargarProductos(); 
                cargarProductosEliminados(); 
                cargarGraficoStock(); 
                limpiarCamposProductos();
            }
        }
    }

    private void cargarUsuarios() {
        DefaultTableModel modelo = new DefaultTableModel(new String[]{"ID", "Usuario", "Rol"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tblUsuarios.setModel(modelo);
        String sql = "SELECT u.id_usuario, u.username, r.nombre_rol FROM Usuarios u INNER JOIN Roles r ON u.id_rol = r.id_rol WHERE u.estado = 1";
        try (Connection conn = Conexion.conectar(); PreparedStatement pst = conn.prepareStatement(sql); ResultSet rs = pst.executeQuery()) {
            while (rs.next()) modelo.addRow(new Object[]{rs.getInt(1), rs.getString(2), rs.getString(3)});
        } catch (SQLException e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    private void guardarUsuario() {
        String usu = txtNomUsuario.getText().trim(); String pass = txtClave.getText().trim();
        int rol = cbxRol.getSelectedItem().equals("Administrador") ? 1 : 2;
        if (usu.isEmpty() || pass.isEmpty()) { JOptionPane.showMessageDialog(this, "Campos vacíos."); return; }
        String sql = "INSERT INTO Usuarios (username, password, id_rol, estado) VALUES (?, ?, ?, 1) ON DUPLICATE KEY UPDATE password=?, id_rol=?, estado=1";
        try (Connection conn = Conexion.conectar(); PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, usu); pst.setString(2, pass); pst.setInt(3, rol);
            pst.setString(4, pass); pst.setInt(5, rol);
            pst.executeUpdate(); cargarUsuarios(); limpiarCamposUsuarios();
        } catch (SQLException e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    private void modificarUsuario() {
        if (idUsuarioSeleccionado == -1) { JOptionPane.showMessageDialog(this, "Seleccione un usuario."); return; }
        String usu = txtNomUsuario.getText().trim(); String pass = txtClave.getText().trim();
        int rol = cbxRol.getSelectedItem().equals("Administrador") ? 1 : 2;
        String sql = pass.isEmpty() ? "UPDATE Usuarios SET username=?, id_rol=? WHERE id_usuario=?" : "UPDATE Usuarios SET username=?, password=?, id_rol=? WHERE id_usuario=?";
        try (Connection conn = Conexion.conectar(); PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, usu);
            if (pass.isEmpty()) { pst.setInt(2, rol); pst.setInt(3, idUsuarioSeleccionado); } 
            else { pst.setString(2, pass); pst.setInt(3, rol); pst.setInt(4, idUsuarioSeleccionado); }
            pst.executeUpdate(); cargarUsuarios(); limpiarCamposUsuarios();
        } catch (SQLException e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    private void eliminarUsuario() {
        if (idUsuarioSeleccionado == -1) { JOptionPane.showMessageDialog(this, "Seleccione un usuario."); return; }
        if (JOptionPane.showConfirmDialog(this, "¿Eliminar usuario?", "Confirmar", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            String sql = "UPDATE Usuarios SET estado=0 WHERE id_usuario=?";
            try (Connection conn = Conexion.conectar(); PreparedStatement pst = conn.prepareStatement(sql)) {
                pst.setInt(1, idUsuarioSeleccionado); pst.executeUpdate(); cargarUsuarios(); limpiarCamposUsuarios();
            } catch (SQLException e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
        }
    }

    private void cargarHistorialVentas() {
        DefaultTableModel modelo = new DefaultTableModel(new String[]{"ID Venta", "Cliente", "Documento", "Total", "Fecha"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tblVentas.setModel(modelo);
        String sql = "SELECT id_venta, cliente, documento, total, fecha FROM Ventas ORDER BY fecha DESC";
        try (Connection conn = Conexion.conectar(); PreparedStatement pst = conn.prepareStatement(sql); ResultSet rs = pst.executeQuery()) {
            while (rs.next()) modelo.addRow(new Object[]{rs.getInt(1), rs.getString(2), rs.getString(3), rs.getDouble(4), rs.getTimestamp(5)});
        } catch (SQLException e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    private void cargarProductosEliminados() {
        DefaultTableModel modelo = new DefaultTableModel(new String[]{"ID", "Código", "Nombre", "Stock"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tblEliminados.setModel(modelo);
        String sql = "SELECT id_producto, codigo_barras, nombre, stock FROM Productos WHERE estado = 0";
        try (Connection conn = Conexion.conectar(); PreparedStatement pst = conn.prepareStatement(sql); ResultSet rs = pst.executeQuery()) {
            while (rs.next()) modelo.addRow(new Object[]{rs.getInt(1), rs.getString(2), rs.getString(3), rs.getInt(4)});
        } catch (SQLException e) { JOptionPane.showMessageDialog(this, "Error: " + e.getMessage()); }
    }

    private void cargarIndicadores() {
        String sqlV = "SELECT SUM(total) as Ingresos, COUNT(id_venta) as Transacciones, MAX(total) as Maximo, MIN(total) as Minimo, AVG(total) as Promedio FROM Ventas";
        String sqlS = "SELECT COUNT(id_producto) as Agotados FROM Productos WHERE stock = 0 AND estado = 1";
        try (Connection conn = Conexion.conectar()) {
            try (PreparedStatement pst = conn.prepareStatement(sqlV); ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    lblIngresosTotales.setText(String.format("S/ %.2f", rs.getDouble("Ingresos")));
                    lblTotalTransacciones.setText(String.valueOf(rs.getInt("Transacciones")));
                    lblTicketMaximo.setText(String.format("S/ %.2f", rs.getDouble("Maximo")));
                    lblTicketMinimo.setText(String.format("S/ %.2f", rs.getDouble("Minimo")));
                    lblTicketPromedio.setText(String.format("S/ %.2f", rs.getDouble("Promedio")));
                }
            }
            try (PreparedStatement pstS = conn.prepareStatement(sqlS); ResultSet rsS = pstS.executeQuery()) {
                if (rsS.next()) lblAlertasStock.setText(String.valueOf(rsS.getInt("Agotados")));
            }
        } catch (SQLException e) { JOptionPane.showMessageDialog(this, "Error KPIs: " + e.getMessage()); }
    }

    private void cargarGraficoStock() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        String sql = "SELECT nombre, stock FROM Productos WHERE estado = 1 ORDER BY stock ASC LIMIT 10";
        try (Connection conn = Conexion.conectar(); PreparedStatement pst = conn.prepareStatement(sql); ResultSet rs = pst.executeQuery()) {
            while (rs.next()) dataset.setValue(rs.getInt("stock"), "Unidades", rs.getString("nombre"));
        } catch (SQLException e) { return; }

        JFreeChart grafico = ChartFactory.createBarChart("Top 10: Alerta de Reposición", "", "Unidades Disponibles", dataset, PlotOrientation.HORIZONTAL, false, true, false);
        CategoryPlot plot = grafico.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinesVisible(false);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
        
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setMaximumBarWidth(0.15);
        renderer.setBarPainter(new StandardBarPainter());
        renderer.setSeriesPaint(0, CELESTE_FM); // Color del logo

        ChartPanel panelGrafico = new ChartPanel(grafico);
        pnlGraficoStock.removeAll();
        pnlGraficoStock.add(panelGrafico, BorderLayout.CENTER);
        pnlGraficoStock.validate();
    }

    public static void main(String args[]) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
        java.awt.EventQueue.invokeLater(() -> new FrmAdmin().setVisible(true));
    }
}