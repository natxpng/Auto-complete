package com.autocomplete.backend.resolver;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class QueryResolver {

    private final List<String> suggestions;

    public QueryResolver() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        InputStream inputStream = getClass().getResourceAsStream("/suggestions.json");
        this.suggestions = mapper.readValue(inputStream, new TypeReference<List<String>>() {});
    }

    @QueryMapping
    public List<String> suggestions(@Argument String term) {
        if (term.length() < 4) return List.of();
        return suggestions.stream()
                .filter(s -> s.toLowerCase().contains(term.toLowerCase()))
                .limit(20)
                .collect(Collectors.toList());
    }
}
