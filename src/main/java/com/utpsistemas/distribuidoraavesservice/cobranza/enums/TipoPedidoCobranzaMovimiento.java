package com.utpsistemas.distribuidoraavesservice.cobranza.enums;

public enum TipoPedidoCobranzaMovimiento {
    DESCUENTO(1),
    PAGO(2);

    private final int codigo;
    TipoPedidoCobranzaMovimiento(int codigo) { this.codigo = codigo; }
    public int getCodigo() { return codigo; }

    public static TipoPedidoCobranzaMovimiento fromCodigo(Integer codigo) {
        if (codigo == null) return null;
        for (var t : values()) if (t.codigo == codigo) return t;
        throw new IllegalArgumentException("Código de tipo inválido: " + codigo);
    }
}