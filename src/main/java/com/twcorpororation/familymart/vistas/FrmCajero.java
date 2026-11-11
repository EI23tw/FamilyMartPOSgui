package com.twcorpororation.familymart.vistas;

import com.twcorpororation.familymart.dao.Conexion;
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.Image;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FrmCajero extends JFrame {
    private JTable tblCatalogo;
    private JTable tblTicket;
    private DefaultTableModel modeloTicket;
    private DefaultTableModel modeloCat;
    
    private JTextField txtBuscarCajero;
    private JTextField txtCliente;
    private JTextField txtDocumento;
    private JComboBox<String> cbxMetodoPago;
    private JSpinner spnCantidad;
    private JLabel lblTotal;

    private final Color VERDE_FM = new Color(0, 168, 89);
    private final Color CELESTE_FM = new Color(0, 174, 239);
    private final Color FONDO_GRIS = new Color(245, 247, 250);

    public FrmCajero() {
        configurarVentana();
        inicializarComponentes();
        cargarCatalogo();
    }

    private void configurarVentana() {
        setTitle("FamilyMart - Módulo de Caja");
        setSize(1200, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(Color.WHITE);
    }

    private void inicializarComponentes() {
        // CABECERA
        JPanel pnlCabecera = new JPanel(new BorderLayout());
        pnlCabecera.setBackground(VERDE_FM);
        pnlCabecera.setPreferredSize(new Dimension(1200, 60));
        pnlCabecera.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel lblTitulo = new JLabel("PUNTO DE VENTA - CAJERO");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(Color.WHITE);
        pnlCabecera.add(lblTitulo, BorderLayout.WEST);

        JButton btnCerrarSesion = new JButton("Cerrar Sesión");
        btnCerrarSesion.setBackground(Color.WHITE);
        btnCerrarSesion.setForeground(VERDE_FM);
        btnCerrarSesion.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCerrarSesion.setFocusPainted(false);
        btnCerrarSesion.addActionListener(e -> cerrarSesion());
        pnlCabecera.add(btnCerrarSesion, BorderLayout.EAST);

        add(pnlCabecera, BorderLayout.NORTH);

        // 2. PANEL IZQUIERDO (CATÁLOGO DE PRODUCTOS)
        JPanel pnlIzquierdo = new JPanel(new BorderLayout(0, 10));
        pnlIzquierdo.setBackground(Color.WHITE);
        pnlIzquierdo.setBorder(new EmptyBorder(10, 20, 20, 10));

        // Buscador
        JPanel pnlBuscador = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlBuscador.setBackground(Color.WHITE);
        JLabel lblBuscar = new JLabel("Buscar Producto:");
        lblBuscar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        txtBuscarCajero = new JTextField(25);
        txtBuscarCajero.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtBuscarCajero.setBorder(new LineBorder(CELESTE_FM, 1));
        txtBuscarCajero.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                buscarCatalogo(txtBuscarCajero.getText().trim());
            }
        });
        pnlBuscador.add(lblBuscar);
        pnlBuscador.add(txtBuscarCajero);
        pnlIzquierdo.add(pnlBuscador, BorderLayout.NORTH);

        // Tabla Catálogo
        modeloCat = new DefaultTableModel(new String[]{"ID", "Código", "Nombre", "Precio", "Stock"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tblCatalogo = new JTable(modeloCat);
        tblCatalogo.setRowHeight(25);
        JScrollPane scrollCatalogo = new JScrollPane(tblCatalogo);
        scrollCatalogo.getViewport().setBackground(Color.WHITE);
        pnlIzquierdo.add(scrollCatalogo, BorderLayout.CENTER);

        // Controles de Agregado
        JPanel pnlAgregar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlAgregar.setBackground(Color.WHITE);
        spnCantidad = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        spnCantidad.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        spnCantidad.setPreferredSize(new Dimension(80, 35));
        
        JButton btnAgregar = new JButton("AGREGAR AL TICKET");
        btnAgregar.setBackground(CELESTE_FM);
        btnAgregar.setForeground(Color.WHITE);
        btnAgregar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAgregar.setFocusPainted(false);
        btnAgregar.addActionListener(e -> agregarAlTicket());
        
        pnlAgregar.add(new JLabel("Cantidad:"));
        pnlAgregar.add(spnCantidad);
        pnlAgregar.add(btnAgregar);
        pnlIzquierdo.add(pnlAgregar, BorderLayout.SOUTH);

        add(pnlIzquierdo, BorderLayout.CENTER);

        // 3. PANEL DERECHO (TICKET DE VENTA)
        JPanel pnlDerecho = new JPanel(new BorderLayout(0, 10));
        pnlDerecho.setBackground(FONDO_GRIS);
        pnlDerecho.setPreferredSize(new Dimension(450, 0)); // Ancho fijo
        pnlDerecho.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.LIGHT_GRAY, 1),
                new EmptyBorder(10, 10, 20, 10)));

        // Datos del Cliente
        JPanel pnlCliente = new JPanel(new GridLayout(3, 2, 5, 5));
        pnlCliente.setBackground(FONDO_GRIS);
        pnlCliente.setBorder(BorderFactory.createTitledBorder(new LineBorder(Color.GRAY), "Datos del Cliente", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Segoe UI", Font.BOLD, 12)));
        
        pnlCliente.add(new JLabel("Cliente:"));
        txtCliente = new JTextField();
        pnlCliente.add(txtCliente);
        
        pnlCliente.add(new JLabel("DNI/RUC:"));
        txtDocumento = new JTextField();
        
        // --- INICIO: VALIDACIÓN DE TIPO Y LONGITUD (DNI/RUC) ---
        txtDocumento.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyTyped(java.awt.event.KeyEvent evt) {
                char tecla = evt.getKeyChar();
                
                // 1. REQUISITO: TIPO DE DATO (Bloquea cualquier tecla que no sea un número)
                if (!Character.isDigit(tecla)) {
                    evt.consume(); // Cancela la acción de la tecla
                }
                
                // 2. REQUISITO: LONGITUD (Evita que escriban más de 11 números, el máximo para RUC)
                if (txtDocumento.getText().length() >= 11) {
                    evt.consume(); 
                }
            }
        });
        // --- FIN: VALIDACIÓN ---
        
        pnlCliente.add(txtDocumento);
        
        pnlCliente.add(new JLabel("Forma de Pago:"));
        cbxMetodoPago = new JComboBox<>(new String[]{"Efectivo", "Tarjeta", "Yape/Plin"});
        pnlCliente.add(cbxMetodoPago);
        
        pnlDerecho.add(pnlCliente, BorderLayout.NORTH);

        // Tabla Ticket
        modeloTicket = new DefaultTableModel(new String[]{"ID", "PRODUCTO", "PRECIO", "CANT.", "SUBTOTAL"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tblTicket = new JTable(modeloTicket);
        tblTicket.setRowHeight(25);
        JScrollPane scrollTicket = new JScrollPane(tblTicket);
        pnlDerecho.add(scrollTicket, BorderLayout.CENTER);

        // Totales y Cobro
        JPanel pnlCobro = new JPanel(new BorderLayout());
        pnlCobro.setBackground(FONDO_GRIS);
        
        JButton btnQuitar = new JButton("Quitar Artículo");
        btnQuitar.setForeground(Color.RED);
        btnQuitar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnQuitar.setFocusPainted(false);
        btnQuitar.addActionListener(e -> quitarArticulo());
        pnlCobro.add(btnQuitar, BorderLayout.NORTH);

        lblTotal = new JLabel("TOTAL: S/ 0.00", SwingConstants.CENTER);
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTotal.setForeground(VERDE_FM);
        lblTotal.setBorder(new EmptyBorder(15, 0, 15, 0));
        pnlCobro.add(lblTotal, BorderLayout.CENTER);

        JButton btnCobrar = new JButton("COBRAR Y EMITIR BOLETA");
        btnCobrar.setBackground(VERDE_FM);
        btnCobrar.setForeground(Color.WHITE);
        btnCobrar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnCobrar.setPreferredSize(new Dimension(0, 50));
        btnCobrar.setFocusPainted(false);
        btnCobrar.addActionListener(e -> cobrarYEmitirBoleta());
        pnlCobro.add(btnCobrar, BorderLayout.SOUTH);

        pnlDerecho.add(pnlCobro, BorderLayout.SOUTH);

        add(pnlDerecho, BorderLayout.EAST);
    }

    // LÓGICA DEL NEGOCIO
    private void cargarCatalogo() {
        modeloCat.setRowCount(0);
        String sql = "SELECT id_producto, codigo_barras, nombre, precio, stock FROM Productos WHERE estado = 1 AND stock > 0";
        try (Connection conn = Conexion.conectar(); PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                modeloCat.addRow(new Object[]{
                    rs.getInt("id_producto"), rs.getString("codigo_barras"), rs.getString("nombre"), rs.getDouble("precio"), rs.getInt("stock")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void buscarCatalogo(String textoBusqueda) {
        modeloCat.setRowCount(0);
        String sql = "SELECT id_producto, codigo_barras, nombre, precio, stock FROM Productos WHERE estado = 1 AND stock > 0 AND (nombre LIKE ? OR codigo_barras LIKE ?)";
        try (Connection conn = Conexion.conectar(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + textoBusqueda + "%");
            pstmt.setString(2, "%" + textoBusqueda + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                modeloCat.addRow(new Object[]{
                    rs.getInt("id_producto"), rs.getString("codigo_barras"), rs.getString("nombre"), rs.getDouble("precio"), rs.getInt("stock")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al buscar: " + e.getMessage());
        }
    }

    private void agregarAlTicket() {
        int filaSeleccionada = tblCatalogo.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto del catálogo.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int cantidad = (Integer) spnCantidad.getValue();
        int stockDisponible = Integer.parseInt(tblCatalogo.getValueAt(filaSeleccionada, 4).toString());
        
        if (cantidad > stockDisponible) {
            JOptionPane.showMessageDialog(this, "Stock insuficiente. Solo quedan " + stockDisponible + " unidades.");
            return;
        }

        String id = tblCatalogo.getValueAt(filaSeleccionada, 0).toString();
        String nombre = tblCatalogo.getValueAt(filaSeleccionada, 2).toString();
        double precio = Double.parseDouble(tblCatalogo.getValueAt(filaSeleccionada, 3).toString());
        double subtotal = precio * cantidad;

        modeloTicket.addRow(new Object[]{id, nombre, precio, cantidad, subtotal});
        calcularTotal();
        spnCantidad.setValue(1); // Resetear spinner
    }

    private void quitarArticulo() {
        int filaSeleccionada = tblTicket.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto del ticket para quitarlo.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        modeloTicket.removeRow(filaSeleccionada);
        calcularTotal();
    }

    private void calcularTotal() {
        double total = 0.0;
        for (int i = 0; i < tblTicket.getRowCount(); i++) {
            Object valorCelda = tblTicket.getValueAt(i, 4);
            if (valorCelda != null && !valorCelda.toString().trim().isEmpty()) {
                total += Double.parseDouble(valorCelda.toString());
            }
        }
        lblTotal.setText(String.format("TOTAL: S/ %.2f", total));
    }

    private void cobrarYEmitirBoleta() {
        if (tblTicket.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "El ticket está vacío. Agregue productos primero.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nombreCliente = txtCliente.getText().trim();
        String documentoCliente = txtDocumento.getText().trim();
        String metodoPago = cbxMetodoPago.getSelectedItem().toString();

        double totalVenta = 0.0;
        for (int i = 0; i < tblTicket.getRowCount(); i++) {
            totalVenta += Double.parseDouble(tblTicket.getValueAt(i, 4).toString());
        }

        String sqlVenta = "INSERT INTO Ventas (cliente, documento, metodo_pago, total) VALUES (?, ?, ?, ?)";
        String sqlDetalle = "INSERT INTO DetalleVenta (id_venta, id_producto, cantidad, precio_unitario, subtotal) VALUES (?, ?, ?, ?, ?)";
        String sqlActualizarStock = "UPDATE Productos SET stock = stock - ? WHERE id_producto = ?";
        int idVentaGenerada = 0;

        try (Connection conn = Conexion.conectar()) {
            conn.setAutoCommit(false);

            // Cabecera
            try (PreparedStatement pstmtVenta = conn.prepareStatement(sqlVenta, java.sql.Statement.RETURN_GENERATED_KEYS)) {
                pstmtVenta.setString(1, nombreCliente.isEmpty() ? "Público General" : nombreCliente);
                pstmtVenta.setString(2, documentoCliente.isEmpty() ? "00000000" : documentoCliente);
                pstmtVenta.setString(3, metodoPago);
                pstmtVenta.setDouble(4, totalVenta);
                pstmtVenta.executeUpdate();
                ResultSet rsKeys = pstmtVenta.getGeneratedKeys();
                if (rsKeys.next()) idVentaGenerada = rsKeys.getInt(1);
            }

            // Detalles
            try (PreparedStatement pstmtDetalle = conn.prepareStatement(sqlDetalle); PreparedStatement pstmtStock = conn.prepareStatement(sqlActualizarStock)) {
                for (int i = 0; i < tblTicket.getRowCount(); i++) {
                    int idProducto = Integer.parseInt(tblTicket.getValueAt(i, 0).toString());
                    int cantidad = Integer.parseInt(tblTicket.getValueAt(i, 3).toString());
                    double precio = Double.parseDouble(tblTicket.getValueAt(i, 2).toString());
                    double subtotal = Double.parseDouble(tblTicket.getValueAt(i, 4).toString());

                    pstmtDetalle.setInt(1, idVentaGenerada);
                    pstmtDetalle.setInt(2, idProducto);
                    pstmtDetalle.setInt(3, cantidad);
                    pstmtDetalle.setDouble(4, precio);
                    pstmtDetalle.setDouble(5, subtotal);
                    pstmtDetalle.executeUpdate();

                    pstmtStock.setInt(1, cantidad);
                    pstmtStock.setInt(2, idProducto);
                    pstmtStock.executeUpdate();
                }
            }
            conn.commit();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al procesar: " + e.getMessage(), "Error SQL", JOptionPane.ERROR_MESSAGE);
            return; 
        }

        // Generar PDF
        Document documento = new Document();
        try {
            String ruta = "D:\\AngelBLG\\Escritorio\\Boleta_FamilyMart_" + idVentaGenerada + ".pdf";
            PdfWriter.getInstance(documento, new FileOutputStream(ruta));
            documento.open();

            try {
                Image logo = Image.getInstance("src/main/resources/img/logo.png");
                logo.scaleToFit(100, 100);
                logo.setAlignment(Image.ALIGN_CENTER);
                documento.add(logo);
            } catch (Exception e) {
                System.err.println("No se cargó el logo en PDF: " + e.getMessage());
            }

            documento.add(new Paragraph("=== MINIMARKET FAMILY MART ==="));
            documento.add(new Paragraph("Ticket Nro: " + String.format("%06d", idVentaGenerada)));
            documento.add(new Paragraph("Fecha: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date())));
            documento.add(new Paragraph("Cliente: " + (nombreCliente.isEmpty() ? "Público General" : nombreCliente)));
            documento.add(new Paragraph("DNI/RUC: " + (documentoCliente.isEmpty() ? "00000000" : documentoCliente)));
            documento.add(new Paragraph("Forma de Pago: " + metodoPago));
            documento.add(new Paragraph("--------------------------------------------------"));
            documento.add(new Paragraph("CANT.  |  PRODUCTO  |  SUBTOTAL"));
            documento.add(new Paragraph("--------------------------------------------------"));

            for (int i = 0; i < tblTicket.getRowCount(); i++) {
                String nombre = tblTicket.getValueAt(i, 1).toString();
                String precio = tblTicket.getValueAt(i, 2).toString();
                String cantidad = tblTicket.getValueAt(i, 3).toString();
                String subtotal = tblTicket.getValueAt(i, 4).toString();
                documento.add(new Paragraph(cantidad + " x " + nombre + " (S/" + precio + ") = S/ " + subtotal));
            }

            documento.add(new Paragraph("--------------------------------------------------"));
            documento.add(new Paragraph(String.format("TOTAL A PAGAR: S/ %.2f", totalVenta)));
            documento.add(new Paragraph("¡Gracias por su compra!"));
            documento.close();

            JOptionPane.showMessageDialog(this, "Venta Exitosa. Ticket Nro " + idVentaGenerada + " generado.");

            // Limpiar
            modeloTicket.setRowCount(0);
            calcularTotal();
            txtCliente.setText("");
            txtDocumento.setText("");
            cbxMetodoPago.setSelectedIndex(0);
            cargarCatalogo();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al generar PDF: " + e.getMessage());
        }
    }

    private void cerrarSesion() {
        com.twcorpororation.familymart.FrmLogin login = new com.twcorpororation.familymart.FrmLogin();
        login.setVisible(true);
        this.dispose();
    }

    public static void main(String args[]) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } 
        catch (Exception ignored) {}
        java.awt.EventQueue.invokeLater(() -> new FrmCajero().setVisible(true));
    }
}