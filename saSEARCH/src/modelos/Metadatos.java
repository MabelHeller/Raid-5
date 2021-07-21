/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelos;

/**
 *
 * @author Heller
 */
public class Metadatos {
    private int id; 
    private String nombre;
    private String fecha;
    private String propietario;
    private int edicion;

    public Metadatos(int id, String nombre, String fecha, String propietario, int edicion) {
        this.id = id;
        this.nombre = nombre;
        this.fecha = fecha;
        this.propietario = propietario;
        this.edicion=edicion;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getFecha() {
        return fecha;
    }

    public String getPropietario() {
        return propietario;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public void setPropietario(String propietario) {
        this.propietario = propietario;
    }  

    public int getEdicion() {
        return edicion;
    }

    public void setEdicion(int edicion) {
        this.edicion = edicion;
    }

    @Override
    public String toString() {
        return "Metadatos{" + "id=" + id + ", nombre=" + nombre + ", fecha=" + fecha + ", propietario=" + propietario + ", edicion=" + edicion + '}';
    }
    
    
    
}
