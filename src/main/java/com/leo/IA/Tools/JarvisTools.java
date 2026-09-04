package com.leo.IA.Tools;

import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.web.search.WebSearchEngine;
import dev.langchain4j.web.search.tavily.TavilyWebSearchEngine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JarvisTools {
    @Value("${tavily.api.key}")
    private String tavilyKey;

    @Tool("Busca informações atualizadas sobre tecnologia, linguagens, frameworks, ferramentas de desenvolvimento e vagas")
    public String buscarInfoTech(String tema) {
        WebSearchEngine searchEngine = TavilyWebSearchEngine.builder()
                .apiKey(tavilyKey)
                .build();

        return searchEngine.search(tema).results()
                .stream()
                .limit(5)
                .map(r -> "• " + r.title() + "\n  " + r.url() + "\n  " + r.snippet())
                .collect(java.util.stream.Collectors.joining("\n\n"));
    }
}
