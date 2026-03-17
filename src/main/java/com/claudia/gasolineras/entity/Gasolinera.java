package com.claudia.gasolineras.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "gasolinera")
public class Gasolinera {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    private String marca;
    private String direccion;
    private String municipio;
    private String provincia;

    @Column(nullable = false)
    private Double latitud;

    @Column(nullable = false)
    private Double longitud;

    @Column(name = "precio_gasolina95", nullable = false)
    private Double precioGasolina95;

    @Column(name = "precio_diesel")
    private Double precioDiesel;

    @ManyToOne
    @JoinColumn(name = "id_actualizacion", nullable = false)
    private ActualizacionDatos actualizacion;

    public Gasolinera() {}

    public Long getId() { return id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getMunicipio() { return municipio; }
    public void setMunicipio(String municipio) { this.municipio = municipio; }

    public String getProvincia() { return provincia; }
    public void setProvincia(String provincia) { this.provincia = provincia; }

    public Double getLatitud() { return latitud; }
    public void setLatitud(Double latitud) { this.latitud = latitud; }

    public Double getLongitud() { return longitud; }
    public void setLongitud(Double longitud) { this.longitud = longitud; }

    public Double getPrecioGasolina95() { return precioGasolina95; }
    public void setPrecioGasolina95(Double precioGasolina95) { this.precioGasolina95 = precioGasolina95; }

    public Double getPrecioDiesel() { return precioDiesel; }
    public void setPrecioDiesel(Double precioDiesel) { this.precioDiesel = precioDiesel; }

    public ActualizacionDatos getActualizacion() { return actualizacion; }
    public void setActualizacion(ActualizacionDatos actualizacion) { this.actualizacion = actualizacion; }
}