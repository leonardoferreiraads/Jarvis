package com.leo.IA;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;

@Configuration
public class AppConfig {

    @Value("${groq.api.key}")
    private String apiKey;

    @Bean
    public ChatLanguageModel chatLanguageModel() {
        return OpenAiChatModel.builder()
                .baseUrl("https://api.groq.com/openai/v1")
                .apiKey(apiKey)
                .temperature(0.2)
                .modelName("openai/gpt-oss-20b")
                .build();
    }

    @Bean
    public InMemoryEmbeddingStore embeddingStore() {
        InMemoryEmbeddingStore store = new InMemoryEmbeddingStore<>();
        try {
            List<Document> documentos = List.of(
                    FileSystemDocumentLoader.loadDocument(
                            Path.of(AppConfig.class.getClassLoader().getResource("notas.txt").toURI())
                    ),
                    FileSystemDocumentLoader.loadDocument(
                            Path.of(AppConfig.class.getClassLoader().getResource("safra.txt").toURI())
                    ),
                    FileSystemDocumentLoader.loadDocument(
                            Path.of(AppConfig.class.getClassLoader().getResource("sobre_mim.txt").toURI())
                    )
            );
            EmbeddingStoreIngestor.ingest(documentos, store);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return store;
    }
}