package pe.edu.pucp.proyectog1bizcochitos.clases;

import java.io.Serializable;

public class Device implements Serializable {
    private String tipo, fotoid, marca, caracteristicas, incluye;
    private String deviceId="";
    private int stock;

    public Device() {
    }

    public Device(String tipo, String fotoid, String marca, String caracteristicas, String incluye, String deviceId, int stock) {
        this.tipo = tipo;
        this.fotoid = fotoid;
        this.marca = marca;
        this.caracteristicas = caracteristicas;
        this.incluye = incluye;
        this.deviceId = deviceId;
        this.stock = stock;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getFotoid() {
        return fotoid;
    }

    public void setFotoid(String fotoid) {
        this.fotoid = fotoid;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getCaracteristicas() {
        return caracteristicas;
    }

    public void setCaracteristicas(String caracteristicas) {
        this.caracteristicas = caracteristicas;
    }

    public String getIncluye() {
        return incluye;
    }

    public void setIncluye(String incluye) {
        this.incluye = incluye;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

}
