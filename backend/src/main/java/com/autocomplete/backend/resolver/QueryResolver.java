package com.autocomplete.backend.resolver;

import com.autocomplete.backend.service.FraseGeneratorService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class QueryResolver {

    private final FraseGeneratorService generator;

    public QueryResolver(FraseGeneratorService generator) {
        this.generator = generator;
    }

    @QueryMapping
    public List<String> suggestions(@Argument String term) {
        return generator.gerarSugestoes(term);
    }
}
