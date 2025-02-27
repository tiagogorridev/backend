package com.timetracker.sistema_gerenciamento.model;

import com.timetracker.sistema_gerenciamento.service.ProjetoService;
import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tarefas")
public class Tarefa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String descricao;

    private LocalDate dataInicio;
    private LocalDate dataFim;

    @Enumerated(EnumType.STRING)
    private StatusTarefa status;

    @ManyToOne
    @JoinColumn(name = "id_projeto")
    private Projeto projeto;

    @ManyToOne
    @JoinColumn(name = "id_usuario_responsavel")
    private Usuario usuarioResponsavel;

    private LocalDateTime dataCriacao = LocalDateTime.now();

    @Column(name = "horas_estimadas", precision = 10, scale = 2)
    private BigDecimal horasEstimadas;

    @Column(name = "tempo_registrado", precision = 10, scale = 2)
    private BigDecimal tempoRegistrado = BigDecimal.ZERO;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

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

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public void setDataInicio(LocalDate dataInicio) {
        if (projeto != null) {
            if (dataInicio.isBefore(projeto.getDataInicio()) || dataInicio.isAfter(projeto.getDataFim())) {
                throw new IllegalArgumentException("A data de início da tarefa deve estar dentro do intervalo do projeto.");
            }
        }

        this.dataInicio = dataInicio;
    }

    public void setDataFim(LocalDate dataFim) {
        if (dataFim.isBefore(dataInicio)) {
            throw new IllegalArgumentException("A data de fim não pode ser anterior à data de início.");
        }

        if (projeto != null) {
            if (dataFim.isBefore(projeto.getDataInicio()) || dataFim.isAfter(projeto.getDataFim())) {
                throw new IllegalArgumentException("A data de fim da tarefa deve estar dentro do intervalo do projeto.");
            }
        }

        this.dataFim = dataFim;
    }

    public StatusTarefa getStatus() {
        return status;
    }

    public void setStatus(StatusTarefa status) {
        this.status = status;
    }

    public Projeto getProjeto() {
        return projeto;
    }

    public void setProjeto(Projeto projeto) {
        this.projeto = projeto;
    }

    public Usuario getUsuarioResponsavel() {
        return usuarioResponsavel;
    }

    public void setUsuarioResponsavel(Usuario usuarioResponsavel) {
        this.usuarioResponsavel = usuarioResponsavel;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public BigDecimal getHorasEstimadas() {
        return horasEstimadas;
    }

    public void setHorasEstimadas(BigDecimal horasEstimadas) {
        this.horasEstimadas = horasEstimadas;
    }

    public BigDecimal getTempoRegistrado() {
        return tempoRegistrado;
    }

    public void setTempoRegistrado(BigDecimal tempoRegistrado) {
        this.tempoRegistrado = tempoRegistrado;
    }

    public void adicionarTempoRegistrado(BigDecimal horas) {
        if (this.tempoRegistrado == null) {
            this.tempoRegistrado = BigDecimal.ZERO;
        }
        this.tempoRegistrado = this.tempoRegistrado.add(horas);
    }
}