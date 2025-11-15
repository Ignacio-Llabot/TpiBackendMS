package org.tpibackend.mstransportes.dto;

public class SolicitudRemotaDTO {

    private Integer idSolicitud;
    private ContenedorRemotoDTO contenedor;

    public Integer getIdSolicitud() {
        return idSolicitud;
    }

    public void setIdSolicitud(Integer idSolicitud) {
        this.idSolicitud = idSolicitud;
    }

    public ContenedorRemotoDTO getContenedor() {
        return contenedor;
    }

    public void setContenedor(ContenedorRemotoDTO contenedor) {
        this.contenedor = contenedor;
    }

    public static class ContenedorRemotoDTO {
        private Integer idContenedor;
        private Double peso;
        private Double volumen;

        public Integer getIdContenedor() {
            return idContenedor;
        }

        public void setIdContenedor(Integer idContenedor) {
            this.idContenedor = idContenedor;
        }

        public Double getPeso() {
            return peso;
        }

        public void setPeso(Double peso) {
            this.peso = peso;
        }

        public Double getVolumen() {
            return volumen;
        }

        public void setVolumen(Double volumen) {
            this.volumen = volumen;
        }
    }
}
