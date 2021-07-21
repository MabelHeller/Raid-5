/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servicios;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Heller
 */
public class Conexion {

    private static Connection cnx = null;

    //Conexion con la base de datos en mysql atraves del conector JDBC Driver 
    
    public Connection obtener() throws SQLException, ClassNotFoundException {
        if (cnx == null) {
            try {
                Class.forName("com.mysql.jdbc.Driver");     
                String URL = "jdbc:mysql://localhost:3306/ProyectoRedes";               
                cnx = DriverManager.getConnection(URL,"root","");              
            } catch (SQLException ex) {
                throw new SQLException(ex);
            } catch (ClassNotFoundException ex) {
                throw new ClassCastException(ex.getMessage());
            }
        }
        return cnx;
    }
    
    
    

    public static void cerrar() throws SQLException {
        if (cnx != null) {
            cnx.close();
        }
    }

}