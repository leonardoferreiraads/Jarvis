package com.leo.IA.Tools;

import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class BrowserTools {

    private final Map<String, String> sites = Map.of(
            "youtube", "https://www.youtube.com",
            "fiap", "https://on.fiap.com.br",
            "linkedin", "https://linkedin.com",
            "github", "https://github.com",
            "claude", "https://claude.ai",
            "chat gpt", "https://chatgpt.com",
            "whatsapp", "https://web.whatsapp.com",
            "instagram", "https://www.instagram.com/"
    );

    @Tool("Abre um site no navegador. Recebe APENAS o nome do site (ex: 'youtube', 'github', 'fiap'). NÃO passar URLs completas.")
    public String abrirSite(String site) {
        try {
            String url = sites.getOrDefault(
                    site.toLowerCase(),
                    "https://www.google.com/search?q=" + site
            );
            Runtime.getRuntime().exec("cmd /c start " + url);
            return "Site " + site + " aberto no navegador!";
        } catch (Exception e) {
            return "Erro ao abrir o site: " + e.getMessage();
        }
    }
}

