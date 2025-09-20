package com.utpsistemas.distribuidoraavesservice.pedido.enums;


public enum EstadoPedidoEnum {

    PENDIENTE(1, "Pendiente"),
    POR_CONFIRMAR(2, "Por confirmar"),
    EN_COBRANZA(3, "En cobranza"),
    PARCIAL(4, "Parcial"),
    COMPLETADO(5, "Completado"),
    ANULADO(6, "Anulado");

    private final int id;
    private final String descripcion;

    EstadoPedidoEnum(int id, String descripcion) {
        this.id = id;
        this.descripcion = descripcion;
    }

    public int getId() { return id; }
    public String getDescripcion() { return descripcion; }
}