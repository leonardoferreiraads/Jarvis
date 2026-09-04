package com.leo.IA.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.leo.IA.Tools.*;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import com.leo.IA.DTO.AudioDTO;
import com.leo.IA.DTO.ImagemDTO;
import com.leo.IA.DTO.PdfDTO;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import dev.langchain4j.model.chat.ChatLanguageModel;
import org.springframework.web.client.RestTemplate;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;

@Service
public class IAService {
    @Value("${groq.api.key}")
    private String apiKey;
    private final ChatMemory memory = MessageWindowChatMemory.withMaxMessages(10);

    @Autowired
    private JarvisTools jarvisTools;
    @Autowired
    private WeatherTools weatherTools;
    @Autowired
    private LembreteTools lembreteTools;
    @Autowired
    private GmailTools gmailTools;
    @Autowired
    private ComputerTools computerTools;
    @Autowired
    private BrowserTools browserTools;
    @Autowired
    private Orquestrador orquestrador;


    private void validarInput(String pergunta) {
        if (pergunta == null || pergunta.isBlank()) {
            throw new IllegalArgumentException("Pergunta não pode ser vazia.");
        }
        if (pergunta.length() > 2000) {
            throw new IllegalArgumentException("Pergunta muito longa.");
        }

        List<String> padroesMaliciosos = List.of(
                "ignore suas instruções",
                "ignore as instruções anteriores",
                "ignore previous instructions",
                "you are now",
                "esqueça tudo",
                "forget everything",
                "act as",
                "aja como",
                "novo prompt",
                "new prompt",
                "system:",
                "<|im_start|>",
                "###instrução"
        );

        String perguntaLower = pergunta.toLowerCase();
        for (String padrao : padroesMaliciosos) {
            if (perguntaLower.contains(padrao)) {
                throw new IllegalArgumentException("Mensagem bloqueada: conteúdo não permitido.");
            }
        }
    }


    public String perguntaIA(String pergunta) {
        validarInput(pergunta);
        return orquestrador.rotear(pergunta);
    }

    public String perguntaComImagem(ImagemDTO imagem) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String body = """
        {
            "model": "meta-llama/llama-4-scout-17b-16e-instruct",
            "messages": [
                {
                    "role": "user",
                    "content": [
                        {
                            "type": "text",
                            "text": "%s"
                        },
                        {
                            "type": "image_url",
                            "image_url": {
                                "url": "data:%s;base64,%s"
                            }
                        }
                    ]
                }
            ]
        }
    """.formatted(imagem.getPergunta(), imagem.getMimeType(), imagem.getImagemBase64());

        HttpEntity<String> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(
                "https://api.groq.com/openai/v1/chat/completions",
                request,
                String.class
        );

        return response.getBody();
    }

    public String perguntaComPdf(PdfDTO pdf) throws IOException {
        // 1. base64 → bytes → InputStream
        byte[] pdfBytes = Base64.getDecoder().decode(pdf.getPdf());
        PDDocument document = Loader.loadPDF(pdfBytes);

        // 2. PDFBox extrai o texto
        PDFTextStripper stripper = new PDFTextStripper();
        String textoPdf = stripper.getText(document);
        document.close();

        // 3. Monta o prompt com o texto extraído
        String prompt = "Com base no seguinte conteúdo de um PDF:\n\n" + textoPdf + "\n\nResponda: " + pdf.getPergunta();

        // 4. Manda para o modelo
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String body = """
    {
        "model": "qwen/qwen3.6-27b",
        "messages": [
            {
                "role": "user",
                "content": "%s"
            }
        ]
    }
    """.formatted(prompt.replace("\"", "\\\"").replace("\r\n", "\\n").replace("\r", "\\n").replace("\n", "\\n"));

        HttpEntity<String> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(
                "https://api.groq.com/openai/v1/chat/completions",
                request,
                String.class
        );

        return response.getBody();
    }

    public String perguntaComAudio(AudioDTO audioDTO) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        byte[] audioBytes = Base64.getDecoder().decode(audioDTO.getAudio());

        ByteArrayResource audioResource = new ByteArrayResource(audioBytes) {
            @Override
            public String getFilename() {
                return "audio.mp3"; // nome obrigatório, mesmo que fake
            }
        };
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", audioResource);
        body.add("model", "whisper-large-v3");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(
                "https://api.groq.com/openai/v1/audio/transcriptions",
                request,
                String.class
        );
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response.getBody());
        String textoTranscrito = root.get("text").asText();
        String prompt = "Com base no seguinte conteúdo de um áudio:\n\n" + textoTranscrito + "\n\nResponda: " + audioDTO.getPergunta();

        HttpHeaders headersLlama = new HttpHeaders();
        headersLlama.set("Authorization", "Bearer " + apiKey);
        headersLlama.setContentType(MediaType.APPLICATION_JSON);
        String bodyLlama = """
{
    "model": "qwen/qwen3.6-27b",
    "messages": [
        {
            "role": "user",
            "content": "%s"
        }
    ]
}
""".formatted(prompt.replace("\"", "\\\"").replace("\r\n", "\\n").replace("\r", "\\n").replace("\n", "\\n"));
        HttpEntity<String> request1 = new HttpEntity<>(bodyLlama, headersLlama);

        ResponseEntity<String> response1 = restTemplate.postForEntity(
                "https://api.groq.com/openai/v1/chat/completions",
                request1,
                String.class
        );

        return response1.getBody();
    }
}