package com.autocomplete.backend.service;

import com.autocomplete.backend.config.DataLoader;
import com.autocomplete.backend.model.SuggestionData;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class FraseGeneratorService {

    private final SuggestionData data;
    private final List<String> todosSujeitos;

    public FraseGeneratorService(DataLoader loader) {
        this.data = loader.getSuggestionData();
        this.todosSujeitos = Stream.of(
                data.getSujeitos().getJogadores(),
                data.getSujeitos().getTimes(),
                data.getSujeitos().getTecnicos(),
                data.getSujeitos().getEstadios()
        ).flatMap(Collection::stream).collect(Collectors.toList());
    }

    public List<String> gerarSugestoes(String termo) {
        if (termo == null || termo.trim().isEmpty()) {
            return Collections.emptyList();
        }

        final String termoLimpo = termo.trim().toLowerCase(Locale.ROOT);
        String[] tokens = termoLimpo.split("\\s+");
        String primeiroToken = tokens[0];

        Optional<String> sujeitoPrincipalOpt = todosSujeitos.stream()
                .filter(s -> s.toLowerCase().startsWith(primeiroToken))
                .findFirst();

        if (sujeitoPrincipalOpt.isEmpty()) {
            return Collections.emptyList();
        }
        String sujeitoPrincipal = sujeitoPrincipalOpt.get();

        List<String> universoDeFrases = gerarUniversoParaSujeito(sujeitoPrincipal);

        List<String> sugestoesFiltradas = universoDeFrases;
        if (tokens.length > 1) {
            sugestoesFiltradas = universoDeFrases.stream()
                    .filter(frase -> {
                        return Arrays.stream(tokens)
                                .skip(1)
                                .allMatch(token -> frase.toLowerCase().contains(token));
                    })
                    .collect(Collectors.toList());
        }

        sugestoesFiltradas.sort((s1, s2) -> {
            boolean s1Starts = s1.toLowerCase().startsWith(termoLimpo);
            boolean s2Starts = s2.toLowerCase().startsWith(termoLimpo);
            if (s1Starts && !s2Starts) return -1;
            if (!s1Starts && s2Starts) return 1;
            return 0;
        });

        return sugestoesFiltradas.stream().limit(20).collect(Collectors.toList());
    }

    private List<String> gerarUniversoParaSujeito(String sujeito) {
        Set<String> universo = new LinkedHashSet<>();
        for (String modelo : data.getModelosDeFrase()) {
            for (String acao : data.getAcoes().subList(0, Math.min(3, data.getAcoes().size()))) {
                for (String objeto : data.getObjetos().get("gols").subList(0, 1)) {
                    String frase = modelo
                            .replace("{sujeito}", sujeito)
                            .replace("{acao}", acao)
                            .replace("{objeto}", objeto)
                            .replace("{contexto_campeonato}", data.getContextos().get("campeonatos").get(0))
                            .replace("{contexto_fase}", data.getContextos().get("fases").get(0))
                            .replace("{contexto_local}", data.getContextos().get("locais").get(0))
                            .replace("{tempo}", data.getTempos().get(0))
                            .replace("{conectivo}", data.getConectivos().get(0));

                    frase = frase.substring(0, 1).toUpperCase() + frase.substring(1);
                    universo.add(frase.trim().replaceAll(" +", " "));
                }
            }
        }
        return new ArrayList<>(universo);
    }
}