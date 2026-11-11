package com.twcorpororation.familymart;

public class FamilyMart {

    public static void main(String[] args) {
        //Flatlaf
        try {
            javax.swing.UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatLightLaf());
        } catch (Exception ex) {
            System.err.println("No se pudo inicializar FlatLaf");
        }
        // ---------------------------------------------------

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                FrmLogin ventanaLogin = new FrmLogin();
                
                // Centrar la ventana en la pantalla
                ventanaLogin.setLocationRelativeTo(null);
                
                // Hacer visible la ventana
                ventanaLogin.setVisible(true);
            }
        });
    }
}