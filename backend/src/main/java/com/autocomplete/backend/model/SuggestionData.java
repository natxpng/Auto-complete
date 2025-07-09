package com.autocomplete.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true) // Boa prática para ignorar campos não mapeados
public class SuggestionData {

    private Sujeitos sujeitos;
    private List<String> acoes;
    private Map<String, List<String>> objetos;
    private Map<String, List<String>> contextos;
    private List<String> tempos;
    private List<String> conectivos;
    private List<String> modelosDeFrase;

    // Getters e Setters para todos os campos

    public Sujeitos getSujeitos() { return sujeitos; }
    public void setSujeitos(Sujeitos sujeitos) { this.sujeitos = sujeitos; }

    public List<String> getAcoes() { return acoes; }
    public void setAcoes(List<String> acoes) { this.acoes = acoes; }

    public Map<String, List<String>> getObjetos() { return objetos; }
    public void setObjetos(Map<String, List<String>> objetos) { this.objetos = objetos; }

    public Map<String, List<String>> getContextos() { return contextos; }
    public void setContextos(Map<String, List<String>> contextos) { this.contextos = contextos; }

    public List<String> getTempos() { return tempos; }
    public void setTempos(List<String> tempos) { this.tempos = tempos; }

    public List<String> getConectivos() { return conectivos; }
    public void setConectivos(List<String> conectivos) { this.conectivos = conectivos; }

    public List<String> getModelosDeFrase() { return modelosDeFrase; }
    public void setModelosDeFrase(List<String> modelosDeFrase) { this.modelosDeFrase = modelosDeFrase; }
}