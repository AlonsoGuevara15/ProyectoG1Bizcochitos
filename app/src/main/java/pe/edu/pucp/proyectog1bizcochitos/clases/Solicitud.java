package pe.edu.pucp.proyectog1bizcochitos.clases;

import java.io.Serializable;

public class Solicitud implements Serializable {
    private String estado = "Pendiente";
    private String motivo, direccion;
    private String solicId = "";
    private double lat = 0, lon = 0;
    private String deviceid;
    private boolean correoNotif;


    public Solicitud() {

    }

    public Solicitud(String estado, String motivo, String direccion, String solicId, double lat, double lon, String deviceid, boolean correoNotif) {
        this.estado = estado;
        this.motivo = motivo;
        this.direccion = direccion;
        this.solicId = solicId;
        this.lat = lat;
        this.lon = lon;
        this.deviceid = deviceid;
        this.correoNotif = correoNotif;
    }

    public Solicitud(String motivo, String direccion, String deviceid, boolean correoNotif) {
        this.motivo = motivo;
        this.direccion = direccion;
        this.deviceid = deviceid;
        this.correoNotif = correoNotif;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getSolicId() {
        return solicId;
    }

    public void setSolicId(String solicId) {
        this.solicId = solicId;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getDeviceid() {
        return deviceid;
    }

    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public boolean isCorreoNotif() {
        return correoNotif;
    }

    public void setCorreoNotif(boolean correoNotif) {
        this.correoNotif = correoNotif;
    }
}
