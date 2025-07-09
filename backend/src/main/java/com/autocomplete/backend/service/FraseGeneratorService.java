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
    private final Random random = new Random();

    public FraseGeneratorService(DataLoader loader) {
        this.data = loader.getSuggestionData();
        // Pré-carrega todos os sujeitos para otimização
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

        // 1. ENCONTRA O SUJEITO PRINCIPAL (usando apenas o primeiro token)
        List<String> sujeitosCandidatos = todosSujeitos.stream()
                .filter(s -> s.toLowerCase().startsWith(tokens[0]))
                .limit(5)
                .collect(Collectors.toList());

        if (sujeitosCandidatos.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. ENCONTRA COMPONENTES SECUNDÁRIOS (usando os outros tokens)
        Map<String, List<String>> componentesEncontrados = encontrarComponentesSecundarios(tokens);

        Set<String> sugestoes = new LinkedHashSet<>();
        int tentativas = 0;
        while (sugestoes.size() < 20 && tentativas < 100) {
            String sujeito = getRandomElement(sujeitosCandidatos);
            String modelo = getRandomElement(data.getModelosDeFrase());

            if (sujeito == null || modelo == null) continue;

            String frase = preencherModelo(modelo, sujeito, componentesEncontrados);
            frase = frase.substring(0, 1).toUpperCase() + frase.substring(1);
            sugestoes.add(frase.trim().replaceAll(" +", " "));
            tentativas++;
        }

        List<String> resultadoFinal = new ArrayList<>(sugestoes);

        // 3. NOVA ORDENAÇÃO HÍBRIDA
        resultadoFinal.sort((s1, s2) -> {
            // REGRA 1: Prioridade máxima para quem começa com o termo completo
            boolean s1Starts = s1.toLowerCase().startsWith(termoLimpo);
            boolean s2Starts = s2.toLowerCase().startsWith(termoLimpo);
            if (s1Starts && !s2Starts) return -1;
            if (!s1Starts && s2Starts) return 1;

            // REGRA 2 (DESEMPATE): Score baseado na quantidade de palavras da busca que a frase contém
            long s1Score = Arrays.stream(tokens).filter(token -> s1.toLowerCase().contains(token)).count();
            long s2Score = Arrays.stream(tokens).filter(token -> s2.toLowerCase().contains(token)).count();
            if (s1Score != s2Score) {
                return Long.compare(s2Score, s1Score); // Maior score primeiro
            }

            // Se tudo for igual, não muda a ordem
            return 0;
        });

        return resultadoFinal;
    }

    private Map<String, List<String>> encontrarComponentesSecundarios(String[] tokens) {
        Map<String, List<String>> encontrados = new HashMap<>();
        if (tokens.length <= 1) {
            return encontrados;
        }

        List<String> outrosTokens = Arrays.asList(tokens).subList(1, tokens.length);

        for (String token : outrosTokens) {
            if (token.isEmpty()) continue;
            // Procura em ações
            for (String acao : data.getAcoes()) {
                if (acao.toLowerCase().contains(token)) {
                    encontrados.computeIfAbsent("acoes", k -> new ArrayList<>()).add(acao);
                }
            }
            // Procura em objetos
            for (String objeto : data.getObjetos().values().stream().flatMap(Collection::stream).collect(Collectors.toList())) {
                if (objeto.toLowerCase().contains(token)) {
                    encontrados.computeIfAbsent("objetos", k -> new ArrayList<>()).add(objeto);
                }
            }
        }
        return encontrados;
    }

    private String preencherModelo(String modelo, String sujeito, Map<String, List<String>> componentesEncontrados) {
        // Lógica de preenchimento que prioriza os componentes encontrados
        String acao = getComponenteParaFrase(componentesEncontrados.get("acoes"), data.getAcoes());
        String objeto = getComponenteParaFrase(componentesEncontrados.get("objetos"), data.getObjetos());

        return modelo
                .replace("{sujeito}", sujeito)
                .replace("{acao}", acao)
                .replace("{objeto}", objeto)
                .replace("{contexto_campeonato}", getRandomElementFromList(data.getContextos(), "campeonatos"))
                .replace("{contexto_fase}", getRandomElementFromList(data.getContextos(), "fases"))
                .replace("{contexto_local}", getRandomElementFromList(data.getContextos(), "locais"))
                .replace("{tempo}", getRandomElement(data.getTempos()))
                .replace("{conectivo}", getRandomElement(data.getConectivos()));
    }

    private String getComponenteParaFrase(List<String> encontrados, List<String> listaCompleta) {
        if (encontrados != null && !encontrados.isEmpty()) {
            return getRandomElement(encontrados);
        }
        return getRandomElement(listaCompleta);
    }

    private String getComponenteParaFrase(List<String> encontrados, Map<String, List<String>> mapaCompleto) {
        if (encontrados != null && !encontrados.isEmpty()) {
            return getRandomElement(encontrados);
        }
        List<String> listaCompleta = mapaCompleto.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
        return getRandomElement(listaCompleta);
    }

    private String getRandomElement(List<String> list) {
        if (list == null || list.isEmpty()) return "";
        return list.get(random.nextInt(list.size()));
    }

    private String getRandomElementFromList(Map<String, List<String>> categoryMap, String key) {
        if (categoryMap == null || !categoryMap.containsKey(key)) return "";
        return getRandomElement(categoryMap.get(key));
    }
}