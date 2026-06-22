package com.escola.eventos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "eventos")
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Informe o nome do evento.")
    @Column(nullable = false)
    private String nome;

    @NotBlank(message = "Informe o local do evento.")
    @Column(nullable = false)
    private String local;

    @NotBlank(message = "Informe a data do evento.")
    @Column(nullable = false)
    private String data;

    @NotNull(message = "Informe a capacidade do evento.")
    @Min(value = 1, message = "A capacidade deve ser de pelo menos 1 pessoa.")
    @Column(nullable = false)
    private Integer capacidade;

    public Evento() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getLocal() { return local; }
    public void setLocal(String local) { this.local = local; }

    public String getData() { return data; }
    public void setData(String data) { this.data = data; }

    public Integer getCapacidade() { return capacidade; }
    public void setCapacidade(Integer capacidade) { this.capacidade = capacidade; }
}
