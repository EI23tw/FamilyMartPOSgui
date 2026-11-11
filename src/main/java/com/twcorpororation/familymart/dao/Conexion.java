package com.twcorpororation.familymart.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {
    
    // Rutas de conexión para MySQL (con allowPublicKeyRetrieval=true)
    private static final String URL = "jdbc:mysql://localhost:3306/FamilyMartDB?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC";
    private static final String USER = "root"; // Tu usuario de MySQL Workbench
    private static final String PASSWORD = "admin"; // Pon tu clave de MySQL aquí

    public static Connection conectar() {
        Connection conn = null;
        try {
            // Establecer la conexión
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("¡Conexión a MySQL exitosa!");
        } catch (SQLException e) {
            System.err.println("Error de conexión a la base de datos: " + e.getMessage());
        }
        return conn;
    }
}
