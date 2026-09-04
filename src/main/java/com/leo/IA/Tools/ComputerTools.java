package com.leo.IA.Tools;

import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;

@Component
public class ComputerTools {

    @Tool("Abre programas no computador")
    public String abrirPrograma(String programa) {

        try {

            switch (programa.toLowerCase()) {

                case "intellij":
                    Runtime.getRuntime().exec(
                            "C:\\Program Files\\JetBrains\\IntelliJ IDEA Community Edition 2025.2\\bin\\idea64.exe"
                    );
                    break;

                case "chrome":
                    Runtime.getRuntime().exec(
                            "C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe"
                    );
                    break;

                case "spotify":
                    Runtime.getRuntime().exec(
                            "explorer.exe shell:AppsFolder\\SpotifyAB.SpotifyMusic_zpdnekdrzrea0!Spotify"
                    );
                    break;

                default:
                    return "Programa não encontrado";
            }

            return programa + " aberto com sucesso";

        } catch (Exception e) {
            return "Erro ao abrir programa: " + e.getMessage();
        }
    }
}