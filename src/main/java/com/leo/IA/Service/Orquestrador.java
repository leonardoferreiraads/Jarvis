package com.leo.IA.Service;

import com.leo.IA.Tools.JarvisTools;
import com.leo.IA.Tools.LembreteTools;
import com.leo.IA.Tools.WeatherTools;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.springframework.stereotype.Component;

@Component
public class Orquestrador {

    private final Assistente.AgenteWeb agenteWeb;
    private final Assistente.AgenteClima agenteClima;
    private final Assistente.AgenteRAG agenteRAG;
    private final Assistente.AgenteLembretes agenteLembretes;

    public Orquestrador(
            ChatLanguageModel model,
            JarvisTools jarvisTools,
            WeatherTools weatherTools,
            LembreteTools lembreteTools,
            InMemoryEmbeddingStore embeddingStore
    ) {
        ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.from(embeddingStore);

        this.agenteWeb = AiServices.builder(Assistente.AgenteWeb.class)
                .chatLanguageModel(model)
                .tools(jarvisTools)
                .build();

        this.agenteClima = AiServices.builder(Assistente.AgenteClima.class)
                .chatLanguageModel(model)
                .tools(weatherTools)
                .build();

        this.agenteRAG = AiServices.builder(Assistente.AgenteRAG.class)
                .chatLanguageModel(model)
                .contentRetriever(contentRetriever)
                .build();

        this.agenteLembretes = AiServices.builder(Assistente.AgenteLembretes.class)
                .chatLanguageModel(model)
                .tools(lembreteTools)
                .build();
    }

    public String rotear(String pergunta) {
        String lower = pergunta.toLowerCase();

        if (lower.contains("clima") || lower.contains("tempo") ||
                lower.contains("temperatura") || lower.contains("chuva") ||
                lower.contains("frio") || lower.contains("calor")) {
            return agenteClima.chat(pergunta);
        }

        if (lower.contains("lembrete") || lower.contains("lembra") ||
                lower.contains("agenda") || lower.contains("tarefa")) {
            return agenteLembretes.chat(pergunta);
        }

        if (lower.contains("pesquisa") || lower.contains("busca") ||
                lower.contains("notícia") || lower.contains("internet") ||
                lower.contains("pesquise") || lower.contains("busque")) {
            return agenteWeb.chat(pergunta);
        }

        // padrão: RAG (documentos pessoais)
        return agenteRAG.chat(pergunta);
    }
}

