package com.ong.model;

public enum TipoAuxilio {

    CESTA_BASICA("Cesta Básica"),
    MATERIAL_ESCOLAR("Material Escolar"),
    MEDICAMENTOS("Medicamentos"),
    ROUPA("Roupa"),
    VALE_TRANSPORTE("Vale Transporte"),
    OUTROS("Outros");

    private final String descricao;

    TipoAuxilio(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
