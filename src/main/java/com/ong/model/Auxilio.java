package com.ong.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "auxilios")
public class Auxilio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O tipo de auxílio é obrigatório")
    private String tipoAuxilio;

    private String descricao;

    @NotNull(message = "O valor estimado é obrigatório")
    @PositiveOrZero(message = "O valor estimado não pode ser negativo")
    private BigDecimal valorEstimado;

    @NotNull(message = "A data de entrega é obrigatória")
    private LocalDate dataEntrega;

    @ManyToOne
    @JoinColumn(name = "pessoa_id")
    private PessoaCarente pessoa;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTipoAuxilio() {
        return tipoAuxilio;
    }

    public void setTipoAuxilio(String tipoAuxilio) {
        this.tipoAuxilio = tipoAuxilio;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getValorEstimado() {
        return valorEstimado;
    }

    public void setValorEstimado(BigDecimal valorEstimado) {
        this.valorEstimado = valorEstimado;
    }

    public LocalDate getDataEntrega() {
        return dataEntrega;
    }

    public void setDataEntrega(LocalDate dataEntrega) {
        this.dataEntrega = dataEntrega;
    }

    public PessoaCarente getPessoa() {
        return pessoa;
    }

    public void setPessoa(PessoaCarente pessoa) {
        this.pessoa = pessoa;
    }
}
