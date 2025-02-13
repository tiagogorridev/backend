package com.timetracker.sistema_gerenciamento.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "projetos")  // Este é o nome da tabela no banco de dados
public class Projeto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String descricao;

    @Column(name = "horas_estimadas")
    private BigDecimal horasEstimadas;  // Alterado para BigDecimal

    @Column(name = "custo_estimado")
    private BigDecimal custoEstimado;

    @Temporal(TemporalType.DATE)  // Especificando que a data deve ser tratada como Date
    private Date dataInicio;

    @Temporal(TemporalType.DATE)  // Especificando que a data deve ser tratada como Date
    private Date dataFim;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Enumerated(EnumType.STRING)
    private Prioridade prioridade;

    @ManyToOne
    @JoinColumn(name = "id_usuario_responsavel")
    private Usuario usuarioResponsavel;

    // Getters e Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getHorasEstimadas() {
        return horasEstimadas;
    }

    public void setHorasEstimadas(BigDecimal horasEstimadas) {
        this.horasEstimadas = horasEstimadas;
    }

    public BigDecimal getCustoEstimado() {
        return custoEstimado;
    }

    public void setCustoEstimado(BigDecimal custoEstimado) {
        this.custoEstimado = custoEstimado;
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public Date getDataFim() {
        return dataFim;
    }

    public void setDataFim(Date dataFim) {
        this.dataFim = dataFim;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Prioridade getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(Prioridade prioridade) {
        this.prioridade = prioridade;
    }

    public Usuario getUsuarioResponsavel() {
        return usuarioResponsavel;
    }

    public void setUsuarioResponsavel(Usuario usuarioResponsavel) {
        this.usuarioResponsavel = usuarioResponsavel;
    }
}