package com.utpsistemas.distribuidoraavesservice.pedido.enums;

public enum TipoMerma {
    CT("conTripaKg"), ST("sinTripaKg"), NA("noAplicaKg");
    private final String json;
    TipoMerma(String json){ this.json=json; }
    @com.fasterxml.jackson.annotation.JsonValue public String toJson(){ return json; }
    @com.fasterxml.jackson.annotation.JsonCreator
    public static TipoMerma fromJson(String v){
        for (var t: values()) if (t.json.equals(v)) return t;
        throw new IllegalArgumentException("Valor inválido: " + v);
    }
}