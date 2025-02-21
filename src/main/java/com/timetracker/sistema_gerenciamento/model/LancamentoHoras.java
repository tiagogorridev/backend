package com.timetracker.sistema_gerenciamento.model;

import jakarta.persistence.*;

import java.time.LocalDate;

import java.time.LocalTime;

@Entity
@Table(name = "lancamentos_horas")
public class LancamentoHoras {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_tarefa", nullable = false)
    private Tarefa tarefa;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_projeto", nullable = false)
    private Projeto projeto;

    @Column(nullable = false)
    private Double horas;

    @Column(nullable = false)
    private LocalDate data;

    @Column(nullable = false)
    private LocalTime horaInicio;

    @Column(nullable = false)
    private LocalTime horaFim;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(columnDefinition = "TIME DEFAULT '00:00:00'")
    private LocalTime totalDia;

    @Column(columnDefinition = "TIME DEFAULT '00:00:00'")
    private LocalTime totalSemana;

    @Column(columnDefinition = "TIME DEFAULT '00:00:00'")
    private LocalTime totalMes;

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Tarefa getTarefa() {
        return tarefa;
    }

    public void setTarefa(Tarefa tarefa) {
        this.tarefa = tarefa;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Projeto getProjeto() {
        return projeto;
    }

    public void setProjeto(Projeto projeto) {
        this.projeto = projeto;
    }

    public Double getHoras() {
        return horas;
    }

    public void setHoras(Double horas) {
        this.horas = horas;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFim() {
        return horaFim;
    }

    public void setHoraFim(LocalTime horaFim) {
        this.horaFim = horaFim;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalTime getTotalDia() {
        return totalDia;
    }

    public void setTotalDia(LocalTime totalDia) {
        this.totalDia = totalDia;
    }

    public LocalTime getTotalSemana() {
        return totalSemana;
    }

    public void setTotalSemana(LocalTime totalSemana) {
        this.totalSemana = totalSemana;
    }

    public LocalTime getTotalMes() {
        return totalMes;
    }

    public void setTotalMes(LocalTime totalMes) {
        this.totalMes = totalMes;
    }
}