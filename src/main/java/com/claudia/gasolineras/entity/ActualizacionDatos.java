package com.claudia.gasolineras.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "actualizacion_datos")
public class ActualizacionDatos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha_descarga", nullable = false)
    private LocalDateTime fechaDescarga;

    @Column(nullable = false)
    private String fuente;

    @Column(name = "numero_registros")
    private Integer numeroRegistros;

    public ActualizacionDatos() {}

    public Long getId() { return id; }

    public LocalDateTime getFechaDescarga() { return fechaDescarga; }
    public void setFechaDescarga(LocalDateTime fechaDescarga) { this.fechaDescarga = fechaDescarga; }

    public String getFuente() { return fuente; }
    public void setFuente(String fuente) { this.fuente = fuente; }

    public Integer getNumeroRegistros() { return numeroRegistros; }
    public void setNumeroRegistros(Integer numeroRegistros) { this.numeroRegistros = numeroRegistros; }
}
