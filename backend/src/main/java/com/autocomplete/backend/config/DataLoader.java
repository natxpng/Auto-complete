package com.autocomplete.backend.config;

import com.autocomplete.backend.model.SuggestionData;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class DataLoader {
    private SuggestionData suggestionData;

    @PostConstruct
    public void init() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        InputStream inputStream = getClass().getResourceAsStream("/suggestions.json");
        this.suggestionData = mapper.readValue(inputStream, SuggestionData.class);
    }

    public SuggestionData getSuggestionData() {
        return suggestionData;
    }
}
