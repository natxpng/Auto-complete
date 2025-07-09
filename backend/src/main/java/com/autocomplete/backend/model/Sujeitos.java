package com.autocomplete.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Sujeitos {

    private List<String> jogadores;
    private List<String> times;
    private List<String> tecnicos;
    private List<String> estadios; // Campo 'estadios' adicionado

    // Getters e Setters

    public List<String> getJogadores() { return jogadores; }
    public void setJogadores(List<String> jogadores) { this.jogadores = jogadores; }

    public List<String> getTimes() { return times; }
    public void setTimes(List<String> times) { this.times = times; }

    public List<String> getTecnicos() { return tecnicos; }
    public void setTecnicos(List<String> tecnicos) { this.tecnicos = tecnicos; }

    public List<String> getEstadios() { return estadios; } // Getter para estadios
    public void setEstadios(List<String> estadios) { this.estadios = estadios; }
}